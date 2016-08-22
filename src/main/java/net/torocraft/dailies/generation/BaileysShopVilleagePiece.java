package net.torocraft.dailies.generation;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.torocraft.dailies.entities.EntityBailey;

public class BaileysShopVilleagePiece extends StructureVillagePieces.Village {
	public BaileysShopVilleagePiece() {
	}

	public BaileysShopVilleagePiece(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox p_i45571_4_, EnumFacing facing) {
		super(start, type);
		this.setCoordBaseMode(facing);
		this.boundingBox = p_i45571_4_;
	}

	public static BaileysShopVilleagePiece createPiece(StructureVillagePieces.Start start, List<StructureComponent> p_175850_1_, Random rand, int x, int y, int z, EnumFacing facing, int p_175850_7_) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 9, 6, facing);
		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175850_1_, structureboundingbox) == null ? new BaileysShopVilleagePiece(start, p_175850_7_, rand, structureboundingbox, facing) : null;
	}

	/**
	 * second Part of Structure generating, this for example places Spiderwebs,
	 * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
	 */

	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if (this.averageGroundLvl < 0) {
			this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

			if (this.averageGroundLvl < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 9 - 1, 0);
		}
		// -5599655086621954440

		// clear inside
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

		// floor
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 0, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 8, 1, 5, Blocks.CARPET.getDefaultState(), Blocks.CARPET.getDefaultState(), false);

		// front and back wall
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 0, 8, 4, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);

		// front and back top wall area
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 0, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.LOG.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 1, 8, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.LOG.getDefaultState(), false);

		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 0, 5, 3, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 2, 8, 5, 3, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);

		// lights
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 2, 1, 5, 3, Blocks.GLOWSTONE.getDefaultState(), Blocks.GLOWSTONE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 5, 2, 3, 5, 3, Blocks.GLOWSTONE.getDefaultState(), Blocks.GLOWSTONE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 5, 3, Blocks.GLOWSTONE.getDefaultState(), Blocks.GLOWSTONE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 5, 2, 7, 5, 3, Blocks.GLOWSTONE.getDefaultState(), Blocks.GLOWSTONE.getDefaultState(), false);

		// side walls
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 8, 4, 0, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 5, 8, 4, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);

		// side windows
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 0, 6, 2, 0, Blocks.GLASS_PANE.getDefaultState(), Blocks.GLASS_PANE.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 5, 6, 2, 5, Blocks.GLASS_PANE.getDefaultState(), Blocks.GLASS_PANE.getDefaultState(), false);

		// corners
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 4, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 4, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 5, 0, 4, 5, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 5, 8, 4, 5, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);

		// front doors
		this.placeDoorCurrentPosition(worldIn, structureBoundingBoxIn, randomIn, 0, 1, 1, EnumFacing.EAST);
		this.placeDoorCurrentPosition(worldIn, structureBoundingBoxIn, randomIn, 0, 1, 4, EnumFacing.EAST);

		// side doors
		this.placeDoorCurrentPosition(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.NORTH);
		this.placeDoorCurrentPosition(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 5, EnumFacing.SOUTH);

		// counter
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 1, 6, 1, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 6, 3, 4, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);


		// roof
		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				this.setBlockState(worldIn, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), j, 4 + i, i, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH), j, 4 + i, 5 - i, structureBoundingBoxIn);
			}
		}

		// foundation
		if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getMaterial() == Material.AIR && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getMaterial() != Material.AIR) {
			this.setBlockState(worldIn, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 1, 0, -1, structureBoundingBoxIn);
		}

		for (int l = 0; l < 6; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.clearCurrentPositionBlocksUpwards(worldIn, k, 9, l, structureBoundingBoxIn);
				this.replaceAirAndLiquidDownwards(worldIn, Blocks.COBBLESTONE.getDefaultState(), k, -1, l, structureBoundingBoxIn);
			}
		}

		this.spawnBailey(worldIn, structureBoundingBoxIn, 7, 1, 2, 1);
		return true;
	}

	protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession) {
		return 1;
	}
	
    protected void spawnBailey(World worldIn, StructureBoundingBox structurebb, int x, int y, int z, int count)
    {
        int j = this.getXWithOffset(x, z);
        int k = this.getYWithOffset(y);
        int l = this.getZWithOffset(x, z);

        if (!structurebb.isVecInside(new BlockPos(j, k, l)))
        {
            return;
        }
        
        EntityBailey bailey = new EntityBailey(worldIn);
		bailey.setLocationAndAngles((double) j + 0.5D, (double) k, (double) l + 0.5D, 180F, 0.0F);
        bailey.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(bailey)), (IEntityLivingData)null);
        worldIn.spawnEntityInWorld(bailey);
    }
}