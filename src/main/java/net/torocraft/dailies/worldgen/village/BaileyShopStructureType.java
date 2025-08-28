package net.torocraft.dailies.worldgen.village;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.torocraft.dailies.DailiesMod;

public class BaileyShopStructureType {
    
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = 
        DeferredRegister.create(Registries.STRUCTURE_TYPE, DailiesMod.MODID);
    
    public static final DeferredHolder<StructureType<?>, StructureType<BaileyShopStructure>> BAILEY_SHOP_STRUCTURE = 
        STRUCTURE_TYPES.register("bailey_shop", () -> () -> BaileyShopStructure.CODEC);
}
