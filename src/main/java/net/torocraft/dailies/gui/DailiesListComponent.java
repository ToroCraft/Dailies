package net.torocraft.dailies.gui;
/*
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.java.games.input.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class BadgeDisplay {
    private static final ResourceLocation GUI = new ResourceLocation(DailiesMod.MODID + ":textures/gui/dailies_list_gui.png");
    private static ResourceLocation BADGE_TEXTURE = new ResourceLocation(DailiesMod.MODID + ":textures/gui/badge_bg.png");

    private final Minecraft mc;
    private final AbstractGui gui;

    private final DailyQuest quest;

    public BadgeDisplay(Minecraft mc, AbstractGui gui, DailyQuest quest) {
        this.mc = mc;
        this.gui = gui;
        this.quest = quest;


    }

    public void drawStringWithShadow(MatrixStack matrices, FontRenderer textRenderer, String text, int x, int y, int color) {
        gui.func_238476_c_(matrices, textRenderer, text, x, y, color);
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        gui.func_238474_b_(matrices, x, y, u, v, width, height);
    }

    public int drawWithShadow(MatrixStack matrices, String text, float x, float y, int color) {
        return mc.fontRenderer.func_238405_a_(matrices, text, x, y, color);
    }

    public void draw(MatrixStack matrix) {
        int xOffset = 0;

        drawTexturedModalRect(x, y, 0, 0, width, height);
        drawTexturedModalRect(x + 6, y + 14, 0, 76, 108, 10);
        int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
        drawTexturedModalRect(x + 6, y + 14, 0, 86, progress, 10);

        String formattedQuestName = mc.fontRenderer.trimStringToWidth(quest.name, 110);
        drawCenteredString(mc.fontRenderer, formattedQuestName, x + 60, y + 5, 0xffffff);
        String barText = buildQuestProgressRatioString();
        if (Minecraft.getSystemTime() % 6000 < 3000) {
            barText = mc.fontRenderer.trimStringToWidth(quest.description, 110);
        }
        drawCenteredString(mc.fontRenderer, barText, x + 60, y + 15, 0xffffff);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        HealthBarRenderer.render(matrix, entity, 63, 14, 130, false);
        String name = getEntityName(entity);
        String health = (int) Math.ceil(entity.getHealth()) + "/" + (int) entity.getMaxHealth();

    }

    private void renderBadge(MatrixStack matrix, int x, int y) {
        mc.getTextureManager().bindTexture(BADGE_TEXTURE);
        drawTexture(matrix, x, y, 34, 9, 9, 9);
    }
}
 */
