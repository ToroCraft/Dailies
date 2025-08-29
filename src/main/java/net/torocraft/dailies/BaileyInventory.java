package net.torocraft.dailies;

import java.util.Arrays;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.torocraft.dailies.attachments.DailiesAttachmentTypes;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.network.remote.ProgressUpdater;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.Reward;

public class BaileyInventory implements Container {

	private static final Logger LOGGER = LogManager.getLogger(DailiesMod.MODID + " BaileyInventory");
	private static final int SUBMIT_ITEM_COUNT = 3;
	private static final int OUTPUT_ITEM_COUNT = 1;
	private static final int REWARD_OUTPUT_INDEX = 3;
	private static final int TOTAL_SLOT_COUNT = SUBMIT_ITEM_COUNT + OUTPUT_ITEM_COUNT;
	
	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOT_COUNT];
	
	private ItemStack lastModifiedStack = ItemStack.EMPTY;
	private int lastModifiedIndex = 0;
	
	private Player player = null;
	private IDailiesCapability playerDailiesCapability;
	private Set<DailyQuest> acceptedQuests;
	
	public BaileyInventory() {
		clearContent();
	}

	/**
	 * Ensures capability setup is complete, particularly important for creative mode
	 */
	private IDailiesCapability ensureCapabilitySetup(Player player) {
		if (player == null) {
			return null;
		}
		
		// Get the dailies data using new attachment system
		return player.getData(DailiesAttachmentTypes.DAILIES_DATA);
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
	public void setItem(int index, @Nonnull ItemStack stack) {
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
	

	public void startOpen(@Nonnull Player player) {
		this.player = player;
		this.playerDailiesCapability = player.getData(DailiesAttachmentTypes.DAILIES_DATA);
	}


	@Override
	public void stopOpen(@Nonnull Player player) {
		for (int x = 0; x < getContainerSize(); x++) {
			if (!itemStacks[x].isEmpty()) {
				player.drop(itemStacks[x], false);
			}
		}
	}


	@Override
	public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
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
		System.out.println("BaileyInventory: setChanged() called");
		// Trigger quest checking when inventory changes
		// This ensures it works in both creative and survival mode
		checkForReward();
	}


	@Override
	public boolean stillValid(@Nonnull Player player) {
		return true;
	}

	public void checkForReward() {
		if (player != null) {
			IDailiesCapability capability = ensureCapabilitySetup(player);
			if (capability != null) {
				this.playerDailiesCapability = capability;
			}
		}
		
		if(playerDailiesCapability == null) {
			if (player != null) {
				this.playerDailiesCapability = player.getData(DailiesAttachmentTypes.DAILIES_DATA);
			}
			if(playerDailiesCapability == null) {
				LOGGER.warn("[DEBUG] No capabilities found for player");
				return;
			}
		}
		
		lastModifiedStack = this.itemStacks[lastModifiedIndex];
		if (playerDailiesCapability != null) {
			acceptedQuests = playerDailiesCapability.getAcceptedQuests();
			LOGGER.info("[DEBUG] Found {} accepted quests", acceptedQuests.size());
		}

		if (DailiesMod.devMode) {
			logItemStack(lastModifiedStack);
		}
		
		LOGGER.info("[DEBUG] Checking item at slot {}: {}", lastModifiedIndex, lastModifiedStack.getItem().toString());
		
		if(canSearchForReward()) {
			ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(lastModifiedStack.getItem());
			int subType = lastModifiedStack.getDamageValue();
			LOGGER.info("[DEBUG] Looking for quest matching item: {} (subType: {})", itemId, subType);
			DailyQuest quest = checkForMatchingQuest(itemId, subType);
			
			if(quest != null) {
				LOGGER.info("[DEBUG] Found matching quest: {}", quest.getDisplayName());
				updateQuestProgress(quest, lastModifiedStack, lastModifiedIndex);
			} else {
				LOGGER.info("[DEBUG] No matching quest found for item: {}", itemId);
			}
		} else {
			LOGGER.info("[DEBUG] Cannot search for reward - requirements not met");
		}
	}
	
	public static void logItemStack(ItemStack stack) {
		if (stack == null) {
			return;
		}
		System.out.println("LOGGING ITEM STACK");
		System.out.println("type:" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()));
		//System.out.println("subType:" + stack.getMetadata());
		//System.out.println("NBT: " + String.valueOf(stack.getTagCompound()));
	}
	
	private DailyQuest checkForMatchingQuest(ResourceLocation itemId, int itemSubType) {
		LOGGER.info("[DEBUG] checkForMatchingQuest() called with itemId: {}, subType: {}", itemId, itemSubType);
		
		DailyQuest quest = null;
		for (DailyQuest q : acceptedQuests) {
			LOGGER.info("[DEBUG] Checking quest: {} - isGather: {}, rewardFulfilled: {}, targetSubType: {}", 
				q.getDisplayName(), q.isGatherQuest(), q.rewardFulfilled, q.target.subType);
			
			if (q.isGatherQuest() && !q.rewardFulfilled && q.target.subType == itemSubType) {
				// Get quest target's string identifier
				String targetIdentifier = q.target.getItemIdentifier();
				String itemIdentifier = itemId.toString();
				
				LOGGER.info("[DEBUG] Quest target identifier: {}, actual item identifier: {}", targetIdentifier, itemIdentifier);
				
				if (itemIdentifier.equals(targetIdentifier)) {
					LOGGER.info("[DEBUG] Found matching quest: {}", q.getDisplayName());
					quest = q;
					break;
				} else {
					LOGGER.info("[DEBUG] Quest item mismatch - expected: {}, actual: {}", targetIdentifier, itemIdentifier);
				}
			} else {
				LOGGER.info("[DEBUG] Quest skipped - not gather quest, reward fulfilled, or subtype mismatch");
			}
		}
		
		if (quest == null) {
			LOGGER.info("[DEBUG] No matching quest found after checking {} quests", acceptedQuests.size());
		}
		
		return quest;
	}
	
	private void updateQuestProgress(DailyQuest quest, ItemStack stack, int index) {
		System.out.println("BaileyInventory: Updating quest progress for: " + quest.name + " (current: " + quest.progress + "/" + quest.target.quantity + ")");
		
		int remainingTarget = quest.target.quantity - quest.progress;
		int leftOver = stack.getCount() - remainingTarget;
		
		if (leftOver < 0) {
			leftOver = 0;
		}
		
		quest.progress += stack.getCount() - leftOver;
		
		System.out.println("BaileyInventory: Quest progress updated to: " + quest.progress + "/" + quest.target.quantity);
		
		if(quest.isComplete()) {
			quest.rewardFulfilled = true;
			if (playerDailiesCapability != null) {
				playerDailiesCapability.completeQuest(player, quest);
			}
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
		
		System.out.println("BaileyInventory: Calling updateClient to sync to client");
		updateClient(player);
	}
	
	private void buildReward(Reward reward) {
		// Get Item from string identifier using modern Forge registry system
		String rewardIdentifier = reward.getItemIdentifier();
		Item rewardItem;
		try {
			ResourceLocation resourceLocation = ResourceLocation.parse(rewardIdentifier);
			rewardItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(resourceLocation);
			if (rewardItem == null || rewardItem == Items.AIR) {
				rewardItem = Items.DIRT; // Fallback for invalid items
			}
		} catch (Exception e) {
			System.err.println("Failed to resolve reward identifier: " + rewardIdentifier);
			rewardItem = Items.DIRT; // Fallback
		}
		
		ItemStack rewardStack = new ItemStack(rewardItem, reward.quantity);
		if (reward.subType > 0) {
			rewardStack.setDamageValue(reward.subType);
		}
		
		if (reward.nbt != null && !reward.nbt.isEmpty()) {
			try {
				convertNbtStringToDataComponents(rewardStack, reward.nbt);
			} catch (Exception e) {
				System.err.println("Failed to parse reward NBT: " + reward.nbt);
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
		if (playerDailiesCapability != null) {
			playerDailiesCapability.sendAcceptedQuestsToClient(player);
		}
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
	
	//TODO: Double check this.
	private void convertNbtStringToDataComponents(ItemStack stack, String nbtString) {
		
		if (nbtString.contains("display") && nbtString.contains("Name")) {
			try {
				int nameStart = nbtString.indexOf("Name:\"") + 6;
				int nameEnd = nbtString.indexOf("\"", nameStart);
				if (nameStart > 5 && nameEnd > nameStart) {
					String nameJson = nbtString.substring(nameStart, nameEnd);
					Component name = Component.literal(nameJson);
					stack.set(DataComponents.CUSTOM_NAME, name);
				}
			} catch (Exception e) {
				System.err.println("Failed to parse display name from NBT: " + nbtString);
			}
		}
		
		if (nbtString.contains("Lore")) {
			System.out.println("Lore found in NBT - conversion not yet implemented");
		}
		if (nbtString.contains("Enchantments")) {
			System.out.println("Enchantments found in NBT - conversion not yet implemented");
		}
	}
}
