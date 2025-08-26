package net.torocraft.dailies.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.torocraft.dailies.config.Config;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Simple configuration screen for the Dailies mod.
 * Uses Forge's built-in config screen system for 1.19.2+
 */
public class ConfigScreen extends Screen {
    
    private final Screen parentScreen;
    private Checkbox onlineCheckbox;
    
    public ConfigScreen(Screen parentScreen) {
        super(Component.literal("Dailies Configuration"));
        this.parentScreen = parentScreen;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Add config options
        int centerX = this.width / 2;
        int startY = this.height / 4;
        
        // Online mode checkbox
        onlineCheckbox = new Checkbox(centerX - 75, startY, 150, 20, 
            Component.literal("Online Mode Enabled"), Config.isOnline);
        addRenderableWidget(onlineCheckbox);
        
        // Done button
        addRenderableWidget(new Button(centerX - 50, startY + 60, 100, 20, 
            Component.literal("Done"), (button) -> {
            saveConfig();
            this.minecraft.setScreen(parentScreen);
        }));
        
        // Cancel button
        addRenderableWidget(new Button(centerX - 50, startY + 85, 100, 20, 
            Component.literal("Cancel"), (button) -> {
            this.minecraft.setScreen(parentScreen);
        }));
    }
    
    private void saveConfig() {
        // Update config values
        Config.CLIENT.isOnline.set(onlineCheckbox.selected());
        Config.apply();
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        
        // Draw description
        drawCenteredString(poseStack, this.font, 
            Component.literal("Configure Dailies mod settings"), 
            this.width / 2, 40, 0xCCCCCC);
            
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
    
    @Override
    public void onClose() {
        this.minecraft.setScreen(parentScreen);
    }
    
    /**
     * Factory method for creating the config screen
     */
    public static Screen createConfigScreen(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }
}
