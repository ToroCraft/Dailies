package net.torocraft.dailies.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.dailies.DailiesMod;

public class ConfigurationHandler {

	public static Configuration config;
	public static boolean showQuestsInPlayerInventory;

	public static void init(File configFile) {
		config = new Configuration(configFile);
		loadConfiguration();
	}

	public static void loadConfiguration() {
		try {
			showQuestsInPlayerInventory = config.getBoolean("Show Quests in Inventory", Configuration.CATEGORY_CLIENT, true, "Show quest progression UI on item inventory screen.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}

	@SubscribeEvent
	public void onConfigChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(DailiesMod.MODID)) {
			loadConfiguration();
		}
	}

}
