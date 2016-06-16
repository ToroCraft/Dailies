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
	private boolean canInteract;

	public GuiDailyBadge(final DailyQuest quest, final Minecraft mc, int x, int y, boolean canInteract) {
		this.quest = quest;
		this.mc = mc;
		this.x = x;
		this.y = y;
		this.canInteract = canInteract;
		draw();
	}

	public void draw() {
		ResourceLocation badgeTexture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");
		mc.renderEngine.bindTexture(badgeTexture);

		drawTexturedModalRect(x, y, 0, 0, 120, 28);
		drawTexturedModalRect(x + 6, y + 14, 0, 76, 108, 10);
		int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
		drawTexturedModalRect(x + 6, y + 14, 0, 86, progress, 10);

		String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
		drawCenteredString(mc.fontRendererObj, formattedQuestName, x + 60, y + 5, 0xffffff);
		drawCenteredString(mc.fontRendererObj, quest.progress + "/" + quest.target.quantity, x + 60, y + 15, 0xffffff);

		if (canInteract) {
			drawQuestActions();
		}
	}

	private void drawQuestActions() {
		// TODO Auto-generated method stub

	}
}
