package net.torocraft.dailies.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class ConfigScreen extends Screen {
    
    private final Screen parentScreen;
    private Checkbox onlineCheckbox;
    private Checkbox showQuestsCheckbox;
    private Checkbox baileySpawningCheckbox;
    
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
        onlineCheckbox = Checkbox.builder(Component.literal("Online Mode Enabled"), this.font)
            .pos(centerX - 75, startY)
            .selected(Config.isOnline)
            .onValueChange((checkbox, selected) -> {})
            .build();
        addRenderableWidget(onlineCheckbox);
        
        // Show quests in inventory checkbox
        showQuestsCheckbox = Checkbox.builder(Component.literal("Show Quests in Inventory"), this.font)
            .pos(centerX - 75, startY + 25)
            .selected(Config.showQuestsInInventory)
            .onValueChange((checkbox, selected) -> {})
            .build();
        addRenderableWidget(showQuestsCheckbox);
        
        // Bailey spawning checkbox
        baileySpawningCheckbox = Checkbox.builder(Component.literal("Enable Bailey Spawning"), this.font)
            .pos(centerX - 75, startY + 50)
            .selected(Config.enableBaileySpawning)
            .onValueChange((checkbox, selected) -> {})
            .build();
        addRenderableWidget(baileySpawningCheckbox);
        
        // Done button
        addRenderableWidget(Button.builder(Component.literal("Done"), (button) -> {
            saveConfig();
            if (this.minecraft != null) {
                this.minecraft.setScreen(parentScreen);
            }
        }).bounds(centerX - 50, startY + 85, 100, 20).build());
        
        // Cancel button
        addRenderableWidget(Button.builder(Component.literal("Cancel"), (button) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(parentScreen);
            }
        }).bounds(centerX - 50, startY + 110, 100, 20).build());
    }
    
    private void saveConfig() {
        // Update config values
        Config.CLIENT.isOnline.set(onlineCheckbox.selected());
        Config.CLIENT.showQuestsInInventory.set(showQuestsCheckbox.selected());
        Config.COMMON.enableBaileySpawning.set(baileySpawningCheckbox.selected());
        Config.apply();
    }
    
    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        
        // Draw description
        guiGraphics.drawCenteredString(this.font, 
            Component.literal("Configure Dailies mod settings"), 
            this.width / 2, 40, 0xCCCCCC);
            
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }
    
    public static Screen createConfigScreen(Screen parentScreen) {
        return new ConfigScreen(parentScreen);
    }
}
