package net.torocraft.dailies.config;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModContainer;

public class ConfigScreenHandler {

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (ModContainer modContainer, Screen parentScreen) -> createConfigScreen(parentScreen)
        );
    }

    public static Screen createConfigScreen(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }
}
