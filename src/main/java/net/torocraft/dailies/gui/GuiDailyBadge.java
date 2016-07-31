package net.torocraft.dailies.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.torocraft.dailies.quests.DailyQuest;

public class GuiDailyBadge extends Gui {

	private final DailyQuest quest;
	private final Minecraft mc;
	private int x;
	private int y;

	private int width = 120;
	private int height = 28;

	public GuiDailyBadge(final DailyQuest quest, final Minecraft mc, int x, int y) {
		this.quest = quest;
		this.mc = mc;
		this.x = x;
		this.y = y;
		draw();
	}

	public void draw() {
		ResourceLocation badgeTexture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");
		mc.renderEngine.bindTexture(badgeTexture);

		drawTexturedModalRect(x, y, 0, 0, width, height);
		drawTexturedModalRect(x + 6, y + 14, 0, 76, 108, 10);
		int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
		drawTexturedModalRect(x + 6, y + 14, 0, 86, progress, 10);

		String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
		drawCenteredString(mc.fontRendererObj, formattedQuestName, x + 60, y + 5, 0xffffff);
		String barText = quest.progress + "/" + quest.target.quantity;
		if (Minecraft.getSystemTime() % 6000 < 3000) {
			barText = mc.fontRendererObj.trimStringToWidth(quest.description, 110);
		}
		drawCenteredString(mc.fontRendererObj, barText, x + 60, y + 15, 0xffffff);
	}
	
	public void drawAccept() {
		ResourceLocation badgeTexture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");
		mc.renderEngine.bindTexture(badgeTexture);

		drawTexturedModalRect(x, y, 0, 0, width, height);
		drawTexturedModalRect(x + 6, y + 14, 0, 76, 108, 10);
		drawTexturedModalRect(x + 6, y + 14, 0, 86, 108, 10);

		String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
		String questDescription = mc.fontRendererObj.trimStringToWidth(quest.description, 110);
		String questAccept = mc.fontRendererObj.trimStringToWidth(quest.description, 110);
		drawCenteredString(mc.fontRendererObj, formattedQuestName, x + 60, y + 5, 0xffffff);
		drawCenteredString(mc.fontRendererObj, questDescription, x + 60, y + 15, 0xffffff);
		drawCenteredString(mc.fontRendererObj, questAccept, x + 60, y + 30, 0xffffff);
	}
}
