package net.torocraft.dailies.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.commands.DailiesCommand;
import net.torocraft.dailies.network.DailiesNetworkException;
import net.torocraft.dailies.network.QuestInventoryFetcher;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.RandomQuestGenerator;
import net.torocraft.dailies.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.torocraft.dailies.DailiesMod.MODID;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Events {

	private static final Logger LOGGER = LogManager.getLogger(MODID + " Events");

	@SubscribeEvent
	public static void registerCommands(final FMLServerStartingEvent event) {
		LOGGER.debug("REGISTERING COMMANDS");
		DailiesCommand.register(event.getCommandDispatcher());
	}

	@SubscribeEvent
	public static void onHunt(LivingDeathEvent event) {

		PlayerEntity player = null;

		LivingEntity e = (LivingEntity) event.getEntity();
		DamageSource source = event.getSource();

		if (source.getTrueSource() instanceof PlayerEntity) {
			player = (PlayerEntity) source.getTrueSource();
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

		IDailiesCapability newDailies = getCapability(event.getPlayer());
		IDailiesCapability originalDailies = getCapability(event.getOriginal());

		if (newDailies == null || originalDailies == null) {
			return;
		}

		newDailies.readNBT(originalDailies.writeNBT());
	}

	@SubscribeEvent
	public static void onSave(PlayerEvent.SaveToFile event) {
		IDailiesCapability dailies = getCapability(event.getPlayer());
		if (dailies == null) {
			return;
		}
		event.getPlayer().getPersistentData().put(DailiesCapabilityProvider.NAME, dailies.writeNBT());
	}

	@SubscribeEvent
	public static void onLoad(PlayerEvent.LoadFromFile event) {
		IDailiesCapability dailies = getCapability(event.getPlayer());
		if (dailies == null) {
			return;
		}
		dailies.readNBT((CompoundNBT) event.getPlayer().getPersistentData().get(DailiesCapabilityProvider.NAME));
	}

	private static IDailiesCapability getCapability(PlayerEntity player) {
		if (isMissingCapability(player)) {
			return null;
		}
        return player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(null);
	}

	private static boolean isMissingCapability(PlayerEntity player) {
		return player == null || !player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).isPresent();
	}

	@SubscribeEvent
	public static void onEntityLoad(AttachCapabilitiesEvent<Entity> event) {

		if (!(event.getObject() instanceof PlayerEntity)) {
			return;
		}

		event.addCapability(new ResourceLocation(DailiesCapabilityProvider.NAME), new DailiesCapabilityProvider());
	}


	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		final PlayerEntity player = event.getPlayer();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				setupDailiesData(player);
			}
		}).start();
	}
	
	private static void setupDailiesData(PlayerEntity player) {;
		IDailiesCapability cap = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(null);
		if(cap == null)
			return;

		Set<DailyQuest> serversDailyQuests = getDailyQuests(player);
		
		List<DailyQuest> openDailyQuests = new ArrayList<DailyQuest>();
		List<DailyQuest> acceptedDailyQuests = new ArrayList<DailyQuest>();

		for (DailyQuest quest : serversDailyQuests) {
			if ("available".equals(quest.status)) {
				openDailyQuests.add(quest);
			} else if ("accepted".equals(quest.status)) {
				acceptedDailyQuests.add(quest);
			}
		}
		
		cap.setAvailableQuests(new HashSet<DailyQuest>(openDailyQuests));
		cap.setAcceptedQuests(new HashSet<DailyQuest>(acceptedDailyQuests));
		
		cap.writeNBT();
	}
	
	private static Set<DailyQuest> getDailyQuests(PlayerEntity player) {
		Set<DailyQuest> quests = new HashSet<DailyQuest>();
		try {
			quests = new QuestInventoryFetcher(player).getQuestInventory();
		} catch (DailiesNetworkException e) {
			player.sendMessage(e.getMessageAsTextComponent(), player.getUniqueID());
			player.sendMessage(new StringTextComponent("Randomly generating quests instead."), player.getUniqueID());
			quests = new RandomQuestGenerator().generateQuests();
		} catch (DailiesException e) {
			player.sendMessage(e.getMessageAsTextComponent(), player.getUniqueID());
		}
		return quests;
	}

	@SubscribeEvent
	public static void onConfigLoad(net.minecraftforge.fml.config.ModConfig.Loading event) {
		Config.onLoad(event.getConfig());
	}
}