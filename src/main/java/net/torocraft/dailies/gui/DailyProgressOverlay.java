package net.torocraft.dailies.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.client.ClientQuestCache;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = DailiesMod.MODID, value = Dist.CLIENT)
public class DailyProgressOverlay {
    private static final int QUESTS_PER_PAGE = 5;
    private static final int BADGE_WIDTH = 120;
    private static final int BADGE_HEIGHT = 28;
    private static final int PADDING = 5;
    private static int offsetAccepted = 0;
    private static int offsetAvailable = 0;
    
    // State for inventory quest toggle
    private static boolean showQuestsInInventory = false;
    private static Button questToggleButton = null;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {
            // Add quest toggle button to inventory screens
            int buttonX = event.getScreen().width - 90;
            int buttonY = 10;
            
            questToggleButton = Button.builder(Component.literal("Show Quests"), 
                (button) -> {
                    showQuestsInInventory = !showQuestsInInventory;
                    button.setMessage(Component.literal(showQuestsInInventory ? "Hide Quests" : "Show Quests"));
                    if (showQuestsInInventory) {
                        // Request fresh quest data when showing quests
                        PacketHandler.getQuests(QuestsFilter.AVAILABLE);
                        PacketHandler.getQuests(QuestsFilter.ACCEPTED);
                    }
                }).bounds(buttonX, buttonY, 80, 20).build();
            
            event.addListener(questToggleButton);
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        // Handle inventory screen quest overlay rendering
        if ((event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) 
            && showQuestsInInventory) {
            
            // Render with very high z-index to be above blur effects
            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().pose().translate(0.0D, 0.0D, 2000.0D); // Even higher z-index
            
            renderQuestOverlay(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
            
            event.getGuiGraphics().pose().popPose();
        }
    }

        @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            return;
        }
        
        // Skip inventory screens - they're handled by onScreenRender
        if (mc.screen instanceof InventoryScreen || mc.screen instanceof CreativeModeInventoryScreen) {
            return;
        }
        
        // Don't show overlay on specific screens
        if (mc.screen instanceof ChatScreen ||
            mc.screen instanceof BaileyInventoryGui ||
            mc.screen instanceof DailiesGuiContainer ||
            mc.screen instanceof net.torocraft.dailies.config.ConfigScreen) {
            return;
        }
        
        // Push matrix to render on top of everything including blur effects
        PoseStack poseStack = event.getGuiGraphics().pose();
        poseStack.pushPose();
        
        // Translate to a very high z-index to render on top of blur
        poseStack.translate(0.0D, 0.0D, 1000.0D);
        
        // Get mouse coordinates from Minecraft
        double mouseX = mc.mouseHandler.xpos() * (double)mc.getWindow().getGuiScaledWidth() / (double)mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos() * (double)mc.getWindow().getGuiScaledHeight() / (double)mc.getWindow().getScreenHeight();
        
        renderQuestOverlay(event.getGuiGraphics(), (int)mouseX, (int)mouseY);
        
        poseStack.popPose();
    }

    private static void renderQuestOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        renderAcceptedQuests(guiGraphics, mc, mouseX, mouseY);
        renderAvailableQuests(guiGraphics, mc, mouseX, mouseY);
    }

    private static void renderAcceptedQuests(GuiGraphics guiGraphics, Minecraft mc, int mouseX, int mouseY) {
        // Use ClientQuestCache instead of static fields
        ClientQuestCache cache = ClientQuestCache.getInstance();
        Set<DailyQuest> accepted = cache.getAcceptedQuests();
        
        if (accepted == null || accepted.isEmpty()) {
            return;
        }
        int x = mc.getWindow().getGuiScaledWidth() - BADGE_WIDTH - 5;
        int y = (mc.getWindow().getGuiScaledHeight() / 2) - ((BADGE_HEIGHT + PADDING) * QUESTS_PER_PAGE / 2);
        List<DailyQuest> quests = new ArrayList<>(accepted);
        for (int i = 0; i < QUESTS_PER_PAGE && i + offsetAccepted < quests.size(); i++) {
            DailyQuest quest = quests.get(i + offsetAccepted);
            GuiDailyBadge badge = new GuiDailyBadge(quest, x, y);
            badge.render(guiGraphics, mouseX, mouseY, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            y += BADGE_HEIGHT + PADDING;
        }
        // Paging buttons
        int btnY = y + 5;
        int btnX = x;
        boolean prevEnabled = offsetAccepted > 0;
        boolean nextEnabled = offsetAccepted + QUESTS_PER_PAGE < quests.size();
        if (drawPagingButton(guiGraphics, btnX, btnY, "< Prev", prevEnabled, mouseX, mouseY)) {
            if (prevEnabled) offsetAccepted = Math.max(0, offsetAccepted - QUESTS_PER_PAGE);
        }
        if (drawPagingButton(guiGraphics, btnX + 60, btnY, "Next >", nextEnabled, mouseX, mouseY)) {
            if (nextEnabled) offsetAccepted = Math.min(quests.size() - QUESTS_PER_PAGE, offsetAccepted + QUESTS_PER_PAGE);
        }
    }

    private static void renderAvailableQuests(GuiGraphics guiGraphics, Minecraft mc, int mouseX, int mouseY) {
        // Use ClientQuestCache instead of static fields
        ClientQuestCache cache = ClientQuestCache.getInstance();
        Set<DailyQuest> available = cache.getAvailableQuests();
        
        if (available == null || available.isEmpty()) {
            return;
        }
        int x = 5;
        int y = (mc.getWindow().getGuiScaledHeight() / 2) - ((BADGE_HEIGHT + PADDING) * QUESTS_PER_PAGE / 2);
        List<DailyQuest> quests = new ArrayList<>(available);
        for (int i = 0; i < QUESTS_PER_PAGE && i + offsetAvailable < quests.size(); i++) {
            DailyQuest quest = quests.get(i + offsetAvailable);
            GuiDailyBadge badge = new GuiDailyBadge(quest, x, y);
            badge.render(guiGraphics, mouseX, mouseY, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            y += BADGE_HEIGHT + PADDING;
        }
        // Paging buttons
        int btnY = y + 5;
        int btnX = x;
        boolean prevEnabled = offsetAvailable > 0;
        boolean nextEnabled = offsetAvailable + QUESTS_PER_PAGE < quests.size();
        if (drawPagingButton(guiGraphics, btnX, btnY, "< Prev", prevEnabled, mouseX, mouseY)) {
            if (prevEnabled) offsetAvailable = Math.max(0, offsetAvailable - QUESTS_PER_PAGE);
        }
        if (drawPagingButton(guiGraphics, btnX + 60, btnY, "Next >", nextEnabled, mouseX, mouseY)) {
            if (nextEnabled) offsetAvailable = Math.min(quests.size() - QUESTS_PER_PAGE, offsetAvailable + QUESTS_PER_PAGE);
        }
    }
    // Simple button rendering and click detection for paging
    private static boolean drawPagingButton(GuiGraphics guiGraphics, int x, int y, String label, boolean enabled, int mouseX, int mouseY) {
        int width = 54, height = 18;
        int color = enabled ? 0xFFAAAAAA : 0xFF555555;
        guiGraphics.fill(x, y, x + width, y + height, color);
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.gui.Font font = mc.font;
        int textColor = enabled ? 0xFFFFFFFF : 0xFF888888;
        guiGraphics.drawString(font, label, x + 6, y + 5, textColor);
        boolean hovered = enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        // Only page on left mouse click
        if (hovered && mc.mouseHandler.isLeftPressed()) {
            return true;
        }
        return false;
    }
}
