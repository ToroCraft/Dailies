package net.torocraft.dailies.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.config.ConfigurationHandler;

public class GuiConfigDailies extends GuiConfig {

	public GuiConfigDailies(GuiScreen parent) {
		super (parent, new ConfigElement(ConfigurationHandler.config.getCategory(Configuration.CATEGORY_CLIENT)).getChildElements(),
				DailiesMod.MODID,
				false,
				false,
				DailiesMod.MODNAME);
		titleLine2 = ConfigurationHandler.config.getConfigFile().getAbsolutePath();
	}
}
