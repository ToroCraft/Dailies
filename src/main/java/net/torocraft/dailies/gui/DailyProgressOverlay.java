package net.torocraft.dailies.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.network.packets.QuestsPacket;
import net.torocraft.dailies.network.packets.QuestProgressPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DailiesMod.MODID, value = Dist.CLIENT)
public class DailyProgressOverlay {
    private static final int QUESTS_PER_PAGE = 5;
    private static final int BADGE_WIDTH = 120;
    private static final int BADGE_HEIGHT = 28;
    private static final int PADDING = 5;
    private static int offsetAccepted = 0;
    private static int offsetAvailable = 0;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        PoseStack poseStack = event.getPoseStack();
        double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        // Only show quest overlay on player inventory screen, NOT on Bailey's inventory or other GUIs
        if (screen instanceof InventoryScreen) {
            renderAcceptedQuests(poseStack, mc, (int)mouseX, (int)mouseY);
            renderAvailableQuests(poseStack, mc, (int)mouseX, (int)mouseY);
        }
    }

    private static void renderAcceptedQuests(PoseStack poseStack, Minecraft mc, int mouseX, int mouseY) {
        // TODO: Replace with correct client-side quest cache for 1.18.2+
        // This is a placeholder. You should implement a client quest cache synced from the server.
        Set<DailyQuest> accepted = DailiesMod.acceptedQuests; // Use DailiesMod static field
        if (accepted == null || accepted.isEmpty()) {
            PacketHandler.getQuests(QuestsFilter.ACCEPTED);
            return;
        }
        int x = mc.getWindow().getGuiScaledWidth() - BADGE_WIDTH - 22;
        int y = (mc.getWindow().getGuiScaledHeight() / 2) - ((BADGE_HEIGHT + PADDING) * QUESTS_PER_PAGE / 2);
        List<DailyQuest> quests = new ArrayList<>(accepted);
        for (int i = 0; i < QUESTS_PER_PAGE && i + offsetAccepted < quests.size(); i++) {
            DailyQuest quest = quests.get(i + offsetAccepted);
            GuiDailyBadge badge = new GuiDailyBadge(quest, x, y);
            badge.render(poseStack, mouseX, mouseY, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            y += BADGE_HEIGHT + PADDING;
        }
        // Paging buttons
        int btnY = y + 5;
        int btnX = x;
        boolean prevEnabled = offsetAccepted > 0;
        boolean nextEnabled = offsetAccepted + QUESTS_PER_PAGE < quests.size();
        if (drawPagingButton(poseStack, btnX, btnY, "< Prev", prevEnabled, mouseX, mouseY)) {
            if (prevEnabled) offsetAccepted = Math.max(0, offsetAccepted - QUESTS_PER_PAGE);
        }
        if (drawPagingButton(poseStack, btnX + 60, btnY, "Next >", nextEnabled, mouseX, mouseY)) {
            if (nextEnabled) offsetAccepted = Math.min(quests.size() - QUESTS_PER_PAGE, offsetAccepted + QUESTS_PER_PAGE);
        }
    }

    private static void renderAvailableQuests(PoseStack poseStack, Minecraft mc, int mouseX, int mouseY) {
        // TODO: Replace with correct client-side quest cache for 1.18.2+
        // This is a placeholder. You should implement a client quest cache synced from the server.
        Set<DailyQuest> available = DailiesMod.availableQuests; // Use DailiesMod static field
        if (available == null || available.isEmpty()) {
            PacketHandler.getQuests(QuestsFilter.AVAILABLE);
            return;
        }
        int x = 5;
        int y = (mc.getWindow().getGuiScaledHeight() / 2) - ((BADGE_HEIGHT + PADDING) * QUESTS_PER_PAGE / 2);
        List<DailyQuest> quests = new ArrayList<>(available);
        for (int i = 0; i < QUESTS_PER_PAGE && i + offsetAvailable < quests.size(); i++) {
            DailyQuest quest = quests.get(i + offsetAvailable);
            GuiDailyBadge badge = new GuiDailyBadge(quest, x, y);
            badge.render(poseStack, mouseX, mouseY, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            y += BADGE_HEIGHT + PADDING;
        }
        // Paging buttons
        int btnY = y + 5;
        int btnX = x;
        boolean prevEnabled = offsetAvailable > 0;
        boolean nextEnabled = offsetAvailable + QUESTS_PER_PAGE < quests.size();
        if (drawPagingButton(poseStack, btnX, btnY, "< Prev", prevEnabled, mouseX, mouseY)) {
            if (prevEnabled) offsetAvailable = Math.max(0, offsetAvailable - QUESTS_PER_PAGE);
        }
        if (drawPagingButton(poseStack, btnX + 60, btnY, "Next >", nextEnabled, mouseX, mouseY)) {
            if (nextEnabled) offsetAvailable = Math.min(quests.size() - QUESTS_PER_PAGE, offsetAvailable + QUESTS_PER_PAGE);
        }
    }
    // Simple button rendering and click detection for paging
    private static boolean drawPagingButton(PoseStack poseStack, int x, int y, String label, boolean enabled, int mouseX, int mouseY) {
        int width = 54, height = 18;
        int color = enabled ? 0xFFAAAAAA : 0xFF555555;
        GuiComponent.fill(poseStack, x, y, x + width, y + height, color);
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.gui.Font font = mc.font;
        int textColor = enabled ? 0xFFFFFFFF : 0xFF888888;
        font.draw(poseStack, label, x + 6, y + 5, textColor);
        boolean hovered = enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        // Only page on left mouse click
        if (hovered && mc.mouseHandler.isLeftPressed()) {
            return true;
        }
        return false;
    }
}
