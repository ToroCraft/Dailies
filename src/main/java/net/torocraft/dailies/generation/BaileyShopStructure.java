package net.torocraft.dailies.generation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class BaileyShopStructure extends Structure {
    
    public static final MapCodec<BaileyShopStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            settingsCodec(instance)
        ).apply(instance, BaileyShopStructure::new)
    );
    
    public BaileyShopStructure(StructureSettings settings) {
        super(settings);
    }
    
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // Check if we can place the structure here
        ChunkPos chunkPos = context.chunkPos();
        
        // Get a suitable position in the chunk
        BlockPos centerPos = new BlockPos(
            chunkPos.getMinBlockX() + 8,
            60, // Default Y level, will be adjusted
            chunkPos.getMinBlockZ() + 8
        );
        
        // Adjust Y position to terrain
        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
            centerPos.getX(), centerPos.getZ(), 
            Heightmap.Types.WORLD_SURFACE_WG, 
            context.heightAccessor(), 
            context.randomState()
        );
        
        final BlockPos finalPos = new BlockPos(centerPos.getX(), surfaceY, centerPos.getZ());
        
        return Optional.of(new GenerationStub(finalPos, (structurePiecesBuilder) -> {
            generatePieces(structurePiecesBuilder, context, finalPos);
        }));
    }
    
    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos pos) {
        // Add the Bailey shop structure piece
        builder.addPiece(new BaileyShopPiece(0, pos));
    }
    
    @Override
    public StructureType<?> type() {
        return ModStructures.BAILEY_SHOP.get();
    }
}
