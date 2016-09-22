package net.torocraft.dailies.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.config.ConfigurationHandler;
import net.torocraft.dailies.messages.AbandonQuestRequest;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.messages.RequestAcceptedQuests;
import net.torocraft.dailies.quests.DailyQuest;

@SideOnly(Side.CLIENT)
public class GuiDailyProgressIndicators extends Gui {

	private static final int MOUSE_COOLDOWN = 200;
	private final Minecraft mc;
	private static int offset = 0;
	private static long mousePressed = 0;

	GuiButton prevBtn;
	GuiButton nextBtn;

	DailyQuest quest = null;
	
	private Boolean questsDataUpdateRequired = true;
	
	long activationTime = 0;
	final static int TTL = 1500;

	Map<String, GuiButton> buttonMap;
	int mouseX;
	int mouseY;

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
	public void drawProgressIndicatorsInInventory(DrawScreenEvent.Post event) {
		if (!ConfigurationHandler.showQuestsInPlayerInventory) {
			return;
		}
		
		if (!(mc.currentScreen instanceof GuiInventory)) {
			questsDataUpdateRequired = true;
			return;
		}
		
		if(questsDataUpdateRequired) {
			DailiesPacketHandler.INSTANCE.sendToServer(new RequestAcceptedQuests());
			questsDataUpdateRequired = false;
			return;
		}

		if (!isSet(DailiesPacketHandler.acceptedQuests)) {
			return;
		}

		ScaledResolution viewport = new ScaledResolution(mc);

		int inventoryHeight = 166;
		int inventoryWidth = 176;

		int xPos = (viewport.getScaledWidth() / 2) + (inventoryWidth / 2) + 4;
		int yPos = (viewport.getScaledHeight() / 2) - (inventoryHeight / 2);

		buttonMap = new HashMap<String, GuiButton>();

		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		mouseX = Mouse.getX() * viewport.getScaledWidth() / this.mc.displayWidth;
		mouseY = viewport.getScaledHeight() - Mouse.getY() * viewport.getScaledHeight() / this.mc.displayHeight - 1;
		
		for (int i = 0; i < 5; i++) {
			if (DailiesPacketHandler.acceptedQuests.size() < i + offset + 1) {
				break;
			}
			DailyQuest quest = (DailyQuest) DailiesPacketHandler.acceptedQuests.toArray()[i + offset];
			new GuiDailyBadge(quest, mc, xPos, yPos, mouseX, mouseY);
			buttonMap.put(quest.id, new GuiButton(i + 10, xPos + 122, yPos, 20, 20, "X"));
			yPos += 30;
		}

		if (DailiesPacketHandler.acceptedQuests.size() > 5) {
			drawPagerButtons(viewport, inventoryHeight, xPos, DailiesPacketHandler.acceptedQuests.size());
		}

		drawQuestActions();
	}

	private void drawPagerButtons(ScaledResolution viewport, int inventoryHeight, int xPos, int numAcceptedQuests) {
		int buttonWidth = 59;
		int buttonHeight = 16;
		int buttonY = (viewport.getScaledHeight() / 2) + (inventoryHeight / 2) - buttonHeight - 1;

		prevBtn = new GuiButton(0, xPos, buttonY, buttonWidth, buttonHeight, "Previous");
		nextBtn = new GuiButton(1, xPos + buttonWidth + 2, buttonY, buttonWidth, buttonHeight, "Next");

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

	private void drawQuestActions() {
		for (Entry<String, GuiButton> entry : buttonMap.entrySet()) {
			GuiButton btn = entry.getValue();
			btn.drawButton(mc, mouseX, mouseY);

			if (Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
				if (btn.mousePressed(mc, mouseX, mouseY) && mouseCooldownOver()) {
					mousePressed = Minecraft.getSystemTime();
					DailiesPacketHandler.INSTANCE.sendToServer(new AbandonQuestRequest(entry.getKey()));
					break;
				}
			}
		}
	}

	private boolean mouseCooldownOver() {
		return Minecraft.getSystemTime() - mousePressed > MOUSE_COOLDOWN;
	}

	private boolean isSet(Set<DailyQuest> set) {
		return set != null && set.size() > 0;
	}

	@SubscribeEvent
	public void showProgressUpdate(RenderGameOverlayEvent.Post event) {
		if (quest == null || Minecraft.getSystemTime() - activationTime > TTL) {
			quest = null;
			return;
		}

		if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
			return;
		}

		ScaledResolution viewport = new ScaledResolution(mc);
		new GuiDailyBadge(quest, mc, viewport.getScaledWidth() - 122, (viewport.getScaledHeight() / 4), 0, 0);
	}

	public void setQuest(DailyQuest quest) {
		activationTime = Minecraft.getSystemTime();
		this.quest = quest;
	}
}
