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
import net.torocraft.dailies.client.ClientQuestCache;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaileyInventoryGui extends AbstractContainerScreen<DailiesContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(DailiesMod.MODID, "textures/gui/bailey_gui.png");

    private boolean showingQuests = false;
    
    // Scrolling variables
    private int availableQuestScroll = 0;
    private int acceptedQuestScroll = 0;
    private static final int QUESTS_PER_PAGE = 6; // Increased from 3 to 6 - Number of quests visible at once
    private static final int QUEST_ENTRY_HEIGHT = 30; // Reduced from 35 to 30 for more compact layout
    private static final int ACCEPTED_QUEST_ENTRY_HEIGHT = 35; // Reduced from 40 to 35 for more compact layout

    /**
     * Create a consistently ordered list from a quest set to ensure stable scrolling and button interactions
     */
    private List<DailyQuest> createOrderedQuestList(Set<DailyQuest> questSet) {
        if (questSet == null || questSet.isEmpty()) {
            return new ArrayList<>();
        }
        List<DailyQuest> quests = new ArrayList<>(questSet);
        // Sort by quest ID for consistent ordering
        quests.sort((q1, q2) -> {
            if (q1.id == null && q2.id == null) return 0;
            if (q1.id == null) return 1;
            if (q2.id == null) return -1;
            return q1.id.compareTo(q2.id);
        });
        return quests;
    }

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
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            RenderSystem.setShaderTexture(0, TEXTURE);
        }
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, Component.literal("Bailey's Shop"), 8, 6, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, 8, 38, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.showingQuests) { // Left click
            // Check quest panel clicks
            return handleQuestPanelClick(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.showingQuests) {
            // Check if mouse is over quest panel - use same logic as renderQuestPanel
            int panelWidth = 220;
            int maxPanelHeight = 300; // Increased from 240 to 300 for more quest space
            int availableHeight = this.height - 20;
            int panelHeight = Math.min(maxPanelHeight, availableHeight);
            
            // Calculate panel position (same logic as in renderQuestPanel)
            int panelX = this.leftPos + this.imageWidth + 5;
            if (panelX + panelWidth > this.width - 5) {
                if (this.leftPos - panelWidth - 5 >= 5) {
                    panelX = this.leftPos - panelWidth - 5;
                } else {
                    panelX = this.leftPos + this.imageWidth - panelWidth;
                }
            }
            int panelY = this.topPos + (this.imageHeight - panelHeight) / 2;
            panelY = Math.max(10, Math.min(panelY, this.height - panelHeight - 10));
            
            if (mouseX >= panelX && mouseX <= panelX + panelWidth && 
                mouseY >= panelY && mouseY <= panelY + panelHeight) {
                
                // Calculate section boundaries
                int availableSectionHeight = (panelHeight - 40) / 2;
                int acceptedSectionStart = panelY + 20 + availableSectionHeight + 15;
                
                // Determine which section we're scrolling
                if (mouseY < acceptedSectionStart) {
                    // Available quests section
                    ClientQuestCache cache = ClientQuestCache.getInstance();
                    Set<DailyQuest> available = cache.getAvailableQuests();
                    if (available != null) {
                        List<DailyQuest> quests = createOrderedQuestList(available);
                        int visibleQuests = Math.min(QUESTS_PER_PAGE, (availableSectionHeight - 20) / QUEST_ENTRY_HEIGHT);
                        int maxScroll = Math.max(0, quests.size() - visibleQuests);
                        availableQuestScroll = Math.max(0, Math.min(maxScroll, 
                            availableQuestScroll - (int)(delta * 2)));
                    }
                } else {
                    // Accepted quests section
                    ClientQuestCache cache = ClientQuestCache.getInstance();
                    Set<DailyQuest> accepted = cache.getAcceptedQuests();
                    if (accepted != null) {
                        List<DailyQuest> quests = createOrderedQuestList(accepted);
                        int availableHeightForAccepted = panelHeight - (acceptedSectionStart - panelY) - 20;
                        int visibleQuests = Math.min(QUESTS_PER_PAGE, availableHeightForAccepted / ACCEPTED_QUEST_ENTRY_HEIGHT);
                        int maxScroll = Math.max(0, quests.size() - visibleQuests);
                        acceptedQuestScroll = Math.max(0, Math.min(maxScroll, 
                            acceptedQuestScroll - (int)(delta * 2)));
                    }
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private boolean handleQuestPanelClick(double mouseX, double mouseY) {
        // Calculate panel position (same logic as renderQuestPanel)
        int panelWidth = 220;
        int panelHeight = 300; // Updated to match render method (increased from 240)
        
        // Use same positioning strategy as render method
        int panelX, panelY;
        
        // Strategy 1: Right side of GUI (preferred)
        panelX = this.leftPos + this.imageWidth + 5;
        if (panelX + panelWidth <= this.width - 5) {
            // Fits on right side - use it
        } 
        // Strategy 2: Left side of GUI
        else if (this.leftPos - panelWidth - 5 >= 5) {
            panelX = this.leftPos - panelWidth - 5;
        }
        // Strategy 3: Overlay on top-right of GUI (fallback)
        else {
            panelX = this.leftPos + this.imageWidth - panelWidth;
        }
        
        // Center vertically relative to the GUI
        panelY = this.topPos + (this.imageHeight - panelHeight) / 2;
        
        // Ensure panel doesn't go off screen vertically
        panelY = Math.max(5, Math.min(panelY, this.height - panelHeight - 5));
        
        // Check available quests accept buttons
        ClientQuestCache cache = ClientQuestCache.getInstance();
        Set<DailyQuest> available = cache.getAvailableQuests();
        if (available != null && !available.isEmpty()) {
            List<DailyQuest> quests = createOrderedQuestList(available);
            int availableSectionHeight = (panelHeight - 40) / 2; // Split remaining space between sections
            int visibleQuests = Math.min(QUESTS_PER_PAGE, (availableSectionHeight - 20) / QUEST_ENTRY_HEIGHT);
            int startIndex = availableQuestScroll;
            int endIndex = Math.min(startIndex + visibleQuests, quests.size());
            int yOffset = 20;
            
            for (int i = startIndex; i < endIndex; i++) {
                DailyQuest quest = quests.get(i);
                
                // Accept button bounds (match renderQuestEntry)
                int buttonX = panelX + 5 + (panelWidth - 10) - 55;
                int buttonY = panelY + yOffset + 5;
                int buttonWidth = 50;
                int buttonHeight = 18;
                
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                    mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                    acceptQuest(quest);
                    return true;
                }
                yOffset += QUEST_ENTRY_HEIGHT;
            }
        }
        
        // Check accepted quests cancel buttons
        Set<DailyQuest> accepted = cache.getAcceptedQuests();
        if (accepted != null && !accepted.isEmpty()) {
            List<DailyQuest> quests = createOrderedQuestList(accepted);
            int availableSectionHeight = (panelHeight - 40) / 2;
            int acceptedSectionStart = 20 + availableSectionHeight + 15;
            int availableHeightForAccepted = panelHeight - acceptedSectionStart - 20;
            int visibleQuests = Math.min(QUESTS_PER_PAGE, availableHeightForAccepted / ACCEPTED_QUEST_ENTRY_HEIGHT);
            int startIndex = acceptedQuestScroll;
            int endIndex = Math.min(startIndex + visibleQuests, quests.size());
            int yOffset = acceptedSectionStart + 15;
            
            for (int i = startIndex; i < endIndex; i++) {
                DailyQuest quest = quests.get(i);
                
                // Cancel button bounds (match renderAcceptedQuestEntry)
                int buttonX = panelX + 5 + (panelWidth - 10) - 55;
                int buttonY = panelY + yOffset + 5;
                int buttonWidth = 50;
                int buttonHeight = 18;
                
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                    mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                    cancelQuest(quest);
                    return true;
                }
                yOffset += ACCEPTED_QUEST_ENTRY_HEIGHT; // Use consistent spacing
            }
        }
        
        return false;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        
        // Only render tooltips if quest panel is not showing to prevent interference
        if (!this.showingQuests) {
            this.renderTooltip(poseStack, mouseX, mouseY);
        }
        
        // Render quest panel AFTER everything else to ensure it appears on top
        if (this.showingQuests) {
            // Push matrix with very high z-index to ensure it renders above everything
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000); // Very high z-index to be above tooltips and item counts
            renderQuestPanel(poseStack, mouseX, mouseY);
            poseStack.popPose();
        }
    }

    private void renderQuestPanel(PoseStack poseStack, int mouseX, int mouseY) {
        // Calculate panel dimensions - make it more adaptive to screen size
        int panelWidth = 220;
        int maxPanelHeight = 300; // Increased from 240 to 300 for more quest space
        
        // Calculate available vertical space and adjust panel height if needed
        int availableHeight = this.height - 20; // Leave 10px margin on top and bottom
        int panelHeight = Math.min(maxPanelHeight, availableHeight);
        
        // Try multiple positioning strategies to keep panel on screen
        int panelX, panelY;
        
        // Strategy 1: Right side of GUI (preferred)
        panelX = this.leftPos + this.imageWidth + 5;
        if (panelX + panelWidth <= this.width - 5) {
            // Fits on right side - use it
        } 
        // Strategy 2: Left side of GUI
        else if (this.leftPos - panelWidth - 5 >= 5) {
            panelX = this.leftPos - panelWidth - 5;
        }
        // Strategy 3: Overlay on top-right of GUI (fallback)
        else {
            panelX = this.leftPos + this.imageWidth - panelWidth;
        }
        
        // Position vertically - try to center but ensure it fits on screen
        panelY = this.topPos + (this.imageHeight - panelHeight) / 2;
        
        // Ensure panel doesn't go off screen vertically with stricter bounds
        panelY = Math.max(10, Math.min(panelY, this.height - panelHeight - 10));
        
        // Draw panel background with border - make it completely opaque to block all content behind
        fill(poseStack, panelX - 3, panelY - 3, panelX + panelWidth + 3, panelY + panelHeight + 3, 0xFF000000); // Thicker black border
        fill(poseStack, panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFF404040); // Gray border
        fill(poseStack, panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY + panelHeight + 1, 0xFF2A2A2A); // Dark border
        fill(poseStack, panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E1E1E); // Fully opaque dark background
        
        // Quest panel title
        this.font.draw(poseStack, Component.literal("Available Quests"), panelX + 5, panelY + 5, 0xFFFFFF);
        
        // Render available quests with scrolling
        ClientQuestCache cache = ClientQuestCache.getInstance();
        Set<DailyQuest> available = cache.getAvailableQuests();
        int availableSectionHeight = (panelHeight - 40) / 2; // Split remaining space between sections
        int acceptedSectionStart = 20 + availableSectionHeight + 15; // Leave space for section header
        
        if (available != null && !available.isEmpty()) {
            List<DailyQuest> quests = createOrderedQuestList(available);
            int yOffset = 20;
            int startIndex = availableQuestScroll;
            int visibleQuests = Math.min(QUESTS_PER_PAGE, (availableSectionHeight - 20) / QUEST_ENTRY_HEIGHT);
            int endIndex = Math.min(startIndex + visibleQuests, quests.size());
            
            for (int i = startIndex; i < endIndex; i++) {
                DailyQuest quest = quests.get(i);
                if (panelY + yOffset + QUEST_ENTRY_HEIGHT <= panelY + acceptedSectionStart - 5) {
                    renderQuestEntry(poseStack, quest, panelX + 5, panelY + yOffset, panelWidth - 10, mouseX, mouseY);
                    yOffset += QUEST_ENTRY_HEIGHT;
                }
            }
            
            // Draw scroll indicators if needed
            if (quests.size() > visibleQuests) {
                renderScrollIndicator(poseStack, panelX + panelWidth - 10, panelY + 20, availableSectionHeight - 20, 
                    availableQuestScroll, quests.size(), visibleQuests);
            }
        } else {
            this.font.draw(poseStack, Component.literal("No quests available"), panelX + 5, panelY + 25, 0x888888);
        }
        
        // Render accepted quests section
        this.font.draw(poseStack, Component.literal("Your Quests"), panelX + 5, panelY + acceptedSectionStart, 0xFFFFFF);
        
        Set<DailyQuest> accepted = cache.getAcceptedQuests();
        if (accepted != null && !accepted.isEmpty()) {
            List<DailyQuest> quests = createOrderedQuestList(accepted);
            int yOffset = acceptedSectionStart + 15;
            int startIndex = acceptedQuestScroll;
            int availableHeightForAccepted = panelHeight - acceptedSectionStart - 20;
            int visibleQuests = Math.min(QUESTS_PER_PAGE, availableHeightForAccepted / ACCEPTED_QUEST_ENTRY_HEIGHT);
            int endIndex = Math.min(startIndex + visibleQuests, quests.size());
            
            for (int i = startIndex; i < endIndex; i++) {
                DailyQuest quest = quests.get(i);
                if (panelY + yOffset + ACCEPTED_QUEST_ENTRY_HEIGHT <= panelY + panelHeight - 5) {
                    renderAcceptedQuestEntry(poseStack, quest, panelX + 5, panelY + yOffset, panelWidth - 10, mouseX, mouseY);
                    yOffset += ACCEPTED_QUEST_ENTRY_HEIGHT;
                }
            }
            
            // Draw scroll indicators if needed
            if (quests.size() > visibleQuests) {
                renderScrollIndicator(poseStack, panelX + panelWidth - 10, panelY + acceptedSectionStart + 15, 
                    availableHeightForAccepted, acceptedQuestScroll, quests.size(), visibleQuests);
            }
        } else {
            int noQuestsY = Math.min(panelY + acceptedSectionStart + 15, panelY + panelHeight - 20);
            this.font.draw(poseStack, Component.literal("No active quests"), panelX + 5, noQuestsY, 0x888888);
        }
    }

    private void renderQuestEntry(PoseStack poseStack, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        // Quest background (taller for better readability)
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 30;
        fill(poseStack, x, y, x + width, y + 30, hovered ? 0xFF4A4A4A : 0xFF3A3A3A);
        
        // Quest name
        String questName = this.font.plainSubstrByWidth(quest.name, width - 65);
        this.font.draw(poseStack, Component.literal(questName), x + 2, y + 3, 0xFFFFFF);
        
        // Quest description
        String questDesc = this.font.plainSubstrByWidth(quest.description, width - 65);
        this.font.draw(poseStack, Component.literal(questDesc), x + 2, y + 14, 0xCCCCCC);
        
        // Accept button
        int buttonX = x + width - 55;
        int buttonY = y + 5;
        int buttonWidth = 50;
        int buttonHeight = 18;
        
        boolean acceptHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        
        fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 
             acceptHovered ? 0xFF4A8F4A : 0xFF2A6F2A);
        fill(poseStack, buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1,
             acceptHovered ? 0xFF5AA05A : 0xFF3A7F3A);
        
        this.font.draw(poseStack, Component.literal("Accept"), buttonX + 7, buttonY + 6, 0xFFFFFF);
    }

    private void renderAcceptedQuestEntry(PoseStack poseStack, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        // Quest background (taller for better readability)
        fill(poseStack, x, y, x + width, y + 35, 0xFF1E3A1E);
        
        // Quest name with progress
        String questText = quest.name + " (" + quest.progress + "/" + quest.target.quantity + ")";
        String displayText = this.font.plainSubstrByWidth(questText, width - 65);
        this.font.draw(poseStack, Component.literal(displayText), x + 2, y + 3, 0x88FF88);
        
        // Quest description (added)
        if (quest.description != null && !quest.description.isEmpty()) {
            String questDesc = this.font.plainSubstrByWidth(quest.description, width - 65);
            this.font.draw(poseStack, Component.literal(questDesc), x + 2, y + 14, 0xCCCCCC);
        }
        
        // Progress bar (moved down)
        int progressBarWidth = width - 75;
        int progressWidth = (int) (progressBarWidth * ((double) quest.progress / quest.target.quantity));
        fill(poseStack, x + 2, y + 25, x + 2 + progressBarWidth, y + 28, 0xFF444444);
        fill(poseStack, x + 2, y + 25, x + 2 + progressWidth, y + 28, 0xFF88FF88);
        
        // Cancel button
        int buttonX = x + width - 55;
        int buttonY = y + 5;
        int buttonWidth = 50;
        int buttonHeight = 18;
        
        boolean cancelHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        
        fill(poseStack, buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 
             cancelHovered ? 0xFF8F4A4A : 0xFF6F2A2A);
        fill(poseStack, buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1,
             cancelHovered ? 0xFFA05A5A : 0xFF7F3A3A);
        
        this.font.draw(poseStack, Component.literal("Cancel"), buttonX + 7, buttonY + 6, 0xFFFFFF);
    }
    
    private void renderScrollIndicator(PoseStack poseStack, int x, int y, int height, 
                                     int scrollPos, int totalItems, int visibleItems) {
        if (totalItems <= visibleItems) return;
        
        // Background track
        fill(poseStack, x, y, x + 6, y + height, 0xFF666666);
        
        // Calculate scrollbar thumb size and position
        float thumbRatio = (float) visibleItems / totalItems;
        int thumbHeight = Math.max(10, (int)(height * thumbRatio));
        
        float scrollRatio = (float) scrollPos / (totalItems - visibleItems);
        int thumbY = y + (int)((height - thumbHeight) * scrollRatio);
        
        // Scrollbar thumb
        fill(poseStack, x + 1, thumbY, x + 5, thumbY + thumbHeight, 0xFFAAAAAA);
    }

    /**
     * Send quest acceptance request to server through packet system
     */
    private void acceptQuest(DailyQuest quest) {
        if (quest != null && quest.id != null) {
            // Use the PacketHandler to send quest command to server
            net.torocraft.dailies.network.PacketHandler.questCommand(quest.id, 
                net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand.ACCEPT);
            
            // Request fresh quest data immediately after sending command
            PacketHandler.getQuests(QuestsFilter.AVAILABLE);
            PacketHandler.getQuests(QuestsFilter.ACCEPTED);
            
            // Also schedule a delayed refresh to catch any server processing delays
            if (this.minecraft != null) {
                this.minecraft.tell(() -> {
                    try {
                        Thread.sleep(200); // Wait for server processing
                        PacketHandler.getQuests(QuestsFilter.AVAILABLE);
                        PacketHandler.getQuests(QuestsFilter.ACCEPTED);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }

    /**
     * Send quest cancellation request to server through packet system
     */
    private void cancelQuest(DailyQuest quest) {
        if (quest != null && quest.id != null) {
            System.out.println("GUI: Canceling quest: " + quest.id + " - " + quest.name);
            
            // Use the PacketHandler to send quest command to server
            net.torocraft.dailies.network.PacketHandler.questCommand(quest.id, 
                net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand.ABANDON);
            
            // Request fresh quest data immediately after sending command
            PacketHandler.getQuests(QuestsFilter.AVAILABLE);
            PacketHandler.getQuests(QuestsFilter.ACCEPTED);
            
            // Also schedule a delayed refresh to catch any server processing delays
            if (this.minecraft != null) {
                this.minecraft.tell(() -> {
                    try {
                        Thread.sleep(200); // Wait for server processing
                        System.out.println("GUI: Requesting quest refresh after cancel");
                        PacketHandler.getQuests(QuestsFilter.AVAILABLE);
                        PacketHandler.getQuests(QuestsFilter.ACCEPTED);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }
}
