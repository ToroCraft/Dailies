package net.torocraft.dailies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.torocraft.dailies.config.ConfigurationHandler;

@Mod(modid = DailiesMod.MODID, guiFactory = "net.torocraft.dailies.gui.GuiFactoryDailies")
public class DailiesMod {

	public static final boolean devMode = false;
	public static final String MODID = "dailies";
	public static final Integer MAX_QUESTS_ACCEPTABLE = 10;
	public static ModMetadata metadata;

	@Instance(value = DailiesMod.MODID)
	public static DailiesMod instance;

	@SidedProxy(clientSide = "net.torocraft.dailies.ClientProxy", serverSide = "net.torocraft.dailies.ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		e.registerServerCommand(new DailiesCommand());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		metadata = e.getModMetadata();
		proxy.preInit(e);
		ConfigurationHandler.init(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}
}
