package net.torocraft.dailies.worldgen.village;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.torocraft.dailies.DailiesMod;

public class BaileyShopStructurePieceType {
    
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = 
        DeferredRegister.create(Registries.STRUCTURE_PIECE, DailiesMod.MODID);
    
    public static final DeferredHolder<StructurePieceType, StructurePieceType> BAILEY_SHOP_PIECE = 
        STRUCTURE_PIECE_TYPES.register("bailey_shop_piece", () -> BaileyShopStructurePiece::new);
}
