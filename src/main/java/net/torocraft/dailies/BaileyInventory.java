package net.torocraft.dailies;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.network.remote.ProgressUpdater;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.Reward;

public class BaileyInventory implements Container {

	private static final int SUBMIT_ITEM_COUNT = 3;
	private static final int OUTPUT_ITEM_COUNT = 1;
	private static final int REWARD_OUTPUT_INDEX = 3;
	private static final int TOTAL_SLOT_COUNT = SUBMIT_ITEM_COUNT + OUTPUT_ITEM_COUNT;
	
	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOT_COUNT];
	
	private ItemStack lastModifiedStack = ItemStack.EMPTY;
	private int lastModifiedIndex = 0;
	
	private Player player = null;
	private LazyOptional<IDailiesCapability> playerDailiesCapability;
	private Set<DailyQuest> acceptedQuests;
	
	public BaileyInventory() {
		clearContent();
	}


	public Component getDisplayName() {
		return Component.literal("Bailey's Inventory");
	}


	@Override
	public int getContainerSize() {
		return itemStacks.length;
	}

	@Override
	public ItemStack getItem(int index) {
		return itemStacks[index];
	}


	@Override
	public ItemStack removeItem(int index, int count) {
		if (count <= 0 || index < 0 || index >= itemStacks.length) {
			return ItemStack.EMPTY;
		}
		ItemStack stackInSlot = itemStacks[index];
		if (stackInSlot.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (stackInSlot.getCount() <= count) {
			ItemStack removed = stackInSlot;
			itemStacks[index] = ItemStack.EMPTY;
			setChanged();
			return removed;
		} else {
			ItemStack removed = stackInSlot.split(count);
			if (stackInSlot.getCount() == 0) {
				itemStacks[index] = ItemStack.EMPTY;
			}
			setChanged();
			return removed;
		}
	}


	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = itemStacks[index];
		if (!stack.isEmpty()) {
			itemStacks[index] = ItemStack.EMPTY;
			setChanged();
			return stack;
		}
		return ItemStack.EMPTY;
	}


	@Override
	public void setItem(int index, ItemStack stack) {
		itemStacks[index] = stack;
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}
		if (!stack.isEmpty() && index != REWARD_OUTPUT_INDEX) {
			this.lastModifiedIndex = index;
		}
		setChanged();
	}


	@Override
	public int getMaxStackSize() {
		return 64;
	}
	

	public void startOpen(Player player) {
		this.player = player;
		this.playerDailiesCapability = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null);
	}


	@Override
	public void stopOpen(Player player) {
		for (int x = 0; x < getContainerSize(); x++) {
			if (!itemStacks[x].isEmpty()) {
				player.drop(itemStacks[x], false);
			}
		}
	}


	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	/*@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}*/


	@Override
	public void clearContent() {
		Arrays.fill(itemStacks, ItemStack.EMPTY);
	}
	

	@Override
	public void setChanged() {
		// No-op or add logic if needed
	}


	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public void checkForReward() {
		if(playerDailiesCapability == null) {
			return;
		}
		
		lastModifiedStack = this.itemStacks[lastModifiedIndex];
		playerDailiesCapability.ifPresent((cap) -> acceptedQuests = cap.getAcceptedQuests());

		if (DailiesMod.devMode) {
			logItemStack(lastModifiedStack);
		}
		
		if(canSearchForReward()) {
			ResourceLocation itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(lastModifiedStack.getItem());
			int subType = lastModifiedStack.getDamageValue();
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
		System.out.println("type:" + net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()));
		//System.out.println("subType:" + stack.getMetadata());
		//System.out.println("NBT: " + String.valueOf(stack.getTagCompound()));
	}
	
	private DailyQuest checkForMatchingQuest(ResourceLocation itemId, int itemSubType) {
		DailyQuest quest = null;
		for (DailyQuest q : acceptedQuests) {
			if (q.isGatherQuest() && itemId.equals(q.target.type) && !q.rewardFulfilled && q.target.subType == itemSubType) {
				quest = q;
			}
		}
		return quest;
	}
	
	private void updateQuestProgress(DailyQuest quest, ItemStack stack, int index) {
		int remainingTarget = quest.target.quantity - quest.progress;
		int leftOver = stack.getCount() - remainingTarget;
		
		if (leftOver < 0) {
			leftOver = 0;
		}
		
		quest.progress += stack.getCount() - leftOver;
		
		if(quest.isComplete()) {
			quest.rewardFulfilled = true;
			playerDailiesCapability.ifPresent((cap) -> cap.completeQuest(player, quest));
			buildReward(quest.reward);
		} else {
			syncProgress(quest.id, quest.progress);
		}
		
		if (leftOver > 0) {
			stack.setCount(leftOver);
			setItem(index, stack);
		} else {
			removeItemNoUpdate(index);
		}
		
		updateClient(player);
	}
	
	private void buildReward(Reward reward) {
		// TODO: Fix this - using integer IDs for items is deprecated
		// Should use ResourceLocation strings instead of integers
		Item rewardItem = Item.byId(reward.type); // This will need to be updated
		ItemStack rewardStack = new ItemStack(rewardItem, reward.quantity);
		if (reward.subType > 0) {
			rewardStack.setDamageValue(reward.subType);
		}
		if (reward.nbt != null) {
			try {
				CompoundTag tag = TagParser.parseTag(reward.nbt);
				rewardStack.setTag(tag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setItem(REWARD_OUTPUT_INDEX, rewardStack);
	}
	
	private void syncProgress(final String questId, final int progress) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					new ProgressUpdater(player, questId, progress).update();
				} catch (DailiesException e) {
					//player.sendMessage(e.getMessageAsTextComponent());
				}
			}

		}).start();
	}
	
	private void updateClient(final Player player) {
		playerDailiesCapability.ifPresent((cap) -> cap.sendAcceptedQuestsToClient(player));
	}
	
	private boolean rewardStackExists() {
	return !itemStacks[REWARD_OUTPUT_INDEX].isEmpty();
	}
	
	private boolean canSearchForReward() {
	return !(rewardStackExists() || lastModifiedStack == null || acceptedQuests == null || acceptedQuests.isEmpty());
	}


	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
	}
}
