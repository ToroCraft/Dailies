package net.torocraft.dailies;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.Reward;
import scala.actors.threadpool.Arrays;

public class BaileyInventory implements IInventory {

	private static final int SUBMIT_ITEM_COUNT = 3;
	private static final int OUTPUT_ITEM_COUNT = 1;
	private static final int REWARD_OUTPUT_INDEX = 3;
	private static final int TOTAL_SLOT_COUNT = SUBMIT_ITEM_COUNT + OUTPUT_ITEM_COUNT;
	
	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOT_COUNT];
	
	private ItemStack lastModifiedStack = null;
	private int lastModifiedIndex = 0;
	
	private EntityPlayer player = null;
	private IDailiesCapability playerDailiesCapability = null;
	private Set<DailyQuest> acceptedQuests;
	
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
			return null;
		}
		
		ItemStack stackRemoved;
		if(slotStack.stackSize <= count) {
			stackRemoved = slotStack;
			setInventorySlotContents(index, null);
			
		} else {
			stackRemoved = slotStack.splitStack(count);
			if(slotStack.stackSize == 0) {
				setInventorySlotContents(index, null);
			}
		}
		
		markDirty();
		
		return stackRemoved;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getStackInSlot(index);
		if(itemStack != null) {
			setInventorySlotContents(index, null);
		}
		
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		itemStacks[index] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
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
		Arrays.fill(itemStacks, null);
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
		
		if(canSearchForReward()) {
			int itemId = Item.getIdFromItem(lastModifiedStack.getItem());
			DailyQuest quest = checkForMatchingQuest(itemId);
			
			if(quest != null) {
				updateQuestProgress(quest, lastModifiedStack, lastModifiedIndex);
			}
		}
	}
	
	private DailyQuest checkForMatchingQuest(int itemId) {
		DailyQuest quest = null;
		
		for(DailyQuest q : acceptedQuests) {
			if(q.isGatherQuest() && itemId == q.target.type && !q.rewardFulfilled) { 
				quest = q;
			}
		}
		
		return quest;
	}
	
	private void updateQuestProgress(DailyQuest quest, ItemStack stack, int index) {
		int remainingTarget = quest.target.quantity - quest.progress;
		int leftOver = stack.stackSize - remainingTarget;
		
		if (leftOver < 0) {
			leftOver = 0;
		}
		
		quest.progress += stack.stackSize - leftOver;
		
		if(quest.isComplete()) {
			quest.rewardFulfilled = true;
			playerDailiesCapability.completeQuest(quest, player);
			buildReward(quest.reward);
			
		} else {
			syncProgress(player.getName(), quest.id, quest.progress);
		}
		
		if (leftOver > 0) {
			stack.stackSize = leftOver;
			setInventorySlotContents(index, stack);
		} else {
			removeStackFromSlot(index);
		}
		
		updateClient(player);
	}
	
	private void buildReward(Reward reward) {
		ItemStack rewardStack = new ItemStack(Item.getItemById(reward.type));
		rewardStack.stackSize = reward.quantity;
		setInventorySlotContents(REWARD_OUTPUT_INDEX, rewardStack);
	}
	
	private void syncProgress(final String username, final String questId, final int progress) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				new DailiesRequester().progressQuest(username, questId, progress);
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
}
