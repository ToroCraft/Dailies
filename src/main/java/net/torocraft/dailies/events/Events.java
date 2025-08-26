package net.torocraft.dailies.events;

import static net.torocraft.dailies.DailiesMod.MODID;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torocraft.dailies.capabilities.DailiesCapabilityImpl;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.commands.DailiesCommand;
import net.torocraft.dailies.network.remote.DailiesNetworkException;
import net.torocraft.dailies.network.remote.QuestInventoryFetcher;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.RandomQuestGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

	private static final Logger LOGGER = LogManager.getLogger(MODID + " Events");

	@SubscribeEvent
	public static void registerCommands(final RegisterCommandsEvent event) {
		LOGGER.debug("REGISTERING COMMANDS");
		DailiesCommand.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void onHunt(LivingDeathEvent event) {
		Player player = null;
		LivingEntity e = event.getEntity();
		DamageSource source = event.getSource();
		Entity attacker = source.getEntity();
		if (attacker instanceof Player) {
			player = (Player) attacker;
		}
		if (player == null) {
			return;
		}
		IDailiesCapability dailies = getCapability(player);
		if (dailies == null) {
			return;
		}
		dailies.hunt(player, e);
	}

	@SubscribeEvent
	public static void onDeath(PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			return;
		}
		IDailiesCapability newDailies = getCapability(event.getEntity());
		IDailiesCapability originalDailies = getCapability(event.getOriginal());
		if (newDailies == null || originalDailies == null) {
			return;
		}
		newDailies.readNBT(originalDailies.writeNBT());
	}

	@SubscribeEvent
	public static void onSave(PlayerEvent.SaveToFile event) {
		IDailiesCapability dailies = getCapability(event.getEntity());
		if (dailies == null) {
			return;
		}
		event.getEntity().getPersistentData().put(DailiesCapabilityProvider.NAME, dailies.writeNBT());
	}

	@SubscribeEvent
	public static void onLoad(PlayerEvent.LoadFromFile event) {
		IDailiesCapability dailies = getCapability(event.getEntity());
		if (dailies == null) {
			return;
		}
		
		// Check if there's saved data and load it
		CompoundTag savedData = (CompoundTag) event.getEntity().getPersistentData().get(DailiesCapabilityProvider.NAME);
		if (savedData != null) {
			dailies.readNBT(savedData);
		}
	}

	private static IDailiesCapability getCapability(Player player) {
		if (isMissingCapability(player)) {
			return null;
		}
		return player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(new DailiesCapabilityImpl());
	}

	private static boolean isMissingCapability(Player player) {
		return player == null || !player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).isPresent();
	}

	@SubscribeEvent
	public static void onEntityLoad(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof Player)) {
			return;
		}
		event.addCapability(new ResourceLocation(DailiesCapabilityProvider.NAME), new DailiesCapabilityProvider());
	}


	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		final Player player = event.getEntity();
		new Thread(() -> setupDailiesData(player)).start();
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		// Only check on server side, and only once per second to avoid spam
		if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
			return;
		}
		
		Player player = event.player;
		if (player.tickCount % 20 == 0) { // Check every 20 ticks (1 second)
			checkPlayerInventoryForQuestProgress(player);
		}
	}

	private static void setupDailiesData(Player player) {
		IDailiesCapability cap = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(new DailiesCapabilityImpl());
		if (cap == null)
			return;
			
		// Preserve existing accepted quests from saved data
		Set<DailyQuest> existingAcceptedQuests = cap.getAcceptedQuests();
		if (existingAcceptedQuests == null) {
			existingAcceptedQuests = new HashSet<>();
		}
		
		Set<DailyQuest> serversDailyQuests = getDailyQuests(player);
		List<DailyQuest> openDailyQuests = new ArrayList<>();
		List<DailyQuest> acceptedDailyQuests = new ArrayList<>();
		
		for (DailyQuest quest : serversDailyQuests) {
			if ("available".equals(quest.status)) {
				openDailyQuests.add(quest);
			} else if ("accepted".equals(quest.status)) {
				acceptedDailyQuests.add(quest);
			}
		}
		
		// Merge server accepted quests with existing accepted quests
		Set<DailyQuest> mergedAcceptedQuests = new HashSet<>(existingAcceptedQuests);
		mergedAcceptedQuests.addAll(acceptedDailyQuests);
		
		// Only update available quests, preserve accepted quests
		cap.setAvailableQuests(new HashSet<>(openDailyQuests));
		cap.setAcceptedQuests(mergedAcceptedQuests);
		
		// Force save the updated data
		player.getPersistentData().put(DailiesCapabilityProvider.NAME, cap.writeNBT());
	}

	private static Set<DailyQuest> getDailyQuests(Player player) {
		Set<DailyQuest> quests = new HashSet<>();
		try {
			quests = new QuestInventoryFetcher(player).getQuestInventory();
		} catch (DailiesNetworkException e) {
			player.sendSystemMessage(Component.literal("Randomly generating quests instead."));
			quests = new RandomQuestGenerator().generateQuests();
		} catch (DailiesException e) {
			player.sendSystemMessage(Component.literal(e.getMessage()));
		}
		return quests;
	}

	/**
	 * Checks player inventory for quest items and updates progress accordingly
	 * This implements the two-stage quest system:
	 * Stage 1: Collecting items into inventory updates quest progress count
	 * Stage 2: Placing items in Bailey's inventory provides rewards
	 */
	public static void checkPlayerInventoryForQuestProgress(Player player) {
		if (player == null || player.level.isClientSide()) {
			return;
		}
		
		IDailiesCapability capability = getCapability(player);
		if (capability == null) {
			return;
		}
		
		Set<DailyQuest> acceptedQuests = capability.getAcceptedQuests();
		if (acceptedQuests == null || acceptedQuests.isEmpty()) {
			return;
		}
		
		// Check player inventory for quest items
		for (DailyQuest quest : acceptedQuests) {
			if (!quest.isGatherQuest() || quest.rewardFulfilled) {
				continue;
			}
			
			// Count relevant items in player inventory
			int totalCount = 0;
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack stack = player.getInventory().getItem(i);
				if (stack.isEmpty()) {
					continue;
				}
				
				// Check if this item matches the quest
				ResourceLocation itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
				int subType = stack.getDamageValue();
				
				if (matchesQuestItem(quest, itemId, subType)) {
					totalCount += stack.getCount();
				}
			}
			
			// Update quest progress (but don't complete it)
			if (totalCount != quest.progress) {
				quest.progress = Math.min(totalCount, quest.target.quantity);
				LOGGER.info("Updated quest '{}' progress to {}/{}", quest.name, quest.progress, quest.target.quantity);
				// Send progress update to client
				capability.sendAcceptedQuestsToClient(player);
			}
		}
	}
	
	/**
	 * Helper method to check if an item matches a quest's target item
	 */
	private static boolean matchesQuestItem(DailyQuest quest, ResourceLocation itemId, int subType) {
		if (quest.target.subType != subType) {
			return false;
		}
		
		// Convert quest target's legacy integer ID to modern item for comparison
		net.minecraft.world.item.Item targetItem = getItemFromType(quest.target.type);
		ResourceLocation targetItemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(targetItem);
		
		return itemId.equals(targetItemId);
	}
	
	/**
	 * Convert legacy integer item ID to modern Item from registry
	 * Copied from BaileyInventory for consistency
	 */
	private static net.minecraft.world.item.Item getItemFromType(int itemId) {
		switch (itemId) {
			case 1: return net.minecraft.world.item.Items.STONE;
			case 2: return net.minecraft.world.item.Items.GRASS_BLOCK;
			case 3: return net.minecraft.world.item.Items.DIRT;
			case 4: return net.minecraft.world.item.Items.COBBLESTONE;
			case 5: return net.minecraft.world.item.Items.OAK_PLANKS;
			case 263: return net.minecraft.world.item.Items.COAL;
			case 264: return net.minecraft.world.item.Items.DIAMOND;
			case 265: return net.minecraft.world.item.Items.IRON_INGOT;
			case 266: return net.minecraft.world.item.Items.GOLD_INGOT;
			case 287: return net.minecraft.world.item.Items.STRING;
			case 318: return net.minecraft.world.item.Items.FLINT;
			case 348: return net.minecraft.world.item.Items.GLOWSTONE_DUST;
			case 353: return net.minecraft.world.item.Items.SUGAR;
			case 354: return net.minecraft.world.item.Items.CAKE;
			case 367: return net.minecraft.world.item.Items.ROTTEN_FLESH;
			case 375: return net.minecraft.world.item.Items.SPIDER_EYE;
			case 376: return net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE;
			default: 
				// Fallback to dirt if unknown ID
				return net.minecraft.world.item.Items.DIRT;
		}
	}
}