package net.torocraft.dailies.config;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ConfigScreenHandler {

    public static void registerConfigScreen() {
        try {
            ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (minecraft, parentScreen) -> createConfigScreen(parentScreen)
            );
        } catch (Exception e) {
            // If config screen registration fails, just log and continue
            System.out.println("Failed to register config screen for Dailies mod: " + e.getMessage());
        }
    }

    public static Screen createConfigScreen(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }
}
