package net.torocraft.dailies.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.DailiesMod;

@OnlyIn(Dist.CLIENT)
public class BaileyInventoryGui extends ContainerScreen<DailiesContainer> {

    ResourceLocation texture = new ResourceLocation(DailiesMod.MODID + ":textures/gui/bailey_gui.png");

    private final PlayerEntity player;
    private final int numRows;

    public BaileyInventoryGui(DailiesContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        numRows = screenContainer.inventorySlots.size();
        player = inv.player;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        getContainer().detectAndSendChanges();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(texture);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.numRows * 18 + 17);
        this.blit(matrixStack, i, j + this.numRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
