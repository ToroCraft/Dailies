package net.torocraft.dailies;

import java.util.Set;
// Imports for 1.19.2 (correct package structure)
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.config.Config;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.entities.render.RenderRegistryHandler;
import net.torocraft.dailies.events.Events;
import net.torocraft.dailies.gui.MenuRegistryHandler;
import net.torocraft.dailies.items.ItemRegistryHandler;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.quests.DailyQuest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DailiesMod.MODID)
public class DailiesMod {

	public static final boolean devMode = false;
	public static final String MODID = "dailies";
	public static final Integer MAX_QUESTS_ACCEPTABLE = 10;

	public static Set<DailyQuest> availableQuests;
	public static Set<DailyQuest> acceptedQuests;

	private static final Logger LOGGER = LogManager.getLogger(MODID + " Event Subscriber");

	//public static GuiDailyProgressIndicators dailyGui = new GuiDailyProgressIndicators();

	public DailiesMod() {
	ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, Config.CLIENT_CONFIG_SPEC);
	// TODO: Register config screen for Mods menu using ModLoadingContext.get().registerExtensionPoint with DisplayTest or other modern Forge method (see Forge 1.18.2+ docs)
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
