package net.torocraft.dailies.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.torocraft.dailies.client.ClientQuestCache;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// @EventBusSubscriber(modid = DailiesMod.MODID, value = Dist.CLIENT) // Disabled for now - keeping for future reference
public class InventoryQuestOverlay {
    private static final int QUESTS_PER_PAGE = 5;
    private static final int QUEST_ENTRY_HEIGHT = 35;
    private static final int ACCEPTED_QUEST_ENTRY_HEIGHT = 40;
    private static int availableQuestScroll = 0;
    private static int acceptedQuestScroll = 0;
    
    private static boolean showingQuests = false;
    
    private static int getLeftPos(AbstractContainerScreen<?> screen) {
        try {
            Field field = AbstractContainerScreen.class.getDeclaredField("leftPos");
            field.setAccessible(true);
            return field.getInt(screen);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private static int getTopPos(AbstractContainerScreen<?> screen) {
        try {
            Field field = AbstractContainerScreen.class.getDeclaredField("topPos");
            field.setAccessible(true);
            return field.getInt(screen);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private static int getImageWidth(AbstractContainerScreen<?> screen) {
        try {
            Field field = AbstractContainerScreen.class.getDeclaredField("imageWidth");
            field.setAccessible(true);
            return field.getInt(screen);
        } catch (Exception e) {
            return 176;
        }
    }
    
    private static int getImageHeight(AbstractContainerScreen<?> screen) {
        try {
            Field field = AbstractContainerScreen.class.getDeclaredField("imageHeight");
            field.setAccessible(true);
            return field.getInt(screen);
        } catch (Exception e) {
            return 166;
        }
    }
    
    private static Button questToggleButton = null;
    
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();
            
            questToggleButton = Button.builder(Component.literal("Show Quests"), 
                (button) -> {
                    showingQuests = !showingQuests;
                    button.setMessage(Component.literal(showingQuests ? "Hide Quests" : "Show Quests"));
                    if (showingQuests) {
                        PacketHandler.getQuests(QuestsFilter.AVAILABLE);
                        PacketHandler.getQuests(QuestsFilter.ACCEPTED);
                    }
                }).bounds(getLeftPos(screen) + getImageWidth(screen) + 5, getTopPos(screen) + 10, 80, 20).build();
            
            event.addListener(questToggleButton);
        }
    }
    
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!showingQuests) return;
        
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();
            
            event.getGuiGraphics().pose().pushMatrix();
            event.getGuiGraphics().pose().translate(0, 0);
            
            renderQuestPanel(event.getGuiGraphics(), screen, event.getMouseX(), event.getMouseY());
            
            event.getGuiGraphics().pose().popMatrix();
        }
    }
    
    @SubscribeEvent
    public static void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!showingQuests) return;
        
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();
            
            if (event.getButton() == 0) {
                boolean handled = handleQuestPanelClick(screen, event.getMouseX(), event.getMouseY());
                if (handled) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onScreenMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (!showingQuests) return;
        
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();
            
            if (isMouseOverQuestPanel(screen, event.getMouseX(), event.getMouseY())) {
                handleQuestPanelScroll(screen, event.getMouseX(), event.getMouseY(), event.getScrollDeltaY());
                event.setCanceled(true);
            }
        }
    }
    
    private static void renderQuestPanel(GuiGraphics guiGraphics, AbstractContainerScreen<?> screen, int mouseX, int mouseY) {
        int panelWidth = 220;
        int maxPanelHeight = 300;
        int availableHeight = screen.height - 20;
        int panelHeight = Math.min(maxPanelHeight, availableHeight);
        
        int panelX = getLeftPos(screen) + getImageWidth(screen) + 5;
        if (panelX + panelWidth > screen.width - 5) {
            if (getLeftPos(screen) - panelWidth - 5 >= 5) {
                panelX = getLeftPos(screen) - panelWidth - 5;
            } else {
                panelX = getLeftPos(screen) + getImageWidth(screen) - panelWidth;
            }
        }
        
        int panelY = getTopPos(screen) + (getImageHeight(screen) - panelHeight) / 2;
        panelY = Math.max(10, Math.min(panelY, screen.height - panelHeight - 10));
        
        guiGraphics.fill(panelX - 3, panelY - 3, panelX + panelWidth + 3, panelY + panelHeight + 3, 0xFF000000);
        guiGraphics.fill(panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFF404040);
        guiGraphics.fill(panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY + panelHeight + 1, 0xFF2A2A2A);
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1E1E1E);
        
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Available Quests"), panelX + 5, panelY + 5, 0xFFFFFF, false);
        
        ClientQuestCache cache = ClientQuestCache.getInstance();
        Set<DailyQuest> available = cache.getAvailableQuests();
        int availableSectionHeight = (panelHeight - 40) / 2;
        int acceptedSectionStart = 20 + availableSectionHeight + 15;
        
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
            
            if (quests.size() > visibleQuests) {
                renderScrollIndicator(guiGraphics, panelX + panelWidth - 10, panelY + 20, availableSectionHeight - 20, 
                    availableQuestScroll, quests.size(), visibleQuests);
            }
        } else {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("No quests available"), panelX + 5, panelY + 25, 0x888888, false);
        }
        
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Your Quests"), panelX + 5, panelY + acceptedSectionStart, 0xFFFFFF, false);
        
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
            
            if (quests.size() > visibleQuests) {
                renderScrollIndicator(guiGraphics, panelX + panelWidth - 10, panelY + acceptedSectionStart + 15, 
                    availableHeightForAccepted, acceptedQuestScroll, quests.size(), visibleQuests);
            }
        } else {
            int noQuestsY = Math.min(panelY + acceptedSectionStart + 15, panelY + panelHeight - 20);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("No active quests"), panelX + 5, noQuestsY, 0x888888, false);
        }
    }
    
    private static void renderQuestEntry(GuiGraphics guiGraphics, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 30;
        guiGraphics.fill(x, y, x + width, y + 30, hovered ? 0xFF4A4A4A : 0xFF3A3A3A);
        
        String questName = Minecraft.getInstance().font.plainSubstrByWidth(quest.name, width - 65);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(questName), x + 2, y + 3, 0xFFFFFF, false);
        
        String questDesc = Minecraft.getInstance().font.plainSubstrByWidth(quest.description, width - 65);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(questDesc), x + 2, y + 14, 0xCCCCCC, false);
        
        int buttonX = x + width - 55;
        int buttonY = y + 5;
        int buttonWidth = 50;
        int buttonHeight = 18;
        
        boolean acceptHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        
        guiGraphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 
             acceptHovered ? 0xFF4A8F4A : 0xFF2A6F2A);
        guiGraphics.fill(buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1,
             acceptHovered ? 0xFF5AA05A : 0xFF3A7F3A);
        
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Accept"), buttonX + 7, buttonY + 6, 0xFFFFFF, false);
    }
    
    private static void renderAcceptedQuestEntry(GuiGraphics guiGraphics, DailyQuest quest, int x, int y, int width, int mouseX, int mouseY) {
        guiGraphics.fill(x, y, x + width, y + 35, 0xFF1E3A1E);
        
        String questText = quest.name + " (" + quest.progress + "/" + quest.target.quantity + ")";
        String displayText = Minecraft.getInstance().font.plainSubstrByWidth(questText, width - 65);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(displayText), x + 2, y + 3, 0x88FF88, false);
        
        if (quest.description != null && !quest.description.isEmpty()) {
            String questDesc = Minecraft.getInstance().font.plainSubstrByWidth(quest.description, width - 65);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(questDesc), x + 2, y + 14, 0xCCCCCC, false);
        }
        
        int progressBarWidth = width - 75;
        int progressWidth = (int) (progressBarWidth * ((double) quest.progress / quest.target.quantity));
        guiGraphics.fill(x + 2, y + 25, x + 2 + progressBarWidth, y + 28, 0xFF444444);
        guiGraphics.fill(x + 2, y + 25, x + 2 + progressWidth, y + 28, 0xFF88FF88);
        
        int buttonX = x + width - 55;
        int buttonY = y + 5;
        int buttonWidth = 50;
        int buttonHeight = 18;
        
        boolean cancelHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        
        guiGraphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 
             cancelHovered ? 0xFF8F4A4A : 0xFF6F2A2A);
        guiGraphics.fill(buttonX + 1, buttonY + 1, buttonX + buttonWidth - 1, buttonY + buttonHeight - 1,
             cancelHovered ? 0xFFA05A5A : 0xFF7F3A3A);
        
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("Cancel"), buttonX + 7, buttonY + 6, 0xFFFFFF, false);
    }
    
    private static void renderScrollIndicator(GuiGraphics guiGraphics, int x, int y, int height, 
                                             int scrollPos, int totalItems, int visibleItems) {
        if (totalItems <= visibleItems) return;
        
        guiGraphics.fill(x, y, x + 6, y + height, 0xFF666666);
        
        float thumbRatio = (float) visibleItems / totalItems;
        int thumbHeight = Math.max(10, (int)(height * thumbRatio));
        
        float scrollRatio = (float) scrollPos / (totalItems - visibleItems);
        int thumbY = y + (int)((height - thumbHeight) * scrollRatio);
        
        guiGraphics.fill(x + 1, thumbY, x + 5, thumbY + thumbHeight, 0xFFAAAAAA);
    }
    
    private static boolean handleQuestPanelClick(AbstractContainerScreen<?> screen, double mouseX, double mouseY) {
        int panelWidth = 220;
        int maxPanelHeight = 300;
        int availableHeight = screen.height - 20;
        int panelHeight = Math.min(maxPanelHeight, availableHeight);
        
        int panelX = getLeftPos(screen) + getImageWidth(screen) + 5;
        if (panelX + panelWidth > screen.width - 5) {
            if (getLeftPos(screen) - panelWidth - 5 >= 5) {
                panelX = getLeftPos(screen) - panelWidth - 5;
            } else {
                panelX = getLeftPos(screen) + getImageWidth(screen) - panelWidth;
            }
        }
        
        int panelY = getTopPos(screen) + (getImageHeight(screen) - panelHeight) / 2;
        panelY = Math.max(10, Math.min(panelY, screen.height - panelHeight - 10));
        
        if (mouseX < panelX || mouseX > panelX + panelWidth || mouseY < panelY || mouseY > panelY + panelHeight) {
            return false;
        }
        
        ClientQuestCache cache = ClientQuestCache.getInstance();
        
        Set<DailyQuest> available = cache.getAvailableQuests();
        if (available != null && !available.isEmpty()) {
            List<DailyQuest> quests = createOrderedQuestList(available);
            int availableSectionHeight = (panelHeight - 40) / 2;
            int visibleQuests = Math.min(QUESTS_PER_PAGE, (availableSectionHeight - 20) / QUEST_ENTRY_HEIGHT);
            int startIndex = availableQuestScroll;
            int endIndex = Math.min(startIndex + visibleQuests, quests.size());
            int yOffset = 20;
            
            for (int i = startIndex; i < endIndex; i++) {
                DailyQuest quest = quests.get(i);
                
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
                
                if (panelY + yOffset + ACCEPTED_QUEST_ENTRY_HEIGHT <= panelY + panelHeight - 5) {
                    int buttonX = panelX + 5 + (panelWidth - 10) - 55;
                    int buttonY = panelY + yOffset + 5;
                    int buttonWidth = 50;
                    int buttonHeight = 18;
                    
                    if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth && 
                        mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                        cancelQuest(quest);
                        return true;
                    }
                }
                yOffset += ACCEPTED_QUEST_ENTRY_HEIGHT;
            }
        }
        
        return false;
    }
    
    private static boolean isMouseOverQuestPanel(AbstractContainerScreen<?> screen, double mouseX, double mouseY) {
        int panelWidth = 220;
        int maxPanelHeight = 300;
        int availableHeight = screen.height - 20;
        int panelHeight = Math.min(maxPanelHeight, availableHeight);
        
        int panelX = getLeftPos(screen) + getImageWidth(screen) + 5;
        if (panelX + panelWidth > screen.width - 5) {
            if (getLeftPos(screen) - panelWidth - 5 >= 5) {
                panelX = getLeftPos(screen) - panelWidth - 5;
            } else {
                panelX = getLeftPos(screen) + getImageWidth(screen) - panelWidth;
            }
        }
        
        int panelY = getTopPos(screen) + (getImageHeight(screen) - panelHeight) / 2;
        panelY = Math.max(10, Math.min(panelY, screen.height - panelHeight - 10));
        
        return mouseX >= panelX && mouseX <= panelX + panelWidth && mouseY >= panelY && mouseY <= panelY + panelHeight;
    }
    
    private static void handleQuestPanelScroll(AbstractContainerScreen<?> screen, double mouseX, double mouseY, double scrollY) {
        int panelWidth = 220;
        int maxPanelHeight = 300;
        int availableHeight = screen.height - 20;
        int panelHeight = Math.min(maxPanelHeight, availableHeight);
        
        int panelX = getLeftPos(screen) + getImageWidth(screen) + 5;
        if (panelX + panelWidth > screen.width - 5) {
            if (getLeftPos(screen) - panelWidth - 5 >= 5) {
                panelX = getLeftPos(screen) - panelWidth - 5;
            } else {
                panelX = getLeftPos(screen) + getImageWidth(screen) - panelWidth;
            }
        }
        
        int panelY = getTopPos(screen) + (getImageHeight(screen) - panelHeight) / 2;
        panelY = Math.max(10, Math.min(panelY, screen.height - panelHeight - 10));
        
        int availableSectionHeight = (panelHeight - 40) / 2;
        int acceptedSectionStart = panelY + 20 + availableSectionHeight + 15;
        
        if (mouseY < acceptedSectionStart) {
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
    }
    
    private static void acceptQuest(DailyQuest quest) {
        if (quest != null && quest.id != null) {
            PacketHandler.questCommand(quest.id, 
                net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand.ACCEPT);
            
            PacketHandler.getQuests(QuestsFilter.AVAILABLE);
            PacketHandler.getQuests(QuestsFilter.ACCEPTED);
        }
    }
    
    private static void cancelQuest(DailyQuest quest) {
        if (quest != null && quest.id != null) {
            PacketHandler.questCommand(quest.id, 
                net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand.ABANDON);
            
            PacketHandler.getQuests(QuestsFilter.AVAILABLE);
            PacketHandler.getQuests(QuestsFilter.ACCEPTED);
        }
    }
    
    private static List<DailyQuest> createOrderedQuestList(Set<DailyQuest> questSet) {
        List<DailyQuest> quests = new ArrayList<>(questSet);
        quests.sort((a, b) -> {
            if (a.name == null && b.name == null) return 0;
            if (a.name == null) return 1;
            if (b.name == null) return -1;
            return a.name.compareTo(b.name);
        });
        return quests;
    }
}
