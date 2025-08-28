package net.torocraft.dailies;

import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.torocraft.dailies.attachments.DailiesAttachmentTypes;
import net.torocraft.dailies.config.Config;
import net.torocraft.dailies.config.ConfigScreenHandler;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.events.Events;
import net.torocraft.dailies.gui.MenuRegistryHandler;
import net.torocraft.dailies.items.ItemRegistryHandler;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.worldgen.ModStructures;
import net.torocraft.dailies.worldgen.village.BaileyShopStructurePieceType;

@Mod(DailiesMod.MODID)
public class DailiesMod {

	public static final boolean devMode = false;
	public static final String MODID = "dailies";
	public static final Integer MAX_QUESTS_ACCEPTABLE = 10;
	

	public DailiesMod(IEventBus modEventBus, ModContainer modContainer) {
		// Register configurations using ModContainer (NeoForge 1.21.4 approach)
		modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG_SPEC);
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG_SPEC);
		modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG_SPEC);
		
		// Register config event listeners with proper event types
		modEventBus.addListener(this::onConfigLoad);
		modEventBus.addListener(this::onConfigReload);
		
		EntityRegistryHandler.init(modEventBus);
		MenuRegistryHandler.init(modEventBus);
		ItemRegistryHandler.init(modEventBus);
		DailiesAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus); // Register attachment types
		ModStructures.register(modEventBus); // Register structure types
		BaileyShopStructurePieceType.STRUCTURE_PIECE_TYPES.register(modEventBus); // Register structure piece types
		PacketHandler.init(); // Initialize network packets during mod loading
		NeoForge.EVENT_BUS.register(Events.class);
		modEventBus.addListener(this::onEntityAttributeCreation);
		modEventBus.addListener(PacketHandler::registerPayloads); // Register packet payloads
		
		// Client-only setup
		if (FMLEnvironment.dist == Dist.CLIENT) {
			clientStart(modEventBus);
		}
	}

	private static void clientStart(IEventBus modEventBus) {
		modEventBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, event -> {
			// Register the config screen factory for NeoForge mod menu integration
			ConfigScreenHandler.registerConfigScreen();
			// RenderRegistryHandler uses @SubscribeEvent annotations - no need to call init()
			//NeoForge.EVENT_BUS.register(dailyGui);
		});
	}

	private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		// Register attributes for custom entities
		event.put(EntityRegistryHandler.BAILEY.get(), Villager.createAttributes().build());
	}

	private void onConfigLoad(ModConfigEvent.Loading event) {
		Config.onLoad(event.getConfig());
	}

	private void onConfigReload(ModConfigEvent.Reloading event) {
		Config.onFileChange(event.getConfig());
	}

	public static void displayQuestProgress(DailyQuest quest) {
		//dailyGui.setQuest(quest);
	}
}
