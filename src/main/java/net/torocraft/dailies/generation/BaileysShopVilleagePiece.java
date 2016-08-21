package net.torocraft.dailies.generation;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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

		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 0, 5, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 8, 5, 5, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 6, 1, 8, 6, 4, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 7, 2, 8, 7, 3, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);

		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				this.setBlockState(worldIn, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), j, 6 + i, i, structureBoundingBoxIn);
				this.setBlockState(worldIn, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH), j, 6 + i, 5 - i, structureBoundingBoxIn);
			}
		}

		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 5, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 8, 1, 5, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 0, 8, 1, 4, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 0, 7, 1, 0, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 4, 0, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 4, 5, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 5, 8, 4, 5, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 0, 8, 4, 0, Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 7, 4, 5, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 1, 8, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 4, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 4, 3, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 3, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 3, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 2, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 3, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 2, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 3, 2, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 8, 3, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 5, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 3, 2, 5, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 6, 2, 5, structureBoundingBoxIn);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 7, 4, 1, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 4, 7, 4, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
		this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 7, 1, 4, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST), 7, 1, 3, structureBoundingBoxIn);
		IBlockState iblockstate = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
		this.setBlockState(worldIn, iblockstate, 6, 1, 4, structureBoundingBoxIn);
		this.setBlockState(worldIn, iblockstate, 5, 1, 4, structureBoundingBoxIn);
		this.setBlockState(worldIn, iblockstate, 4, 1, 4, structureBoundingBoxIn);
		this.setBlockState(worldIn, iblockstate, 3, 1, 4, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 1, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 4, 1, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 4, 2, 3, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 7, 1, 1, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
		this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
		this.placeDoorCurrentPosition(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.NORTH);

		if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getMaterial() == Material.AIR && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getMaterial() != Material.AIR) {
			this.setBlockState(worldIn, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH), 1, 0, -1, structureBoundingBoxIn);
		}

		for (int l = 0; l < 6; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.clearCurrentPositionBlocksUpwards(worldIn, k, 9, l, structureBoundingBoxIn);
				this.replaceAirAndLiquidDownwards(worldIn, Blocks.DIAMOND_BLOCK.getDefaultState(), k, -1, l, structureBoundingBoxIn);
			}
		}

		//this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
		this.spawnBailey(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
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
        bailey.setLocationAndAngles((double)j + 0.5D, (double)k, (double)l + 0.5D, 0.0F, 0.0F);
        bailey.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(bailey)), (IEntityLivingData)null);
        worldIn.spawnEntityInWorld(bailey);
    }
}