package net.torocraft.dailies;

import java.util.Set;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
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
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.events.ForgeEvents;
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
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		EntityRegistryHandler.init();
		MinecraftForge.EVENT_BUS.register(Events.class);
		MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonStart);
		MinecraftForge.EVENT_BUS.register(this);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientStart(modEventBus));
	}

	private static void clientStart(IEventBus modEventBus) {
		modEventBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, event -> {
			RenderRegistryHandler.init();
			//MinecraftForge.EVENT_BUS.register(dailyGui);
		});
	}

	private void commonStart(FMLClientSetupEvent event) {
		DailiesCapabilityProvider.register();
		Config.apply();
		//Fixes a null attribute map issue. Will need to rework later
		GlobalEntityTypeAttributes.put(EntityRegistryHandler.BAILEY.get(), VillagerEntity.registerAttributes().create());
		PacketHandler.init();

		//modEventBus.addListener(EventPriority.NORMAL, false, FMLServerStartingEvent.class, event -> {
			//NetworkRegistry.INSTANCE.registerGuiHandler(DailiesMod.instance, new DailiesGuiHandler());
			//MapGenStructureIO.registerStructureComponent(BaileysShopVillagePiece.class, "baileyshop");
			//VillagerRegistry.instance().registerVillageCreationHandler(new VillageHandlerBailey());
		//});
	}



	public static void displayQuestProgress(DailyQuest quest) {
		//dailyGui.setQuest(quest);
	}
}
