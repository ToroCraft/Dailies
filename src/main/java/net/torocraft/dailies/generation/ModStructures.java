package net.torocraft.dailies.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.torocraft.dailies.DailiesMod;

import java.util.function.Supplier;

public class ModStructures {
    
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = 
        DeferredRegister.create(Registries.STRUCTURE_TYPE, DailiesMod.MODID);
    
    public static final Supplier<StructureType<BaileyShopStructure>> BAILEY_SHOP = 
        STRUCTURE_TYPES.register("bailey_shop", () -> typeConvert(BaileyShopStructure.CODEC));
    
    private static <T extends Structure> StructureType<T> typeConvert(MapCodec<T> codec) {
        return () -> codec;
    }
}
