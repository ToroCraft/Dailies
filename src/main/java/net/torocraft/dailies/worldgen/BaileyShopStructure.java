package net.torocraft.dailies.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.torocraft.dailies.worldgen.village.BaileyShopStructurePiece;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Bailey Shop structure for village generation in NeoForge 1.21.4
 */
public class BaileyShopStructure extends Structure {
    
    public static final MapCodec<BaileyShopStructure> CODEC = simpleCodec(BaileyShopStructure::new);
    
    public BaileyShopStructure(StructureSettings settings) {
        super(settings);
    }
    
    @Override
    public StructureType<?> type() {
        return ModStructures.BAILEY_SHOP.get();
    }
    
    @Override
    protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        // Check if we can place the structure here
        ChunkPos chunkPos = context.chunkPos();
        
        // Get a suitable position in the chunk center
        BlockPos centerPos = new BlockPos(
            chunkPos.getMinBlockX() + 8,
            64, // Default Y level, will be adjusted to surface
            chunkPos.getMinBlockZ() + 8
        );
        
        // Adjust Y position to terrain surface
        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
            centerPos.getX(), centerPos.getZ(), 
            Heightmap.Types.WORLD_SURFACE_WG, 
            context.heightAccessor(), 
            context.randomState()
        );
        
        // Create the final position at surface level
        BlockPos structurePos = new BlockPos(centerPos.getX(), surfaceY, centerPos.getZ());
        
        return Optional.of(new GenerationStub(structurePos, (structurePiecesBuilder) -> {
            generatePieces(structurePiecesBuilder, context, structurePos);
        }));
    }
    
    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos pos) {
        // Add the Bailey shop structure piece
        builder.addPiece(new BaileyShopStructurePiece(pos));
    }
}
