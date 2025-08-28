package net.torocraft.dailies.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityRegistryHandler;

import javax.annotation.Nonnull;

/**
 * Structure piece that generates the Bailey shop building and spawns Bailey
 */
public class BaileyShopPiece extends StructurePiece {
    
    // Simple structure piece type - using the disabled pattern for now
    public static final StructurePieceType TYPE = new StructurePieceType() {
        @Override
        public StructurePiece load(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
            return new BaileyShopPiece(tag);
        }
    };
    
    private BlockPos shopPosition;
    
    public BaileyShopPiece(int genDepth, BlockPos pos) {
        super(TYPE, genDepth, createBoundingBox(pos));
        this.shopPosition = pos;
    }
    
    public BaileyShopPiece(@Nonnull CompoundTag tag) {
        super(TYPE, tag);
        this.shopPosition = new BlockPos(
            tag.getInt("ShopX"), 
            tag.getInt("ShopY"), 
            tag.getInt("ShopZ")
        );
    }
    
    private static BoundingBox createBoundingBox(BlockPos pos) {
        // Create a 7x5x7 bounding box for the Bailey shop
        return BoundingBox.fromCorners(
            pos.offset(-3, 0, -3), 
            pos.offset(3, 4, 3)
        );
    }
    
    @Override
    protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt("ShopX", this.shopPosition.getX());
        tag.putInt("ShopY", this.shopPosition.getY());
        tag.putInt("ShopZ", this.shopPosition.getZ());
    }
    
    @Override
    public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager structureManager, 
                           @Nonnull ChunkGenerator chunkGenerator, @Nonnull RandomSource random, 
                           @Nonnull BoundingBox boundingBox, @Nonnull ChunkPos chunkPos, @Nonnull BlockPos pivot) {
        
        // Generate the Bailey shop building
        generateShopStructure(level, random, boundingBox);
        
        // Spawn Bailey inside the shop
        spawnBailey(level, random);
    }
    
    private void generateShopStructure(WorldGenLevel level, RandomSource random, BoundingBox boundingBox) {
        // Define the shop structure using basic blocks
        BlockState cobblestone = Blocks.COBBLESTONE.defaultBlockState();
        BlockState planks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState glass = Blocks.GLASS.defaultBlockState();
        BlockState slab = Blocks.OAK_SLAB.defaultBlockState();
        
        // Build floor (7x7)
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos floorPos = this.shopPosition.offset(x, 0, z);
                if (boundingBox.isInside(floorPos)) {
                    this.placeBlock(level, planks, floorPos.getX(), floorPos.getY(), floorPos.getZ(), boundingBox);
                }
            }
        }
        
        // Build walls
        for (int y = 1; y <= 3; y++) {
            // Front and back walls
            for (int x = -3; x <= 3; x++) {
                BlockPos frontPos = this.shopPosition.offset(x, y, -3);
                BlockPos backPos = this.shopPosition.offset(x, y, 3);
                
                if (boundingBox.isInside(frontPos)) {
                    if (y == 1 && x == 0) {
                        // Door opening in front
                        this.placeBlock(level, Blocks.AIR.defaultBlockState(), frontPos.getX(), frontPos.getY(), frontPos.getZ(), boundingBox);
                    } else if (y == 2 && (x == -1 || x == 1)) {
                        // Windows
                        this.placeBlock(level, glass, frontPos.getX(), frontPos.getY(), frontPos.getZ(), boundingBox);
                    } else {
                        this.placeBlock(level, cobblestone, frontPos.getX(), frontPos.getY(), frontPos.getZ(), boundingBox);
                    }
                }
                
                if (boundingBox.isInside(backPos)) {
                    this.placeBlock(level, cobblestone, backPos.getX(), backPos.getY(), backPos.getZ(), boundingBox);
                }
            }
            
            // Left and right walls
            for (int z = -2; z <= 2; z++) {
                BlockPos leftPos = this.shopPosition.offset(-3, y, z);
                BlockPos rightPos = this.shopPosition.offset(3, y, z);
                
                if (boundingBox.isInside(leftPos)) {
                    if (y == 2 && z == 0) {
                        // Side window
                        this.placeBlock(level, glass, leftPos.getX(), leftPos.getY(), leftPos.getZ(), boundingBox);
                    } else {
                        this.placeBlock(level, cobblestone, leftPos.getX(), leftPos.getY(), leftPos.getZ(), boundingBox);
                    }
                }
                
                if (boundingBox.isInside(rightPos)) {
                    this.placeBlock(level, cobblestone, rightPos.getX(), rightPos.getY(), rightPos.getZ(), boundingBox);
                }
            }
        }
        
        // Build roof
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos roofPos = this.shopPosition.offset(x, 4, z);
                if (boundingBox.isInside(roofPos)) {
                    this.placeBlock(level, slab, roofPos.getX(), roofPos.getY(), roofPos.getZ(), boundingBox);
                }
            }
        }
        
        // Add some interior details
        BlockPos counterPos = this.shopPosition.offset(0, 1, 2);
        if (boundingBox.isInside(counterPos)) {
            this.placeBlock(level, Blocks.CRAFTING_TABLE.defaultBlockState(), counterPos.getX(), counterPos.getY(), counterPos.getZ(), boundingBox);
        }
        
        BlockPos chestPos = this.shopPosition.offset(-1, 1, 2);
        if (boundingBox.isInside(chestPos)) {
            this.placeBlock(level, Blocks.CHEST.defaultBlockState(), chestPos.getX(), chestPos.getY(), chestPos.getZ(), boundingBox);
        }
    }
    
    private void spawnBailey(WorldGenLevel level, RandomSource random) {
        // Spawn Bailey at the center of the shop, slightly offset from the door
        BlockPos baileyPos = this.shopPosition.offset(0, 1, 1);
        
        // Create Bailey entity using the proper modern method
        EntityBailey bailey = new EntityBailey(EntityRegistryHandler.BAILEY.get(), level.getLevel());
        if (bailey != null) {
            bailey.setPos(baileyPos.getX() + 0.5, baileyPos.getY(), baileyPos.getZ() + 0.5);
            bailey.setYRot(180.0F); // Face towards the door
            bailey.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(baileyPos), EntitySpawnReason.STRUCTURE, null);
            level.addFreshEntity(bailey);
        }
    }
}
