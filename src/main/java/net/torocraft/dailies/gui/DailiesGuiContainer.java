package net.torocraft.dailies.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.torocraft.dailies.DailiesContainer;

public class DailiesGuiContainer extends AbstractContainerScreen<DailiesContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("dailies", "textures/gui/bailey_gui.png");

	public DailiesGuiContainer(DailiesContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		this.imageWidth = 175;
		this.imageHeight = 130;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft mc = Minecraft.getInstance();
		if (mc != null) {
			mc.getTextureManager().bindForSetup(TEXTURE);
		}
		blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, "Bailey's Dailies", 5, 5, 0x404040);
		this.font.draw(poseStack, "Quests & Trading", 5, 15, 0x808080);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}
}

