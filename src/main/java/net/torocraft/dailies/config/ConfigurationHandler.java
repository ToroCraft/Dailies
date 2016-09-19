package net.torocraft.dailies.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {

	public static Configuration config;

	public static boolean showEntityModel;

	public static void init(File configFile) {
		config = new Configuration(configFile);
		loadConfiguration();
	}

	public static void loadConfiguration() {
		try {
			showEntityModel = config.getBoolean("Show 3D Model of Entity", Configuration.CATEGORY_CLIENT, true, "Shows a 3D model of the entity being targeted");
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
		if (event.getModID().equalsIgnoreCase(ToroHealthMod.MODID)) {
			loadConfiguration();
		}
	}

	private static int mapColor(String color) {
		if (color.equals("RED")) {
			return 0xff0000;
		} else if (color.equals("GREEN")) {
			return 0x00ff00;
		} else if (color.equals("BLUE")) {
			return 0x0000ff;
		} else if (color.equals("YELLOW")) {
			return 0xffff00;
		} else if (color.equals("ORANGE")) {
			return 0xffa500;
		} else if (color.equals("BLACK")) {
			return 0x000000;
		} else if (color.equals("PURPLE")) {
			return 0x960096;
		} else {
			return 0xffffff;
		}
	}

}
