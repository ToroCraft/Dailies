package net.torocraft.dailies.worldgen.village;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class BaileyShopStructure extends Structure {
    
    public static final MapCodec<BaileyShopStructure> CODEC = simpleCodec(BaileyShopStructure::new);
    
    public BaileyShopStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        BlockPos centerPos = new BlockPos(chunkPos.getMinBlockX() + 8, 70, chunkPos.getMinBlockZ() + 8);
        
        // Find suitable ground level
        int groundLevel = context.chunkGenerator().getFirstOccupiedHeight(
            centerPos.getX(), centerPos.getZ(), 
            Heightmap.Types.WORLD_SURFACE_WG, 
            context.heightAccessor(), 
            context.randomState()
        );
        
        BlockPos structurePos = new BlockPos(centerPos.getX(), groundLevel, centerPos.getZ());
        
        return Optional.of(new Structure.GenerationStub(structurePos, (structurePiecesBuilder) -> {
            generatePieces(structurePiecesBuilder, context, structurePos);
        }));
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context, BlockPos pos) {
        builder.addPiece(new BaileyShopStructurePiece(pos));
    }

    @Override
    public StructureType<?> type() {
        return BaileyShopStructureType.BAILEY_SHOP_STRUCTURE.get();
    }
}
