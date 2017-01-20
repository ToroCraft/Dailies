package net.torocraft.dailies;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.network.ProgressUpdater;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.Reward;
import scala.actors.threadpool.Arrays;

public class BaileyInventory implements IInventory {

	private static final int SUBMIT_ITEM_COUNT = 3;
	private static final int OUTPUT_ITEM_COUNT = 1;
	private static final int REWARD_OUTPUT_INDEX = 3;
	private static final int TOTAL_SLOT_COUNT = SUBMIT_ITEM_COUNT + OUTPUT_ITEM_COUNT;
	
	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOT_COUNT];
	
	private ItemStack lastModifiedStack = ItemStack.field_190927_a;
	private int lastModifiedIndex = 0;
	
	private EntityPlayer player = null;
	private IDailiesCapability playerDailiesCapability = null;
	private Set<DailyQuest> acceptedQuests;
	
	public BaileyInventory() {
		clear();
	}
	
	@Override
	public String getName() {
		return "Bailey's Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public int getSizeInventory() {
		return itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return itemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack slotStack = getStackInSlot(index);
		if(slotStack == null) {
			return ItemStack.field_190927_a;
		}
		
		ItemStack stackRemoved;
		if(slotStack.func_190916_E() <= count) {
			stackRemoved = slotStack;
			setInventorySlotContents(index, ItemStack.field_190927_a);
			
		} else {
			stackRemoved = slotStack.splitStack(count);
			if(slotStack.func_190916_E() == 0) {
				setInventorySlotContents(index, ItemStack.field_190927_a);
			}
		}
		
		markDirty();
		
		return stackRemoved;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getStackInSlot(index);
		if(itemStack != null) {
			setInventorySlotContents(index, ItemStack.field_190927_a);
		}
		
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		itemStacks[index] = stack;
		if(stack != null && stack.func_190916_E() > getInventoryStackLimit()) {
			stack.func_190920_e(getInventoryStackLimit());
		}
		
		if(stack != null && index != REWARD_OUTPUT_INDEX) {
			this.lastModifiedIndex = index;
		}
		
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	
	@Override
	public void openInventory(EntityPlayer player) {
		this.player = player;
		this.playerDailiesCapability = player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
		for(int x = 0; x < getSizeInventory(); x++) {
			if(itemStacks[x] != null) {
				player.dropItem(itemStacks[x], false);
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		Arrays.fill(itemStacks, ItemStack.field_190927_a);
	}
	
	@Override
	public void markDirty() {
		
	}
	
	public void checkForReward() {
		if(playerDailiesCapability == null) {
			return;
		}
		
		lastModifiedStack = this.itemStacks[lastModifiedIndex];
		acceptedQuests = playerDailiesCapability.getAcceptedQuests();
		
		if (DailiesMod.devMode) {
			logItemStack(lastModifiedStack);
		}
		
		if(canSearchForReward()) {
			int itemId = Item.getIdFromItem(lastModifiedStack.getItem());
			int subType = lastModifiedStack.getMetadata();
			DailyQuest quest = checkForMatchingQuest(itemId, subType);
			
			if(quest != null) {
				updateQuestProgress(quest, lastModifiedStack, lastModifiedIndex);
			}
		}
	}
	
	public static void logItemStack(ItemStack stack) {
		if (stack == null) {
			return;
		}
		System.out.println("LOGGING ITEM STACK");
		System.out.println("type:" + Item.getIdFromItem(stack.getItem()));
		System.out.println("subType:" + stack.getMetadata());
		System.out.println("NBT: " + String.valueOf(stack.getTagCompound()));
	}
	
	private DailyQuest checkForMatchingQuest(int itemId, int itemSubType) {
		DailyQuest quest = null;
		
		for(DailyQuest q : acceptedQuests) {
			if(q.isGatherQuest() && itemId == q.target.type && !q.rewardFulfilled && q.target.subType == itemSubType) {
				quest = q;
			}
		}
		
		return quest;
	}
	
	private void updateQuestProgress(DailyQuest quest, ItemStack stack, int index) {
		int remainingTarget = quest.target.quantity - quest.progress;
		int leftOver = stack.func_190916_E() - remainingTarget;
		
		if (leftOver < 0) {
			leftOver = 0;
		}
		
		quest.progress += stack.func_190916_E() - leftOver;
		
		if(quest.isComplete()) {
			quest.rewardFulfilled = true;
			playerDailiesCapability.completeQuest(player, quest);
			buildReward(quest.reward);
		} else {
			syncProgress(quest.id, quest.progress);
		}
		
		if (leftOver > 0) {
			stack.func_190920_e(leftOver);
			setInventorySlotContents(index, stack);
		} else {
			removeStackFromSlot(index);
		}
		
		updateClient(player);
	}
	
	private void buildReward(Reward reward) {
		Item rewardItem = Item.getItemById(reward.type);
		ItemStack rewardStack = new ItemStack(rewardItem, reward.quantity);
		
		if (reward.subType > 0) {
			rewardStack.setItemDamage(reward.subType);
		}
		
		if (reward.nbt != null) {
			try {
				rewardStack.setTagCompound(JsonToNBT.getTagFromJson(reward.nbt));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		setInventorySlotContents(REWARD_OUTPUT_INDEX, rewardStack);
	}
	
	private void syncProgress(final String questId, final int progress) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					new ProgressUpdater(player, questId, progress).update();
				} catch (DailiesException e) {
					player.addChatMessage(e.getMessageAsTextComponent());
				}
			}

		}).start();
	}
	
	private void updateClient(final EntityPlayer player) {
		playerDailiesCapability.sendAcceptedQuestsToClient(player);
	}
	
	private boolean rewardStackExists() {
		if(itemStacks[REWARD_OUTPUT_INDEX] != null) {
			return true;
		}
		return false;
	}
	
	private boolean canSearchForReward() {
		if(rewardStackExists() || lastModifiedStack == null || acceptedQuests == null || acceptedQuests.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean func_191420_l() {
		for (ItemStack itemstack : this.itemStacks) {
            if (!itemstack.func_190926_b()) {
                return false;
            }
        }

        return true;
	}
}
