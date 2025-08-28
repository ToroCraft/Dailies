package net.torocraft.dailies.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.torocraft.dailies.DailiesContainer;

public class DailiesGuiContainer extends AbstractContainerScreen<DailiesContainer> {

	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("dailies", "textures/gui/bailey_gui.png");

	public DailiesGuiContainer(DailiesContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		this.imageWidth = 175;
		this.imageHeight = 130;
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(RenderType::guiTextured, TEXTURE, leftPos, topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, "Bailey's Dailies", 5, 5, 0x404040);
		guiGraphics.drawString(this.font, "Quests & Trading", 5, 15, 0x808080);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
}

