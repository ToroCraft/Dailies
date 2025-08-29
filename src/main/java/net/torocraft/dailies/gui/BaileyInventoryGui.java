package net.torocraft.dailies.gui;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.client.ClientQuestCache;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaileyInventoryGui extends AbstractContainerScreen<DailiesContainer> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DailiesMod.MODID, "textures/gui/bailey_gui.png");

    private boolean showingQuests = false;
    
    // Scrolling variables
    private int availableQuestScroll = 0;
    private int acceptedQuestScroll = 0;
    private static final int QUESTS_PER_PAGE = 3; 
    private static final int QUEST_ENTRY_HEIGHT = 25;
    private static final int ACCEPTED_QUEST_ENTRY_HEIGHT = 30;

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
        this.addRenderableWidget(Button.builder(Component.literal("View Quests"), 
            (button) -> {
                this.showingQuests = !this.showingQuests;
                button.setMessage(Component.literal(this.showingQuests ? "Hide Quests" : "View Quests"));
                if (this.showingQuests) {
                    // Request fresh quest data when showing quests
                    PacketHandler.getQuests(QuestsFilter.AVAILABLE);
                    PacketHandler.getQuests(QuestsFilter.ACCEPTED);
                }
            }).bounds(this.leftPos + this.imageWidth + 5, this.topPos + 10, 80, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            this.leftPos, this.topPos,
            0, 0,
            this.imageWidth, this.imageHeight,
            256, 256
        );
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.literal("Bailey's Shop"), 8, 6, 0xFF404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, 38, 0xFF404040, false);
    }

        @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.showingQuests) {
            boolean handled = handleQuestPanelClick(mouseX, mouseY);
            return handled || super.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.showingQuests) {
            // Check if mouse is over quest panel - use same logic as renderQuestPanel
            int panelWidth = 280; // Increased from 220 to 280 for wider panel
            int maxPanelHeight = 500; // Increased from 300 to 500 for much more quest space
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
                            availableQuestScroll - (int)(scrollY * 2)));
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
                            acceptedQuestScroll - (int)(scrollY * 2)));
                    }
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean handleQuestPanelClick(double mouseX, double mouseY) {
        // Calculate panel position (same logic as renderQuestPanel)
        int panelWidth = 280; // Increased from 220 to 280 for wider panel
        int maxPanelHeight = 500; // Increased from 300 to 500 for much more quest space
        
        // Calculate available vertical space and adjust panel height if needed
        int availableHeight = this.height - 20; // Leave 10px margin on top and bottom
        int panelHeight = Math.min(maxPanelHeight, availableHeight);
        
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
        
        // Position vertically - try to center but ensure it fits on screen
        panelY = this.topPos + (this.imageHeight - panelHeight) / 2;
        
        // Ensure panel doesn't go off screen vertically with stricter bounds
        panelY = Math.max(10, Math.min(panelY, this.height - panelHeight - 10));

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
                int buttonX = panelX + 5 + (panelWidth - 10) - 65;
                int buttonY = panelY + yOffset + 3;
                int buttonWidth = 60;
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
                
                // Only process click if the quest would be rendered (match rendering bounds check)
                if (panelY + yOffset + ACCEPTED_QUEST_ENTRY_HEIGHT <= panelY + panelHeight - 5) {
                    // Cancel button bounds (match renderAcceptedQuestEntry)
                    int buttonX = panelX + 5 + (panelWidth - 10) - 65;
                    int buttonY = panelY + yOffset + 3;
                    int buttonWidth = 60;
                    int buttonHeight = 18;
                    
                    if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                        mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                        cancelQuest(quest);
                        return true;
                    }
                }
                yOffset += ACCEPTED_QUEST_ENTRY_HEIGHT; // Use consistent spacing
            }
        }
        
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        
        // Only render tooltips if quest panel is not showing to prevent interference
        if (!this.showingQuests) {
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
        
        // Render quest panel AFTER everything else to ensure it appears on top
        if (this.showingQuests) {
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(0.0f, 0.0f);
            renderQuestPanel(guiGraphics, mouseX, mouseY);
            guiGraphics.pose().popMatrix();
        }
    }

    private void renderQuestPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Calculate panel dimensions - make it more adaptive to screen size
        int panelWidth = 280; // Increased from 220 to 280 for wider panel
        int maxPanelHeight = 500; // Increased from 300 to 500 for much more quest space
        
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
        guiGraphics.fill(panelX - 3, panelY - 3, panelX + panelWidth + 3, panelY + panelHeight + 3, 0xFF000000); // Thicker black border
        guiGraphics.fill(panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFF404040); // Gray border
        guiGraphics.fill(panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY + panelHeight + 1, 0xFF2A2A2A); // Dark border
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E1E1E); // Fully opaque dark background
        
        guiGraphics.nextStratum();
        
        // Quest panel title
        guiGraphics.drawString(this.font, Component.literal("Available Quests"), panelX + 5, panelY + 5, 0xFFFFFFFF, false);
        
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
                    renderQuestEntry(guiGraphics, quest, panelX + 5, panelY + yOffset, panelWidth - 10, mouseX, mouseY);
                    yOffset += QUEST_ENTRY_HEIGHT;
                }
            }
            
            // Draw scroll indicators if needed
            if (quests.size() > visibleQuests) {
                renderScrollIndicator(guiGraphics, panelX + panelWidth - 10, panelY + 20, availableSectionHeight - 20, 
                    availableQuestScroll, quests.size(), visibleQuests);
            }
        } else {
            guiGraphics.drawString(this.font, Component.literal("No quests available"), panelX + 5, panelY + 25, 0xFF888888, false);
        }
        
        // Render accepted quests section
        guiGraphics.drawString(this.font, Component.literal("Your Quests"), panelX + 5, panelY + acceptedSectionStart, 0xFFFFFFFF, false);
        
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
                    renderAcceptedQuestEntry(guiGraphics, quest, panelX + 5, panelY + yOffset, panelWidth - 10, mouseX, mouseY);
                    yOffset += ACCEPTED_QUEST_ENTRY_HEIGHT;
                }
            }
            
            // Draw scroll indicators if needed
            if (quests.size() > visibleQuests) {
                renderScrollIndicator(guiGraphics, panelX + panelWidth - 10, panelY + acceptedSectionStart + 15, 
                    availableHeightForAccepted, acceptedQuestScroll, quests.size(), visibleQuests);
            }
        } else {
            int noQuestsY = Math.min(panelY + acceptedSectionStart + 15, panelY + panelHeight - 20);
            guiGraphics.drawString(this.font, Component.literal("No active quests"), panelX + 5, noQuestsY, 0xFF888888, false);
        }
    }

    private void renderQuestEntry(GuiGraphics guiGraphics, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 25;
        guiGraphics.fill(x, y, x + width, y + 25, hovered ? 0xFF4A4A4A : 0xFF3A3A3A);
        
        int buttonX = x + width - 65;
        int buttonY = y + 3;
        int buttonWidth = 60;
        int buttonHeight = 18;
        
        boolean acceptHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        
        guiGraphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 
             acceptHovered ? 0xFF4A8F4A : 0xFF2A6F2A);
        guiGraphics.fill(buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1,
             acceptHovered ? 0xFF5AA05A : 0xFF3A7F3A);
        
        guiGraphics.nextStratum();
        
        String questName = this.font.plainSubstrByWidth(quest.name, width - 75);
        guiGraphics.drawCenteredString(this.font, questName, x + (width - 75) / 2, y + 2, 0xFFFFFFFF);
        
        String questDesc = this.font.plainSubstrByWidth(quest.description, width - 75);
        guiGraphics.drawCenteredString(this.font, questDesc, x + (width - 75) / 2, y + 12, 0xFFCCCCCC);
        
        guiGraphics.drawCenteredString(this.font, "Accept", buttonX + buttonWidth / 2, buttonY + 6, 0xFFFFFFFF);
        
        guiGraphics.fill(x, y + 24, x + width, y + 25, 0xFF555555);
    }

    private void renderAcceptedQuestEntry(GuiGraphics guiGraphics, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        guiGraphics.fill(x, y, x + width, y + 30, 0xFF1E3A1E);
        
        // Progress bar (adjusted for new height)
        int progressBarWidth = width - 85;
        int progressWidth = (int) (progressBarWidth * ((double) quest.progress / quest.target.quantity));
        guiGraphics.fill(x + 2, y + 22, x + 2 + progressBarWidth, y + 25, 0xFF444444);
        guiGraphics.fill(x + 2, y + 22, x + 2 + progressWidth, y + 25, 0xFF88FF88);
        
        int buttonX = x + width - 65;
        int buttonY = y + 3;
        int buttonWidth = 60;
        int buttonHeight = 18;
        
        boolean cancelHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        
        guiGraphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 
             cancelHovered ? 0xFF8F4A4A : 0xFF6F2A2A);
        guiGraphics.fill(buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1,
             cancelHovered ? 0xFFA05A5A : 0xFF7F3A3A);
        
        guiGraphics.nextStratum();
        
        String questText = quest.name + " (" + quest.progress + "/" + quest.target.quantity + ")";
        String displayText = this.font.plainSubstrByWidth(questText, width - 75);
        guiGraphics.drawCenteredString(this.font, displayText, x + (width - 75) / 2, y + 2, 0xFF88FF88);
        
        if (quest.description != null && !quest.description.isEmpty()) {
            String questDesc = this.font.plainSubstrByWidth(quest.description, width - 75);
            guiGraphics.drawCenteredString(this.font, questDesc, x + (width - 75) / 2, y + 11, 0xFFCCCCCC);
        }
        
        guiGraphics.drawCenteredString(this.font, "Cancel", buttonX + buttonWidth / 2, buttonY + 6, 0xFFFFFFFF);
        
        guiGraphics.fill(x, y + 29, x + width, y + 30, 0xFF555555);
    }
    
    private void renderScrollIndicator(GuiGraphics guiGraphics, int x, int y, int height, 
                                     int scrollPos, int totalItems, int visibleItems) {
        if (totalItems <= visibleItems) return;
        
        // Background track
        guiGraphics.fill(x, y, x + 6, y + height, 0xFF666666);
        
        // Calculate scrollbar thumb size and position
        float thumbRatio = (float) visibleItems / totalItems;
        int thumbHeight = Math.max(10, (int)(height * thumbRatio));
        
        float scrollRatio = (float) scrollPos / (totalItems - visibleItems);
        int thumbY = y + (int)((height - thumbHeight) * scrollRatio);
        
        // Scrollbar thumb
        guiGraphics.fill(x + 1, thumbY, x + 5, thumbY + thumbHeight, 0xFFAAAAAA);
    }

    private void acceptQuest(DailyQuest quest) {
        if (quest != null && quest.id != null) {
            // Use the PacketHandler to send quest command to server
            net.torocraft.dailies.network.PacketHandler.questCommand(quest.id, 
                net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand.ACCEPT);
            
            // Request fresh quest data immediately after sending command
            PacketHandler.getQuests(QuestsFilter.AVAILABLE);
            PacketHandler.getQuests(QuestsFilter.ACCEPTED);
            
            if (this.minecraft != null) {
                this.minecraft.execute(() -> {
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

    private void cancelQuest(DailyQuest quest) {
        if (quest == null) {
            return;
        }
        
        if (quest.id == null) {
            return;
        }
        
        net.torocraft.dailies.network.PacketHandler.questCommand(quest.id, 
            net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand.ABANDON);
    }
}
