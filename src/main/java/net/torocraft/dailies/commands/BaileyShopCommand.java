package net.torocraft.dailies.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.torocraft.dailies.worldgen.BaileyShopWorldGen;

public class BaileyShopCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dailies")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("spawn_bailey_shop")
                .executes(BaileyShopCommand::spawnBaileyShopAtPlayer) // Default: spawn at player position
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(BaileyShopCommand::spawnBaileyShopAtCoords)))); // Optional: specific coordinates
    }
    
    private static int spawnBaileyShopAtPlayer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            // Get player position
            BlockPos playerPos = BlockPos.containing(source.getPosition());
            
            // Find a suitable nearby location (within 16 blocks)
            BlockPos spawnPos = findSuitableLocation(source.getLevel(), playerPos);
            
            return spawnBaileyShop(context, spawnPos);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to find suitable location near player: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int spawnBaileyShopAtCoords(CommandContext<CommandSourceStack> context) {
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        return spawnBaileyShop(context, pos);
    }
    
    private static int spawnBaileyShop(CommandContext<CommandSourceStack> context, BlockPos pos) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        
        try {
            // Generate the Bailey shop structure using our updated design
            BaileyShopWorldGen.buildBaileyShopStructure(level, pos);
            
            // Spawn Bailey inside using our updated method
            BaileyShopWorldGen.spawnBaileyInShop(level, pos);
            
            source.sendSuccess(() -> Component.literal("Bailey shop spawned at " + pos.toShortString()), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to spawn Bailey shop: " + e.getMessage()));
            return 0;
        }
    }
    
    private static BlockPos findSuitableLocation(ServerLevel level, BlockPos playerPos) {
        // Search in a 16x16 area around the player
        for (int attempts = 0; attempts < 50; attempts++) {
            int offsetX = level.getRandom().nextInt(33) - 16; // -16 to +16
            int offsetZ = level.getRandom().nextInt(33) - 16;
            
            BlockPos testPos = playerPos.offset(offsetX, 0, offsetZ);
            
            // Find surface level at this position
            int surfaceY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, testPos.getX(), testPos.getZ());
            testPos = new BlockPos(testPos.getX(), surfaceY, testPos.getZ());
            
            // Check if this location is suitable
            if (isSuitableForBaileyShop(level, testPos)) {
                return testPos;
            }
        }
        
        // If no suitable location found, use player position as fallback
        int playerSurfaceY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, playerPos.getX(), playerPos.getZ());
        return new BlockPos(playerPos.getX(), playerSurfaceY, playerPos.getZ());
    }
    
    private static boolean isSuitableForBaileyShop(ServerLevel level, BlockPos centerPos) {
        // Check if there's enough flat space (9x6 area for new structure)
        for (int x = -4; x <= 4; x++) {
            for (int z = -3; z <= 2; z++) {
                BlockPos checkPos = centerPos.offset(x, 0, z);
                BlockPos abovePos = centerPos.offset(x, 1, z);
                BlockPos above2Pos = centerPos.offset(x, 2, z);
                BlockPos above3Pos = centerPos.offset(x, 3, z);
                BlockPos above4Pos = centerPos.offset(x, 4, z);
                BlockPos above5Pos = centerPos.offset(x, 5, z);
                
                // Check if ground exists (not air)
                if (level.getBlockState(checkPos).isAir()) {
                    return false;
                }
                
                // Check if space above is clear for building (9 blocks high)
                if (!level.getBlockState(abovePos).isAir() || 
                    !level.getBlockState(above2Pos).isAir() || 
                    !level.getBlockState(above3Pos).isAir() ||
                    !level.getBlockState(above4Pos).isAir() ||
                    !level.getBlockState(above5Pos).isAir()) {
                    return false;
                }
            }
        }
        
        return true;
    }
}