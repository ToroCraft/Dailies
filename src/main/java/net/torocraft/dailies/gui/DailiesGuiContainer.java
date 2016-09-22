package net.torocraft.dailies.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.BaileyInventory;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.messages.AbandonQuestRequest;
import net.torocraft.dailies.messages.AcceptQuestRequest;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.messages.RequestAcceptedQuests;
import net.torocraft.dailies.messages.RequestAvailableQuests;
import net.torocraft.dailies.quests.DailyQuest;

@SideOnly(Side.CLIENT)
public class DailiesGuiContainer extends GuiContainer {
	
	private static final int MOUSE_COOLDOWN = 200;
	private static long mousePressed = 0;
	
	private Set<DailyQuest> availableQuests;
	private Set<DailyQuest> acceptedQuests;
	
	Map<Integer, String> acceptButtonMap;
	Map<Integer, String> abandonButtonMap;

	ResourceLocation texture = new ResourceLocation("dailiesmod", "textures/gui/bailey_gui.png");

	public DailiesGuiContainer() {
		this(null, null, null);
	}
	
	public DailiesGuiContainer(EntityPlayer player, BaileyInventory baileyInventory, World world) {
		super(new DailiesContainer(player, baileyInventory, world));
		xSize = 175;
		ySize = 130;
		
		syncWithServer();
	}
	
	private void syncWithServer() {
		DailiesPacketHandler.INSTANCE.sendToServer(new RequestAvailableQuests());
		DailiesPacketHandler.INSTANCE.sendToServer(new RequestAcceptedQuests());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		this.buttonList.clear();
		
		ScaledResolution viewport = new ScaledResolution(mc);
		
		int xPos = 5;// (viewport.getScaledWidth() / 2) + (176 / 4) + 4;
		int yPos = 5;// (viewport.getScaledHeight() / 2) - (166);
		int buttonId = 10;
		
		if(DailiesPacketHandler.availableQuests != null) {
			this.availableQuests = DailiesPacketHandler.availableQuests;
			acceptButtonMap = new HashMap<Integer, String>();
			GuiButton button;
			
			for(DailyQuest quest : availableQuests) {
				new GuiDailyBadge(quest, mc, xPos, yPos, mouseX, mouseY).drawAccept();
				button = new GuiButton(buttonId++, xPos + ((122 / 2) - 25), yPos + 30, 50, 20, "Accept");
				this.buttonList.add(button);
				acceptButtonMap.put(button.id, quest.id);
				yPos += 53;
			}
		}
		
		yPos = 5;
		xPos = (viewport.getScaledWidth() - 125);
		abandonButtonMap = new HashMap<Integer, String>();
		
		if(DailiesPacketHandler.acceptedQuests != null) {
			this.acceptedQuests = DailiesPacketHandler.acceptedQuests;
			GuiButton button;
			for(DailyQuest quest : acceptedQuests) {
				new GuiDailyBadge(quest, mc, xPos, yPos, mouseX, mouseY);
				button = new GuiButton(buttonId++, xPos + ((122 / 2) - 25), yPos + 30, 50, 20, "Abandon");
				this.buttonList.add(button);
				abandonButtonMap.put(button.id, quest.id);
				yPos += 53;
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		final int LABEL_XPOS = 5;
		final int LABEL_YPOS = 5;
		fontRendererObj.drawString("Bailey's Dailies", LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(mouseCooldownOver()) {
			
			mousePressed = Minecraft.getSystemTime();
			
			for(Entry<Integer, String> set : acceptButtonMap.entrySet()) {
				if(set.getKey().equals(button.id)) {
					acceptQuest(set.getValue());
					return;
				}
			}
			
			for(Entry<Integer, String> set : abandonButtonMap.entrySet()) {
				if(set.getKey().equals(button.id)) {
					abandonQuest(set.getValue());
					return;
				}
			}
		}
	}
	
	private void acceptQuest(String questId) {
		DailiesPacketHandler.INSTANCE.sendToServer(new AcceptQuestRequest(questId));
	}
	
	private void abandonQuest(String questId) {
		DailiesPacketHandler.INSTANCE.sendToServer(new AbandonQuestRequest(questId));
	}
	
	private boolean mouseCooldownOver() {
		return Minecraft.getSystemTime() - mousePressed > MOUSE_COOLDOWN;
	}
}
