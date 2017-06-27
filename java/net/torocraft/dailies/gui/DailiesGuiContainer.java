package net.torocraft.dailies.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.BaileyInventory;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.messages.RequestAcceptedQuests;
import net.torocraft.dailies.messages.RequestAvailableQuests;

@SideOnly(Side.CLIENT)
public class DailiesGuiContainer extends GuiContainer {

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
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		final int LABEL_XPOS = 5;
		final int LABEL_YPOS = 5;
		fontRenderer.drawString("Bailey's Dailies", LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
	}
	
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
