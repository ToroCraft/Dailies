package net.torocraft.dailies.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaileyInventoryGui extends AbstractContainerScreen<DailiesContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(DailiesMod.MODID, "textures/gui/bailey_gui.png");

    private boolean showingQuests = false;

    public BaileyInventoryGui(DailiesContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        
        // Add a button to toggle quest view
        this.addRenderableWidget(new Button(
            this.leftPos + this.imageWidth + 5, this.topPos + 10, 80, 20,
            Component.literal("View Quests"), 
            (button) -> {
                this.showingQuests = !this.showingQuests;
                button.setMessage(Component.literal(this.showingQuests ? "Hide Quests" : "View Quests"));
                if (this.showingQuests) {
                    // Request fresh quest data when showing quests
                    PacketHandler.getQuests(QuestsFilter.AVAILABLE);
                    PacketHandler.getQuests(QuestsFilter.ACCEPTED);
                }
            }));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        }
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, Component.literal("Bailey's Shop"), 8, 6, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY);
        
        // Render quest panel if showing quests
        if (this.showingQuests) {
            renderQuestPanel(poseStack, mouseX, mouseY);
        }
    }

    private void renderQuestPanel(PoseStack poseStack, int mouseX, int mouseY) {
        int panelX = this.leftPos + this.imageWidth + 10;
        int panelY = this.topPos + 35;
        int panelWidth = 200;
        int panelHeight = 300;
        
        // Draw panel background
        fill(poseStack, panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA000000);
        fill(poseStack, panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + panelHeight - 1, 0xFF2D2D30);
        
        // Quest panel title
        this.font.draw(poseStack, Component.literal("Available Quests"), panelX + 5, panelY + 5, 0xFFFFFF);
        
        // Render available quests
        Set<DailyQuest> available = DailiesMod.availableQuests;
        if (available != null && !available.isEmpty()) {
            List<DailyQuest> quests = new ArrayList<>(available);
            int yOffset = 20;
            for (int i = 0; i < Math.min(5, quests.size()); i++) {
                DailyQuest quest = quests.get(i);
                renderQuestEntry(poseStack, quest, panelX + 5, panelY + yOffset, panelWidth - 10, mouseX, mouseY);
                yOffset += 25;
            }
        } else {
            this.font.draw(poseStack, Component.literal("No quests available"), panelX + 5, panelY + 25, 0x888888);
        }
        
        // Render accepted quests section
        this.font.draw(poseStack, Component.literal("Your Quests"), panelX + 5, panelY + 150, 0xFFFFFF);
        
        Set<DailyQuest> accepted = DailiesMod.acceptedQuests;
        if (accepted != null && !accepted.isEmpty()) {
            List<DailyQuest> quests = new ArrayList<>(accepted);
            int yOffset = 170;
            for (int i = 0; i < Math.min(5, quests.size()); i++) {
                DailyQuest quest = quests.get(i);
                renderAcceptedQuestEntry(poseStack, quest, panelX + 5, panelY + yOffset, panelWidth - 10, mouseX, mouseY);
                yOffset += 25;
            }
        } else {
            this.font.draw(poseStack, Component.literal("No active quests"), panelX + 5, panelY + 175, 0x888888);
        }
    }

    private void renderQuestEntry(PoseStack poseStack, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        // Quest background
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 20;
        fill(poseStack, x, y, x + width, y + 20, hovered ? 0xFF4A4A4A : 0xFF3A3A3A);
        
        // Quest name
        String questName = this.font.plainSubstrByWidth(quest.name, width - 5);
        this.font.draw(poseStack, Component.literal(questName), x + 2, y + 2, 0xFFFFFF);
        
        // Quest description
        String questDesc = this.font.plainSubstrByWidth(quest.description, width - 5);
        this.font.draw(poseStack, Component.literal(questDesc), x + 2, y + 11, 0xCCCCCC);
        
        // Click to accept quest (placeholder - you'd implement quest acceptance logic)
        if (hovered && this.minecraft != null && this.minecraft.mouseHandler.isLeftPressed()) {
            // TODO: Implement quest acceptance
        }
    }

    private void renderAcceptedQuestEntry(PoseStack poseStack, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        // Quest background  
        fill(poseStack, x, y, x + width, y + 20, 0xFF1E3A1E);
        
        // Quest name with progress
        String questText = quest.name + " (" + quest.progress + "/" + quest.target.quantity + ")";
        String displayText = this.font.plainSubstrByWidth(questText, width - 5);
        this.font.draw(poseStack, Component.literal(displayText), x + 2, y + 2, 0x88FF88);
        
        // Progress bar
        int progressBarWidth = width - 10;
        int progressWidth = (int) (progressBarWidth * ((double) quest.progress / quest.target.quantity));
        fill(poseStack, x + 2, y + 15, x + 2 + progressBarWidth, y + 17, 0xFF444444);
        fill(poseStack, x + 2, y + 15, x + 2 + progressWidth, y + 17, 0xFF88FF88);
    }
}
