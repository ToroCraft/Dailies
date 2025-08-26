package net.torocraft.dailies;

import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.torocraft.dailies.config.Config;
import net.torocraft.dailies.config.ConfigScreen;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.events.Events;
import net.torocraft.dailies.gui.MenuRegistryHandler;
import net.torocraft.dailies.items.ItemRegistryHandler;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.quests.DailyQuest;

@Mod(DailiesMod.MODID)
public class DailiesMod {

	public static final boolean devMode = false;
	public static final String MODID = "dailies";
	public static final Integer MAX_QUESTS_ACCEPTABLE = 10;

	// Note: Static quest fields removed in favor of ClientQuestCache for better client-server sync
	
	//public static GuiDailyProgressIndicators dailyGui = new GuiDailyProgressIndicators();

	public DailiesMod() {
	ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, Config.CLIENT_CONFIG_SPEC);
	
	// Register config screen for Mods menu (Forge 1.19.2-43.2.21)
	ModLoadingContext.get().registerExtensionPoint(
		net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
		() -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(
			(client, parentScreen) -> new ConfigScreen(parentScreen)
		)
	);
	
	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
	EntityRegistryHandler.init();
	MenuRegistryHandler.init(modEventBus);
	ItemRegistryHandler.init(modEventBus);
	PacketHandler.init(); // Initialize network packets during mod loading
	MinecraftForge.EVENT_BUS.register(Events.class);
	modEventBus.addListener(this::onEntityAttributeCreation);
	DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientStart(modEventBus));
	}

	private static void clientStart(IEventBus modEventBus) {
		modEventBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, event -> {
			// Config screen registration is working via ConfigScreenHandler.ConfigScreenFactory
			// RenderRegistryHandler uses @SubscribeEvent annotations - no need to call init()
			//MinecraftForge.EVENT_BUS.register(dailyGui);
		});
	}


	   private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
		   // Register attributes for custom entities
		   event.put(EntityRegistryHandler.BAILEY.get(), Villager.createAttributes().build());
	   }



	public static void displayQuestProgress(DailyQuest quest) {
		//dailyGui.setQuest(quest);
	}
}
