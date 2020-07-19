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
import net.torocraft.dailies.DailiesMod;

@OnlyIn(Dist.CLIENT)
public class BaileyInventoryGui extends ContainerScreen<BaileyInventoryContainer> {

    ResourceLocation texture = new ResourceLocation(DailiesMod.MODID, "textures/gui/bailey_gui.png");

    private final PlayerEntity player;

    public BaileyInventoryGui(BaileyInventoryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        player = inv.player;
    }

    @Override
    protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(texture);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.func_238474_b_(p_230450_1_, i, j, 0, 0, this.xSize, this.ySize);
    }
}
