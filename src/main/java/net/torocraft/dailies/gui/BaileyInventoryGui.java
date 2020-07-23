package net.torocraft.dailies.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
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

    ResourceLocation texture = new ResourceLocation(DailiesMod.MODID, "textures/gui/bailey_gui.png");

    private final PlayerEntity player;

    public BaileyInventoryGui(DailiesContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        player = inv.player;
    }

    @Override
    public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    @Override
    protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        this.field_230706_i_.getTextureManager().bindTexture(texture);
        int i = this.guiLeft;
        int j = this.guiTop;
    }
}
