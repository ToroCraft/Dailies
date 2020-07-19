package net.torocraft.dailies.gui;
/*
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.config.ConfigurationHandler;
import net.torocraft.dailies.messages.AbandonQuestRequest;
import net.torocraft.dailies.messages.AcceptQuestRequest;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.messages.RequestAcceptedQuests;
import net.torocraft.dailies.messages.RequestAvailableQuests;
import net.torocraft.dailies.quests.DailyQuest;

@OnlyIn(Dist.CLIENT)
public class GuiDailyProgressIndicators {

	private static final int MOUSE_COOLDOWN = 200;
	private static final int inventoryHeight = 166;
	private static final int buttonWidth = 59;
	private static final int buttonHeight = 16;
	private static final int TTL = 1500;
	private static final int questsPerPage = 5;
	private static final List<GuiDailyBadge> availableBadgeList = new ArrayList<GuiDailyBadge>();
	private static final List<GuiDailyBadge> acceptedBadgeList = new ArrayList<GuiDailyBadge>();
	private static final Map<String, GuiButton> acceptButtonMap = new HashMap<String, GuiButton>();
	private static final Map<String, GuiButton> abandonButtonMap = new HashMap<String, GuiButton>();
	
	private static int offsetAvailable = 0;
	private static int offsetAccepted = 0;
	private static long mousePressed = 0;
	
	private final Minecraft mc;
	
	private DailyQuest quest = null;
	private long activationTime = 0;

	public GuiDailyProgressIndicators() {
		mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent
	public void drawProgressIndicatorsInInventory(BackgroundDrawnEvent event) {
		if (ConfigurationHandler.showQuestsInPlayerInventory && mc.currentScreen instanceof GuiInventory) {
			buildQuestInventoryGui(event);
		} else if (mc.currentScreen instanceof DailiesGuiContainer) {
			buildAvailableQuestGui(event.getMouseX(), event.getMouseY());
			buildQuestInventoryGui(event);
		}
	}
	
	@SubscribeEvent
	public void checkForHovering(DrawScreenEvent.Post event) {
		if ((ConfigurationHandler.showQuestsInPlayerInventory && mc.currentScreen instanceof GuiInventory) || mc.currentScreen instanceof DailiesGuiContainer) {
			for (GuiDailyBadge badge : acceptedBadgeList) {
				badge.checkForHover(event.getMouseX(), event.getMouseY());
			}
			for (GuiDailyBadge badge : availableBadgeList) {
				badge.checkForHover(event.getMouseX(), event.getMouseY());
			}
		}
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
		new GuiDailyBadge(quest, mc, viewport.getScaledWidth() - 122, (viewport.getScaledHeight() / 4));
	}
	
	public void buildAvailableQuestGui(int mouseX, int mouseY) {
		
		if (!isSet(DailiesPacketHandler.availableQuests)) {
			DailiesPacketHandler.INSTANCE.sendToServer(new RequestAvailableQuests());
			return;
		}
		
		ScaledResolution viewport = new ScaledResolution(mc);

		int xPos = 5;
		int yPos = (viewport.getScaledHeight() / 2) - (inventoryHeight / 2);

		adjustGlStateManager();
		availableBadgeList.clear();
		acceptButtonMap.clear();
		
		for (int i = 0; i < questsPerPage; i++) {
			if (DailiesPacketHandler.availableQuests.size() < i + offsetAvailable + 1) {
				break;
			}
			DailyQuest quest = (DailyQuest) DailiesPacketHandler.availableQuests.toArray()[i + offsetAvailable];
			availableBadgeList.add(new GuiDailyBadge(quest, mc, xPos, yPos));
			acceptButtonMap.put(quest.id, new GuiButton(i + 10, xPos + 122, yPos+4, 20, 20, ">"));
			yPos += 30;
		}

		offsetAvailable = drawPagerButtons(viewport, xPos, mouseX, mouseY, offsetAvailable, DailiesPacketHandler.availableQuests.size());
		drawQuestAcceptButtons(mouseX, mouseY);
	}

	public void buildQuestInventoryGui(BackgroundDrawnEvent event) {
		
		if (!isSet(DailiesPacketHandler.acceptedQuests)) {
			DailiesPacketHandler.INSTANCE.sendToServer(new RequestAcceptedQuests());
			return;
		}
		
		ScaledResolution viewport = new ScaledResolution(mc);

		int xPos = viewport.getScaledWidth() - 122 - 22;
		int yPos = (viewport.getScaledHeight() / 2) - (inventoryHeight / 2);

		adjustGlStateManager();
		acceptedBadgeList.clear();
		abandonButtonMap.clear();
		
		for (int i = 0; i < questsPerPage; i++) {
			if (DailiesPacketHandler.acceptedQuests.size() < i + offsetAccepted + 1) {
				break;
			}
			DailyQuest quest = (DailyQuest) DailiesPacketHandler.acceptedQuests.toArray()[i + offsetAccepted];
			acceptedBadgeList.add(new GuiDailyBadge(quest, mc, xPos, yPos));
			abandonButtonMap.put(quest.id, new GuiButton(i + 10, xPos + 122, yPos+4, 20, 20, "X"));
			yPos += 30;
		}
		offsetAccepted = drawPagerButtons(viewport, xPos, event.getMouseX(), event.getMouseY(), offsetAccepted, DailiesPacketHandler.acceptedQuests.size());
		drawQuestAbandonButtons(event.getMouseX(), event.getMouseY());
	}

	private void adjustGlStateManager() {
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private int drawPagerButtons(ScaledResolution viewport, int xPos, int mouseX, int mouseY, int offset, int quests) {
		
		if (quests <= questsPerPage) {
			return 0;
		}
		
		int buttonY = (viewport.getScaledHeight() / 2) + (inventoryHeight / 2) - buttonHeight - 1;

		GuiButton prevBtn = new GuiButton(0, xPos, buttonY, buttonWidth, buttonHeight, "Previous");
		GuiButton nextBtn = new GuiButton(1, xPos + buttonWidth + 2, buttonY, buttonWidth, buttonHeight, "Next");

		if (offset == 0) {
			prevBtn.enabled = false;
		} else {
			prevBtn.enabled = true;
		}
		if (offset + questsPerPage > quests) {
			nextBtn.enabled = false;
		} else {
			nextBtn.enabled = true;
		}
		if (offset > quests) {
			offset = reduceOffset(offset);
		}
		prevBtn.drawButton(mc, mouseX, mouseY, 1);
		nextBtn.drawButton(mc, mouseX, mouseY, 1);

		if (mouseCooldownOver() && Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
			if (prevBtn.mousePressed(mc, mouseX, mouseY)) {
				offset = reduceOffset(offset);
				mousePressed = Minecraft.getSystemTime();
			}
			if (nextBtn.mousePressed(mc, mouseX, mouseY)) {
				offset = increaseOffset(offset);
				mousePressed = Minecraft.getSystemTime();
			}
		}
		return offset;
	}

	private int increaseOffset(int offset) {
		offset = offset + questsPerPage;
		return offset;
	}

	private int reduceOffset(int offset) {
		offset = Math.max(0, offset - questsPerPage);
		return offset;
	}

	private void drawQuestAbandonButtons(int mouseX, int mouseY) {
		for (Entry<String, GuiButton> entry : abandonButtonMap.entrySet()) {
			GuiButton btn = entry.getValue();
			btn.drawButton(mc, mouseX, mouseY, 1);

			if (Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
				if (btn.mousePressed(mc, mouseX, mouseY) && mouseCooldownOver()) {
					mousePressed = Minecraft.getSystemTime();
					DailiesPacketHandler.INSTANCE.sendToServer(new AbandonQuestRequest(entry.getKey()));
					break;
				}
			}
		}
	}
	
	private void drawQuestAcceptButtons(int mouseX, int mouseY) {
		for (Entry<String, GuiButton> entry : acceptButtonMap.entrySet()) {
			GuiButton btn = entry.getValue();
			btn.drawButton(mc, mouseX, mouseY, 1);

			if (Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
				if (btn.mousePressed(mc, mouseX, mouseY) && mouseCooldownOver()) {
					mousePressed = Minecraft.getSystemTime();
					DailiesPacketHandler.INSTANCE.sendToServer(new AcceptQuestRequest(entry.getKey()));
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

	public void setQuest(DailyQuest quest) {
		activationTime = Minecraft.getSystemTime();
		this.quest = quest;
	}
}
*/
