package net.torocraft.dailies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.torocraft.torohealthmod.config.ConfigurationHandler;

@Mod(modid = DailiesMod.MODID, name = DailiesMod.MODNAME, version = DailiesMod.VERSION)
public class DailiesMod {

	public static final String MODID = "dailies";
	public static final String VERSION = "1.9.4-4";
	public static final String MODNAME = "DailiesMod";
	public static final Integer MAX_QUESTS_ACCEPTABLE = 10;

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
