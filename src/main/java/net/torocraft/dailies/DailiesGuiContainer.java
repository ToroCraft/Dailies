package net.torocraft.dailies;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.gui.GuiDailyBadge;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.messages.StatusRequestToServer;
import net.torocraft.dailies.quests.DailyQuest;

@SideOnly(Side.CLIENT)
public class DailiesGuiContainer extends GuiContainer {
	
	private final EntityPlayer player;
	private final World world;
	
	private List<DailyQuest> availableQuests;

	ResourceLocation texture = new ResourceLocation("dailiesmod", "textures/gui/bailey_gui.png");

	public DailiesGuiContainer() {
		this(null, null);
	}
	
	public DailiesGuiContainer(EntityPlayer player, World world) {
		super(new DailiesContainer(player, null, world));
		this.player = player != null ? player : Minecraft.getMinecraft().thePlayer;
		this.world = world != null ? world : Minecraft.getMinecraft().theWorld;
		xSize = 250;
		ySize = 166;
	}
	
	public DailiesGuiContainer(EntityPlayer player, World world, int x, int y, int z) {
		this(player, world);
		loadAvailableQuests();
		
		DailiesPacketHandler.INSTANCE.sendToServer(new StatusRequestToServer());
	}
	
	private void loadAvailableQuests() {
		availableQuests = new ArrayList<DailyQuest>();
		DailiesRequester requester = new DailiesRequester();
		Set<DailyQuest> dailies = requester.getQuestInventory(player.getName());
		for(DailyQuest quest : dailies) {
			if ("available".equals(quest.status)) {
				availableQuests.add(quest);
				System.out.println(quest.name);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		ScaledResolution viewport = new ScaledResolution(mc);
		
		int xPos = (viewport.getScaledWidth() / 2) + (176 / 2) + 4;
		int yPos = (viewport.getScaledHeight() / 2) - (166 / 2);
		
		for(DailyQuest quest : availableQuests) {
			new GuiDailyBadge(quest, mc, xPos, yPos);
			yPos += 30;
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		final int LABEL_XPOS = 5;
		final int LABEL_YPOS = 5;
		fontRendererObj.drawString("Bailey's Dailies", LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
	}
}
