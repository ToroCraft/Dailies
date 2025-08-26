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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.commands.DailiesCommand;
import net.torocraft.dailies.config.Config;
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
		dailies.readNBT((CompoundTag) event.getEntity().getPersistentData().get(DailiesCapabilityProvider.NAME));
	}

	private static IDailiesCapability getCapability(Player player) {
		if (isMissingCapability(player)) {
			return null;
		}
		return player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(null);
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

	private static void setupDailiesData(Player player) {
		IDailiesCapability cap = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(null);
		if (cap == null)
			return;
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
		cap.setAvailableQuests(new HashSet<>(openDailyQuests));
		cap.setAcceptedQuests(new HashSet<>(acceptedDailyQuests));
		cap.writeNBT();
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
}