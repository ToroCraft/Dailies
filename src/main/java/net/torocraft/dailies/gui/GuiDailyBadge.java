package net.torocraft.dailies.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.torocraft.dailies.quests.DailyQuest;

public class GuiDailyBadge extends Gui {
	
	private static ResourceLocation badgeTexture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");

	private final DailyQuest quest;
	private final Minecraft mc;
	private final int x;
	private final int y;
	private final int mouseX;
	private final int mouseY;
	private List<String> hoverLines;

	private int width = 120;
	private int height = 28;
	
	private int screenWidth;
	private int screenHeight;

	public GuiDailyBadge(final DailyQuest quest, final Minecraft mc, int x, int y, int mouseX, int mouseY) {
		this.quest = quest;
		this.mc = mc;
		this.x = x;
		this.y = y;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		getScreenDimensions();
		buildHoverLines();
		draw();
	}
	
	private void getScreenDimensions() {
		ScaledResolution res = new ScaledResolution(mc);
		screenWidth = res.getScaledWidth();
		screenHeight = res.getScaledHeight();
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

		String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
		drawCenteredString(mc.fontRendererObj, formattedQuestName, x + 60, y + 5, 0xffffff);
		String barText = buildQuestProgressRatioString();
		if (Minecraft.getSystemTime() % 6000 < 3000) {
			barText = mc.fontRendererObj.trimStringToWidth(quest.description, 110);
		}
		drawCenteredString(mc.fontRendererObj, barText, x + 60, y + 15, 0xffffff);
		
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			System.out.println("manual hover check is true");
			GuiUtils.drawHoveringText(hoverLines, mouseX, mouseY, screenWidth, screenHeight, 110, mc.fontRendererObj);
		}
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

		String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
		String questDescription = mc.fontRendererObj.trimStringToWidth(quest.description, 110);
		drawCenteredString(mc.fontRendererObj, formattedQuestName, x + 60, y + 5, 0xffffff);
		drawCenteredString(mc.fontRendererObj, questDescription, x + 60, y + 15, 0xffffff);
	}
}
