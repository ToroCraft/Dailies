package net.torocraft.dailies.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.torocraft.dailies.DailiesRequester;
import net.torocraft.dailies.quests.DailyQuest;

public class GuiDailyBadge extends Gui {

	private final DailyQuest quest;
	private final Minecraft mc;
	private int x;
	private int y;
	private boolean canInteract;

	private int mouseX;
	private int mouseY;
	private int width = 120;
	private int height = 28;

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

		drawTexturedModalRect(x, y, 0, 0, width, height);
		drawTexturedModalRect(x + 6, y + 14, 0, 76, 108, 10);
		int progress = (int) Math.ceil(108 * ((double) quest.progress / (double) quest.target.quantity));
		drawTexturedModalRect(x + 6, y + 14, 0, 86, progress, 10);

		String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
		drawCenteredString(mc.fontRendererObj, formattedQuestName, x + 60, y + 5, 0xffffff);
		drawCenteredString(mc.fontRendererObj, quest.progress + "/" + quest.target.quantity, x + 60, y + 15, 0xffffff);

		ScaledResolution viewport = new ScaledResolution(mc);
		mouseX = Mouse.getX() * viewport.getScaledWidth() / this.mc.displayWidth;
		mouseY = viewport.getScaledHeight() - Mouse.getY() * viewport.getScaledHeight() / this.mc.displayHeight - 1;

		if (isMouseOver()) {
			List<String> hoverText = new ArrayList<String>();
			hoverText.add(quest.description);
			GuiUtils.drawHoveringText(hoverText, mouseX, mouseY, viewport.getScaledWidth(), viewport.getScaledHeight(),
					300, mc.fontRendererObj);
		}

		if (canInteract) {
			drawQuestActions();
		}
	}

	private boolean isMouseOver() {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	private void drawQuestActions() {
		GuiButton abandonBtn = new GuiButton(3, x + 122, y, 20, 20, "X");

		abandonBtn.drawButton(mc, mouseX, mouseY);

		if (abandonBtn.enabled && Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
			abandonBtn.enabled = false;
			new DailiesRequester().abandonQuest(mc.thePlayer.getName(), quest.id);
		}
	}
}
