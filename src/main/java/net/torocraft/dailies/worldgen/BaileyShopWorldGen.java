package net.torocraft.dailies.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.StructureManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.config.Config;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityRegistryHandler;

/**
 * Bailey shop world generation with village integration
 * Generates Bailey shops in villages during village generation and occasionally in other chunks
 */
@EventBusSubscriber(modid = DailiesMod.MODID)
public class BaileyShopWorldGen {
    
    private static final int SHOP_GENERATION_CHANCE = 2000; // 1 in 2000 chunks for non-village generation
    private static final int VILLAGE_SHOP_CHANCE = 4; // 1 in 4 chance for Bailey shop in village chunks (25%)
    
    // Track where we've placed Bailey shops to prevent multiple per village
    private static final java.util.Set<net.minecraft.world.level.ChunkPos> placedShops = new java.util.HashSet<>();
    
    // Track initial world setup to handle spawn village case
    private static boolean hasCheckedSpawnArea = false;
    private static int startupTickCounter = 0;
    
    // Static initialization to verify class loading
    static {
        System.out.println("[DAILIES] BaileyShopWorldGen class loaded!");
    }
    
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Only check once after server startup
        if (hasCheckedSpawnArea) {
            return;
        }
        
        startupTickCounter++;
        
        // Wait a few ticks for world to fully initialize, then check spawn area
        if (startupTickCounter == 60) { // 3 seconds at 20 TPS
            hasCheckedSpawnArea = true;
            
            System.out.println("[DAILIES] Checking spawn area for villages after server startup...");
            
            // Check all loaded overworld levels for villages that might have been missed
            event.getServer().getAllLevels().forEach(level -> {
                if (level instanceof ServerLevel serverLevel && 
                    serverLevel.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                    
                    System.out.println("[DAILIES] Checking overworld level for spawn villages...");
                    checkInitiallyLoadedChunksForVillages(serverLevel);
                }
            });
        }
    }
    
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // Only generate in overworld
        if (!serverLevel.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
            return;
        }
        
        // Check if Bailey shop generation is enabled in config
        if (Config.baileySpawnWeight <= 0) {
            return;
        }
        
        RandomSource random = serverLevel.getRandom();
        
        // Schedule village detection for next tick to avoid chunk loading issues
        // This allows the chunk to be fully loaded before we check for villages
        serverLevel.getServer().execute(() -> {
            checkForVillageAndGenerateShop(serverLevel, event.getChunk().getPos(), random);
        });
    }
    
    private static void checkForVillageAndGenerateShop(ServerLevel level, net.minecraft.world.level.ChunkPos chunkPos, RandomSource random) {
        // Now we can safely check for villages since chunk loading is complete
        boolean isNearVillage = isChunkNearVillage(level, chunkPos);
        
        System.out.println("[DAILIES] Chunk " + chunkPos + " village detection result: " + isNearVillage);
        
        if (isNearVillage) {
            // Check if there's already a Bailey shop nearby to avoid overcrowding
            boolean hasNearbyShop = hasBaileyShopNearby(level, chunkPos);
            System.out.println("[DAILIES] Chunk " + chunkPos + " nearby Bailey shop check: " + hasNearbyShop);
            
            if (!hasNearbyShop) {
                // 1 in 4 chance for generation in villages (with spacing)
                if (random.nextInt(VILLAGE_SHOP_CHANCE) == 0) {
                    System.out.println("[DAILIES] Generating Bailey shop in village at chunk " + chunkPos);
                    generateBaileyShopInChunk(level, chunkPos, random);
                    placedShops.add(chunkPos);
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            placedShops.add(new net.minecraft.world.level.ChunkPos(chunkPos.x + dx, chunkPos.z + dz));
                        }
                    }
                } else {
                    System.out.println("[DAILIES] Random check failed for village chunk " + chunkPos + " (chance was " + VILLAGE_SHOP_CHANCE + ")");
                }
            } else {
                System.out.println("[DAILIES] Skipping Bailey shop generation - already have one nearby chunk " + chunkPos);
            }
        } else {
            // Lower chance for random generation outside villages
            if (random.nextInt(SHOP_GENERATION_CHANCE) == 0) {
                System.out.println("[DAILIES] Generating Bailey shop in non-village area at chunk " + chunkPos);
                generateBaileyShopInChunk(level, chunkPos, random);
                placedShops.add(chunkPos);
            }
        }
    }
    
    /**
     * Check if a chunk is near an actual village structure
     * This is called after chunk loading to safely access world state
     */
    private static boolean isChunkNearVillage(ServerLevel level, net.minecraft.world.level.ChunkPos chunkPos) {
        try {
            // Check if there's any village structure in this chunk or nearby chunks
            BlockPos centerPos = new BlockPos(chunkPos.x * 16 + 8, 70, chunkPos.z * 16 + 8);
            
            System.out.println("[DAILIES] Checking chunk " + chunkPos + " at position " + centerPos);
            
            // Check for village structures using the structure manager
            StructureManager structureManager = level.structureManager();
            boolean hasVillageStructure = structureManager.hasAnyStructureAt(centerPos);
            
            System.out.println("[DAILIES] Structure manager check for chunk " + chunkPos + ": " + hasVillageStructure);
            
            if (hasVillageStructure) {
                return true;
            }
            
            // Fallback: Check for village blocks in surrounding area
            boolean hasBlocks = hasVillageBlocks(level, centerPos);
            System.out.println("[DAILIES] Village blocks check for chunk " + chunkPos + ": " + hasBlocks);
            
            if (hasBlocks) {
                return true;
            }
            
            
            return false;
        } catch (Exception e) {
            System.out.println("[DAILIES] Exception in village detection for chunk " + chunkPos + ": " + e.getMessage());
            e.printStackTrace();
            // Fallback to coordinate-based logic if anything fails
            return isLikelyVillageChunk(chunkPos);
        }
    }
    
    /**
     * Check for village-indicating blocks in a small area
     * Excludes blocks that might be from other Bailey shops
     */
    private static boolean hasVillageBlocks(ServerLevel level, BlockPos centerPos) {
        int villageBlockCount = 0;
        int baileyShopBlockCount = 0;
        
        // Check a 5x5 area around the center position
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = -2; y <= 2; y++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    Block block = level.getBlockState(checkPos).getBlock();
                    
                    // Count village-indicating blocks (but be more selective)
                    if (block == Blocks.BELL ||
                        block == Blocks.COMPOSTER ||
                        block == Blocks.BARREL ||
                        block == Blocks.LECTERN ||
                        block == Blocks.CAULDRON ||
                        block == Blocks.ANVIL ||
                        block == Blocks.LOOM ||
                        block == Blocks.CARTOGRAPHY_TABLE ||
                        block == Blocks.FLETCHING_TABLE ||
                        block == Blocks.SMITHING_TABLE) {
                        villageBlockCount++;
                    }
                    
                    // Count blocks that are common in Bailey shops (to detect if we're near one)
                    if (block == Blocks.RED_CARPET ||
                        block == Blocks.GLOWSTONE ||
                        block == Blocks.OAK_FENCE) {
                        baileyShopBlockCount++;
                    }
                }
            }
        }
        
        // If we detect Bailey shop blocks, this is probably near an existing Bailey shop, not a village
        if (baileyShopBlockCount >= 2) {
            return false;
        }
        
        // If we find at least 2 village-specific blocks (reduced from 3), consider it a village area
        return villageBlockCount >= 2;
    }
    
    /**
     * Very conservative fallback coordinate-based village detection
     */
    private static boolean isLikelyVillageChunk(net.minecraft.world.level.ChunkPos chunkPos) {
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        
        // Create deterministic but very sparse "village zones" based on chunk coordinates
        long seed = (long) chunkX * 341873128712L + (long) chunkZ * 132897987541L;
        RandomSource coordinateRandom = RandomSource.create(seed);
        
        return coordinateRandom.nextInt(200) == 0;
    }
    
    private static void generateBaileyShopInChunk(ServerLevel level, net.minecraft.world.level.ChunkPos chunkPos, RandomSource random) {
        // Find a suitable location in the chunk
        int x = chunkPos.getMinBlockX() + 8 + random.nextInt(8); // 8-15 blocks from chunk edge
        int z = chunkPos.getMinBlockZ() + 8 + random.nextInt(8);
        
        // Find surface level
        int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        
        // Make sure it's a reasonable location (not underwater, not too high)
        if (y < 60 || y > 120) {
            return;
        }
        
        BlockPos centerPos = new BlockPos(x, y, z);
        
        // Check if the area is suitable (mostly air above, solid ground below)
        if (!isSuitableForBaileyShop(level, centerPos)) {
            return;
        }
        
        // Generate the Bailey shop
        buildBaileyShop(level, centerPos);
        
        // Spawn Bailey inside
        spawnBailey(level, centerPos, random);
    }
    
    private static boolean isSuitableForBaileyShop(ServerLevel level, BlockPos centerPos) {
        // Check if there's enough flat space (7x7 area)
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos checkPos = centerPos.offset(x, 0, z);
                BlockPos abovePos = centerPos.offset(x, 1, z);
                BlockPos above2Pos = centerPos.offset(x, 2, z);
                
                // Check if ground is solid
                if (level.getBlockState(checkPos).isAir()) {
                    return false;
                }
                
                // Check if space above is mostly clear
                if (!level.getBlockState(abovePos).isAir() || !level.getBlockState(above2Pos).isAir()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static void buildBaileyShop(ServerLevel world, BlockPos pos) {
        // Original design: 9x6x9 structure (width x depth x height)
        
        // Clear interior first
        fillWithBlocks(world, pos, 1, 1, 1, 7, 5, 4, Blocks.AIR);
        
        // Foundation - cobblestone floor
        fillWithBlocks(world, pos, 0, 0, 0, 8, 0, 5, Blocks.COBBLESTONE);
        
        // Carpet on part of floor
        fillWithBlocks(world, pos, 3, 1, 0, 8, 1, 5, Blocks.RED_CARPET);
        
        // Front and back walls (cobblestone)
        fillWithBlocks(world, pos, 0, 1, 0, 0, 4, 5, Blocks.COBBLESTONE);
        fillWithBlocks(world, pos, 8, 1, 0, 8, 4, 5, Blocks.COBBLESTONE);
        
        // Front and back top wall area (planks and logs)
        fillWithBlocks(world, pos, 0, 4, 1, 0, 4, 4, Blocks.OAK_PLANKS);
        fillWithBlocks(world, pos, 8, 4, 1, 8, 4, 4, Blocks.OAK_PLANKS);
        fillWithBlocks(world, pos, 0, 5, 2, 0, 5, 3, Blocks.OAK_LOG);
        fillWithBlocks(world, pos, 8, 5, 2, 8, 5, 3, Blocks.OAK_LOG);
        
        // Ceiling lights (glowstone)
        fillWithBlocks(world, pos, 1, 5, 2, 1, 5, 3, Blocks.GLOWSTONE);
        fillWithBlocks(world, pos, 3, 5, 2, 3, 5, 3, Blocks.GLOWSTONE);
        fillWithBlocks(world, pos, 5, 5, 2, 5, 5, 3, Blocks.GLOWSTONE);
        fillWithBlocks(world, pos, 7, 5, 2, 7, 5, 3, Blocks.GLOWSTONE);
        
        // Side walls
        fillWithBlocks(world, pos, 0, 1, 0, 8, 4, 0, Blocks.COBBLESTONE);
        fillWithBlocks(world, pos, 0, 1, 5, 8, 4, 5, Blocks.COBBLESTONE);
        
        // Side windows (glass panes)
        fillWithBlocks(world, pos, 3, 2, 0, 6, 2, 0, Blocks.GLASS_PANE);
        fillWithBlocks(world, pos, 3, 2, 5, 6, 2, 5, Blocks.GLASS_PANE);
        
        // Back window
        fillWithBlocks(world, pos, 8, 2, 2, 8, 3, 3, Blocks.GLASS_PANE);
        
        // Corner posts (logs)
        fillWithBlocks(world, pos, 0, 0, 0, 0, 4, 0, Blocks.OAK_LOG);
        fillWithBlocks(world, pos, 8, 0, 0, 8, 4, 0, Blocks.OAK_LOG);
        fillWithBlocks(world, pos, 0, 0, 5, 0, 4, 5, Blocks.OAK_LOG);
        fillWithBlocks(world, pos, 8, 0, 5, 8, 4, 5, Blocks.OAK_LOG);
        
        // Door (oak door at front) 
        BlockState lowerDoor = Blocks.OAK_DOOR.defaultBlockState()
            .setValue(DoorBlock.FACING, Direction.SOUTH)
            .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState upperDoor = Blocks.OAK_DOOR.defaultBlockState()
            .setValue(DoorBlock.FACING, Direction.SOUTH)
            .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        
        world.setBlock(pos.offset(1, 1, 0), lowerDoor, 3);
        world.setBlock(pos.offset(1, 2, 0), upperDoor, 3);
        
        // Counter area (planks base, fence top)
        fillWithBlocks(world, pos, 6, 1, 1, 6, 1, 4, Blocks.OAK_PLANKS);
        fillWithBlocks(world, pos, 6, 3, 1, 6, 3, 4, Blocks.OAK_FENCE);
        
        // Build roof with oak planks (temporarily replacing stairs)
        for (int i = -1; i <= 2; i++) {
            for (int j = 0; j <= 8; j++) {
                // Use oak planks for roof instead of stairs for now
                world.setBlock(pos.offset(j, 4 + i, i), Blocks.OAK_PLANKS.defaultBlockState(), 3);
                world.setBlock(pos.offset(j, 4 + i, 5 - i), Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        // Entrance stairs facing south (correct direction for walking up into building)
        BlockState entranceStairs = Blocks.OAK_STAIRS.defaultBlockState()
            .setValue(StairBlock.FACING, Direction.SOUTH);
        world.setBlock(pos.offset(1, 0, -1), entranceStairs, 3);
        
        // Clear above and add foundation below
        for (int l = 0; l < 6; l++) {
            for (int k = 0; k < 9; k++) {
                clearUpwards(world, pos.offset(k, 9, l));
                addFoundationDownwards(world, pos.offset(k, -1, l), Blocks.COBBLESTONE);
            }
        }
    }
    
    private static void spawnBailey(ServerLevel level, BlockPos centerPos, RandomSource random) {
        // Spawn Bailey at position matching original design (7, 1, 2 relative to structure)
        BlockPos baileyPos = centerPos.offset(7, 1, 2);
        
        // Create Bailey entity using the modern EntityType.create method
        EntityBailey bailey = EntityRegistryHandler.BAILEY.get().create(
            level,
            null, // consumer
            baileyPos,
            EntitySpawnReason.NATURAL,
            true, // force spawn
            false // no offsetY
        );
        
        if (bailey != null) {
            bailey.setYRot(180.0F); // Face towards the door
            level.addFreshEntity(bailey);
        }
    }
    
    // Helper methods for block placement (similar to original structure generation)
    private static void fillWithBlocks(ServerLevel world, BlockPos basePos, int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.setBlock(basePos.offset(x, y, z), block.defaultBlockState(), 3);
                }
            }
        }
    }
    
    private static void clearUpwards(ServerLevel world, BlockPos pos) {
        for (int y = 0; y < 20; y++) { // Clear up to 20 blocks above
            BlockPos clearPos = pos.offset(0, y, 0);
            if (world.getBlockState(clearPos).isAir()) {
                break;
            }
            world.setBlock(clearPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
    
    private static void addFoundationDownwards(ServerLevel world, BlockPos pos, Block block) {
        for (int y = 0; y < 10; y++) { // Add foundation up to 10 blocks down
            BlockPos foundationPos = pos.offset(0, -y, 0);
            BlockState currentState = world.getBlockState(foundationPos);
            if (!currentState.isAir() && !currentState.canBeReplaced()) {
                break;
            }
            world.setBlock(foundationPos, block.defaultBlockState(), 3);
        }
    }
    
    /**
     * Check if there's already a Bailey shop within a reasonable distance
     * to prevent overcrowding villages with multiple Bailey shops
     */
    private static boolean hasBaileyShopNearby(ServerLevel level, net.minecraft.world.level.ChunkPos chunkPos) {
        // Check a 3x3 area (balanced performance vs coverage)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // Skip the current chunk
                
                net.minecraft.world.level.ChunkPos nearbyChunk = new net.minecraft.world.level.ChunkPos(
                    chunkPos.x + dx, chunkPos.z + dz);
                
                if (placedShops.contains(nearbyChunk)) {
                    return true;
                }
                
                if (hasBaileyInChunk(level, nearbyChunk)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check if a chunk contains a Bailey entity (indicating a Bailey shop was generated there)
     */
    private static boolean hasBaileyInChunk(ServerLevel level, net.minecraft.world.level.ChunkPos chunkPos) {
        // Get the chunk boundaries
        int minX = chunkPos.getMinBlockX();
        int maxX = chunkPos.getMaxBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int maxZ = chunkPos.getMaxBlockZ();
        
        // Search for Bailey entities in this chunk
        return level.getEntitiesOfClass(EntityBailey.class, 
            new net.minecraft.world.phys.AABB(minX, 0, minZ, maxX + 1, 256, maxZ + 1))
            .size() > 0;
    }
    
    /**
     * Check initially loaded chunks for villages that might have been missed during startup
     * This handles the case where the player spawns directly in a village
     */
    private static void checkInitiallyLoadedChunksForVillages(ServerLevel serverLevel) {
        if (Config.baileySpawnWeight <= 0) {
            System.out.println("[DAILIES] Bailey spawn weight is 0 or negative, skipping generation");
            return;
        }
        
        RandomSource random = serverLevel.getRandom();
        
        // Get spawn position to check nearby chunks
        BlockPos spawnPos = serverLevel.getSharedSpawnPos();
        int spawnChunkX = spawnPos.getX() >> 4;
        int spawnChunkZ = spawnPos.getZ() >> 4;
        
        System.out.println("[DAILIES] Spawn position: " + spawnPos + " (chunk " + spawnChunkX + ", " + spawnChunkZ + ")");
        
        // Check a 5x5 area around spawn for villages without Bailey shops
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                net.minecraft.world.level.ChunkPos chunkPos = new net.minecraft.world.level.ChunkPos(
                    spawnChunkX + dx, spawnChunkZ + dz);
                
                // Only check if chunk is already loaded
                if (serverLevel.hasChunk(chunkPos.x, chunkPos.z)) {
                    System.out.println("[DAILIES] Checking loaded chunk " + chunkPos + " for village...");
                    checkForVillageAndGenerateShop(serverLevel, chunkPos, random);
                } else {
                    System.out.println("[DAILIES] Chunk " + chunkPos + " not loaded, skipping");
                }
            }
        }
    }
    
    // Public methods for command usage
    public static void buildBaileyShopStructure(ServerLevel world, BlockPos pos) {
        buildBaileyShop(world, pos);
    }
    
    public static void spawnBaileyInShop(ServerLevel level, BlockPos centerPos) {
        RandomSource random = level.getRandom();
        spawnBailey(level, centerPos, random);
    }
}
