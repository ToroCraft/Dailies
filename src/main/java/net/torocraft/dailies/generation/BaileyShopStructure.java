package net.torocraft.dailies.generation;

import com.mojang.serialization.Codec;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BaileyShopStructure extends Structure<NoFeatureConfig> {
    public BaileyShopStructure(Codec<NoFeatureConfig> p_i231965_1_) {
        super(p_i231965_1_);
    }

    public Structure.IStartFactory<NoFeatureConfig> getStartFactory() {
        return BaileyShopStructure.Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> p_i225806_1_, int p_i225806_2_, int p_i225806_3_, MutableBoundingBox p_i225806_4_, int p_i225806_5_, long p_i225806_6_) {
            super(p_i225806_1_, p_i225806_2_, p_i225806_3_, p_i225806_4_, p_i225806_5_, p_i225806_6_);
        }

        public void func_230364_a_(ChunkGenerator p_230364_1_, TemplateManager p_230364_2_, int p_230364_3_, int p_230364_4_, Biome p_230364_5_, NoFeatureConfig p_230364_6_) {
            int i = p_230364_3_ * 16;
            int j = p_230364_4_ * 16;
            BlockPos blockpos = new BlockPos(i, 90, j);
            Rotation rotation = Rotation.randomRotation(this.rand);
            IglooPieces.func_236991_a_(p_230364_2_, blockpos, rotation, this.components, this.rand);
            this.recalculateStructureSize();
        }
    }
}
