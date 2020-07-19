package net.torocraft.dailies.gui;
/*
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.torocraft.dailies.quests.DailyQuest;

public class GuiDailyBadge extends AbstractGui {
	
	private static ResourceLocation badgeTexture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");

	private final DailyQuest quest;
	private final Minecraft mc;
	private final int x;
	private final int y;
	private List<String> hoverLines;

	private int width = 120;
	private int height = 28;
	private int screenWidth;
	private int screenHeight;

	public GuiDailyBadge(final DailyQuest quest, final Minecraft mc, int x, int y) {
		this.quest = quest;
		this.mc = mc;
		this.x = x;
		this.y = y;

		buildHoverLines();
		draw();
	}


	private void buildHoverLines() {
		hoverLines = new ArrayList<String>();
		hoverLines.add(quest.name);
		hoverLines.add(quest.description);
	}

	public void draw() {
		mc.renderEngine.bindTexture(badgeTexture);

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
	}
	
	public void checkForHover(int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			drawHoverText(mouseX, mouseY);
		}
	}

	private void drawHoverText(int mouseX, int mouseY) {
		GuiUtils.drawHoveringText(hoverLines, mouseX, mouseY, screenWidth, screenHeight, 110, mc.fontRenderer);
	}

	private String buildQuestProgressRatioString() {
		StringBuilder sb = new StringBuilder();
		sb.append(quest.progress);
		sb.append("/");
		sb.append(quest.target.quantity);
		return sb.toString();
	}
	
	public void drawAccept() {
		mc.renderEngine.bindTexture(badgeTexture);

		drawTexturedModalRect(x, y, 0, 0, width, height);
		drawTexturedModalRect(x + 6, y + 14, 0, 76, 108, 10);

		String formattedQuestName = mc.fontRenderer.trimStringToWidth(quest.name, 110);
		String questDescription = mc.fontRenderer.trimStringToWidth(quest.description, 110);
		drawCenteredString(mc.fontRenderer, formattedQuestName, x + 60, y + 5, 0xffffff);
		drawCenteredString(mc.fontRenderer, questDescription, x + 60, y + 15, 0xffffff);
	}
}
 */
