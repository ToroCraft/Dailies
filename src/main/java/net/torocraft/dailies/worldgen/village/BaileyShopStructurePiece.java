package net.torocraft.dailies.worldgen.village;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.entity.EntitySpawnReason;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityRegistryHandler;

//TODO convert top json
public class BaileyShopStructurePiece extends StructurePiece {
    
    private final BlockPos structurePos;
    
    public BaileyShopStructurePiece(BlockPos pos) {
        super(BaileyShopStructurePieceType.BAILEY_SHOP_PIECE.get(), 0, BoundingBox.fromCorners(pos, pos.offset(8, 6, 5)));
        this.structurePos = pos;
    }
    
    public BaileyShopStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(BaileyShopStructurePieceType.BAILEY_SHOP_PIECE.get(), tag);
        this.structurePos = new BlockPos(tag.getInt("structureX"), tag.getInt("structureY"), tag.getInt("structureZ"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("structureX", this.structurePos.getX());
        tag.putInt("structureY", this.structurePos.getY());
        tag.putInt("structureZ", this.structurePos.getZ());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, 
                           RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pivot) {
        
        if (!boundingBox.intersects(this.boundingBox)) {
            return;
        }
        
        // Build the Bailey shop structure using the same design from BaileyShopWorldGen
        buildBaileyShop(level, random);
        
        // Spawn Bailey NPC
        spawnBailey(level, random);
    }
    
    private void buildBaileyShop(WorldGenLevel level, RandomSource random) {
        // Use the same structure building logic from BaileyShopWorldGen
        BlockPos basePos = this.structurePos;
        
        // Define block states
        BlockState cobblestone = Blocks.COBBLESTONE.defaultBlockState();
        BlockState oakPlanks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();
        BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();
        BlockState glass = Blocks.GLASS.defaultBlockState();
        BlockState glassPane = Blocks.GLASS_PANE.defaultBlockState();
        BlockState oakFence = Blocks.OAK_FENCE.defaultBlockState();
        BlockState oakDoor = Blocks.OAK_DOOR.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        
        BlockState stairsNorth = Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        BlockState stairsSouth = Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        
        // Clear interior space
        fillWithBlocks(level, basePos.offset(1, 1, 1), basePos.offset(7, 5, 4), air);
        
        // Foundation/floor
        fillWithBlocks(level, basePos.offset(0, 0, 0), basePos.offset(8, 0, 5), cobblestone);
        
        // Walls
        // Front and back walls
        fillWithBlocks(level, basePos.offset(0, 1, 0), basePos.offset(8, 4, 0), cobblestone);
        fillWithBlocks(level, basePos.offset(0, 1, 5), basePos.offset(8, 4, 5), cobblestone);
        
        // Side walls  
        fillWithBlocks(level, basePos.offset(0, 1, 0), basePos.offset(0, 4, 5), cobblestone);
        fillWithBlocks(level, basePos.offset(8, 1, 0), basePos.offset(8, 4, 5), cobblestone);
        
        // Corner logs
        fillWithBlocks(level, basePos.offset(0, 0, 0), basePos.offset(0, 4, 0), oakLog);
        fillWithBlocks(level, basePos.offset(8, 0, 0), basePos.offset(8, 4, 0), oakLog);
        fillWithBlocks(level, basePos.offset(0, 0, 5), basePos.offset(0, 4, 5), oakLog);
        fillWithBlocks(level, basePos.offset(8, 0, 5), basePos.offset(8, 4, 5), oakLog);
        
        // Windows
        // Front windows
        fillWithBlocks(level, basePos.offset(3, 2, 0), basePos.offset(6, 2, 0), glassPane);
        // Back windows
        fillWithBlocks(level, basePos.offset(3, 2, 5), basePos.offset(6, 2, 5), glassPane);
        // Side window
        fillWithBlocks(level, basePos.offset(8, 2, 2), basePos.offset(8, 3, 3), glassPane);
        
        // Door (front entrance)
        level.setBlock(basePos.offset(1, 1, 0), oakDoor, 3);
        level.setBlock(basePos.offset(1, 2, 0), oakDoor.setValue(net.minecraft.world.level.block.DoorBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 3);
        
        // Interior counter
        fillWithBlocks(level, basePos.offset(6, 1, 1), basePos.offset(6, 1, 4), oakPlanks);
        fillWithBlocks(level, basePos.offset(6, 3, 1), basePos.offset(6, 3, 4), oakFence);
        
        // Roof structure
        for (int i = -1; i <= 2; i++) {
            for (int j = 0; j <= 8; j++) {
                if (i >= 0 && i <= 1) {
                    level.setBlock(basePos.offset(j, 4 + i, i), stairsNorth, 3);
                    level.setBlock(basePos.offset(j, 4 + i, 5 - i), stairsSouth, 3);
                }
            }
        }
        
        // Ceiling lights
        level.setBlock(basePos.offset(1, 5, 2), glowstone, 3);
        level.setBlock(basePos.offset(1, 5, 3), glowstone, 3);
        level.setBlock(basePos.offset(3, 5, 2), glowstone, 3);
        level.setBlock(basePos.offset(3, 5, 3), glowstone, 3);
        level.setBlock(basePos.offset(5, 5, 2), glowstone, 3);
        level.setBlock(basePos.offset(5, 5, 3), glowstone, 3);
        level.setBlock(basePos.offset(7, 5, 2), glowstone, 3);
        level.setBlock(basePos.offset(7, 5, 3), glowstone, 3);
    }
    
    private void fillWithBlocks(WorldGenLevel level, BlockPos from, BlockPos to, BlockState blockState) {
        BlockPos.betweenClosed(from, to).forEach(pos -> level.setBlock(pos, blockState, 3));
    }
    
    private void spawnBailey(WorldGenLevel level, RandomSource random) {
        // Spawn Bailey inside the shop
        BlockPos spawnPos = this.structurePos.offset(7, 1, 2);
        EntityBailey bailey = new EntityBailey(EntityRegistryHandler.BAILEY.get(), level.getLevel());
        bailey.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 180.0F, 0.0F);
        bailey.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.STRUCTURE, null);
        level.addFreshEntity(bailey);
    }
}
