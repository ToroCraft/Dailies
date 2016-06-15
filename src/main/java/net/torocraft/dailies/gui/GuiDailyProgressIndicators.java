package net.torocraft.dailies.gui;

import java.util.Set;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;

@SideOnly(Side.CLIENT)
public class GuiDailyProgressIndicators extends Gui {

	private static final int MOUSE_COOLDOWN = 200;
	private final Minecraft mc;
	private static int offset = 0;
	private static long mousePressed = 0;

	GuiButton prevBtn;
	GuiButton nextBtn;

	public GuiDailyProgressIndicators() {
		this(null);
	}

	public GuiDailyProgressIndicators(Minecraft mc) {
		if (mc != null) {
			this.mc = mc;
		} else {
			this.mc = Minecraft.getMinecraft();
		}
	}

	@SubscribeEvent
	public void drawProgressIndicators(DrawScreenEvent.Post event) {
		if (!(mc.currentScreen instanceof GuiInventory)) {
			return;
		}

		EntityPlayer player = mc.getIntegratedServer().getPlayerList().getPlayerByUsername(mc.thePlayer.getName());
		IDailiesCapability cap = player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);

		if (!isSet(cap.getAcceptedQuests())) {
			System.out.println("no accepted quests");
			return;
		}

		ScaledResolution viewport = new ScaledResolution(mc);

		int inventoryHeight = 166;
		int inventoryWidth = 176;

		int xPos = (viewport.getScaledWidth() / 2) + (inventoryWidth / 2) + 4;
		int yPos = (viewport.getScaledHeight() / 2) - (inventoryHeight / 2);

		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ResourceLocation badgeTexture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");
		mc.renderEngine.bindTexture(badgeTexture);
		for (int i = 0; i < 5; i++) {
			if (cap.getAcceptedQuests().size() < i + offset + 1) {
				break;
			}
			DailyQuest quest = (DailyQuest) cap.getAcceptedQuests().toArray()[i + offset];
			drawTexturedModalRect(xPos, yPos, 0, 0, 120, 28);
			yPos += 30;
			String formattedQuestName = mc.fontRendererObj.trimStringToWidth(quest.name, 110);
			drawCenteredString(mc.fontRendererObj, formattedQuestName, xPos + 60, yPos - 25, 0xffffff);
			drawCenteredString(mc.fontRendererObj, quest.progress + "/" + quest.target.quantity, xPos + 60, yPos - 15,
					0xffffff);
			// drawing the string changes the bound texture, so we need to reset
			// it to our texture
			mc.renderEngine.bindTexture(badgeTexture);
		}

		if (cap.getAcceptedQuests().size() > 5) {
			drawButtons(viewport, inventoryHeight, xPos, cap.getAcceptedQuests().size());
		}
	}

	private void drawButtons(ScaledResolution viewport, int inventoryHeight, int xPos, int numAcceptedQuests) {
		int buttonWidth = 59;
		int buttonHeight = 16;
		int buttonY = (viewport.getScaledHeight() / 2) + (inventoryHeight / 2) - buttonHeight - 1;

		prevBtn = new GuiButton(0, xPos, buttonY, buttonWidth, buttonHeight, "Previous");
		nextBtn = new GuiButton(1, xPos + buttonWidth + 2, buttonY, buttonWidth, buttonHeight, "Next");

		final int mouseX = Mouse.getX() * viewport.getScaledWidth() / this.mc.displayWidth;
		final int mouseY = viewport.getScaledHeight()
				- Mouse.getY() * viewport.getScaledHeight() / this.mc.displayHeight - 1;

		if (offset == 0) {
			prevBtn.enabled = false;
		} else {
			prevBtn.enabled = true;
		}
		if (offset + 5 > numAcceptedQuests) {
			nextBtn.enabled = false;
		} else {
			nextBtn.enabled = true;
		}
		prevBtn.drawButton(mc, mouseX, mouseY);
		nextBtn.drawButton(mc, mouseX, mouseY);

		if (mouseCooldownOver() && Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
			if (prevBtn.mousePressed(mc, mouseX, mouseY)) {
				offset = Math.max(0, offset - 5);
				mousePressed = Minecraft.getSystemTime();
			}
			if (nextBtn.mousePressed(mc, mouseX, mouseY)) {
				offset = offset + 5;
				mousePressed = Minecraft.getSystemTime();
			}
		}
	}

	private boolean mouseCooldownOver() {
		return Minecraft.getSystemTime() - mousePressed > MOUSE_COOLDOWN;
	}

	private boolean isSet(Set<DailyQuest> set) {
		return set != null && set.size() > 0;
	}

}
