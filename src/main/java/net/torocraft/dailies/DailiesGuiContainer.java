package net.torocraft.dailies;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DailiesGuiContainer extends GuiContainer {
	
	private final EntityPlayer player;
	private final World world;
	
	ResourceLocation texture = new ResourceLocation("dailiesmod", "textures/gui/badge_bg.png");

	public DailiesGuiContainer() {
		this(null, null);
	}
	
	public DailiesGuiContainer(EntityPlayer player, World world) {
		super(new DailiesContainer(null, null, null));
		this.player = player != null ? player : Minecraft.getMinecraft().thePlayer;
		this.world = world != null ? world : Minecraft.getMinecraft().theWorld;
		xSize = 176;
		ySize = 166;
	}
	
	public DailiesGuiContainer(EntityPlayer player, World world, int x, int y, int z) {
		this(player, world);
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
		fontRendererObj.drawString("Dailies!", LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
	}
}
