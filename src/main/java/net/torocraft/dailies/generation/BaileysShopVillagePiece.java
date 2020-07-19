package net.torocraft.dailies.generation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.torocraft.dailies.entities.EntityBailey;

public class BaileysShopVillagePiece extends VillagePieces {
	private static final ResourceLocation field_202592_e = new ResourceLocation("igloo/top");
	private static final ResourceLocation field_202593_f = new ResourceLocation("igloo/middle");
	private static final ResourceLocation field_202594_g = new ResourceLocation("igloo/bottom");
	private static final Map<ResourceLocation, BlockPos> field_207621_d = ImmutableMap.of(field_202592_e, new BlockPos(3, 5, 5), field_202593_f, new BlockPos(1, 3, 1), field_202594_g, new BlockPos(3, 6, 7));
	private static final Map<ResourceLocation, BlockPos> field_207622_e = ImmutableMap.of(field_202592_e, BlockPos.ZERO, field_202593_f, new BlockPos(2, -3, 4), field_202594_g, new BlockPos(0, -3, -2));

	public static void func_236991_a_(TemplateManager p_236991_0_, BlockPos p_236991_1_, Rotation p_236991_2_, List<StructurePiece> p_236991_3_, Random p_236991_4_) {
		if (p_236991_4_.nextDouble() < 0.5D) {
			int i = p_236991_4_.nextInt(8) + 4;
			p_236991_3_.add(new BaileysShopVillagePiece.Piece(p_236991_0_, field_202594_g, p_236991_1_, p_236991_2_, i * 3));

			for(int j = 0; j < i - 1; ++j) {
				p_236991_3_.add(new BaileysShopVillagePiece.Piece(p_236991_0_, field_202593_f, p_236991_1_, p_236991_2_, j * 3));
			}
		}

		p_236991_3_.add(new BaileysShopVillagePiece.Piece(p_236991_0_, field_202592_e, p_236991_1_, p_236991_2_, 0));
	}

	public static class Piece extends TemplateStructurePiece {
		private final ResourceLocation field_207615_d;
		private final Rotation field_207616_e;

		public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
			super(IStructurePieceType.IGLU, 0);
			this.field_207615_d = p_i49313_2_;
			BlockPos blockpos = BaileysShopVillagePiece.field_207622_e.get(p_i49313_2_);
			this.templatePosition = p_i49313_3_.add(blockpos.getX(), blockpos.getY() - p_i49313_5_, blockpos.getZ());
			this.field_207616_e = p_i49313_4_;
			this.func_207614_a(p_i49313_1_);
		}

		public Piece(TemplateManager p_i50566_1_, CompoundNBT p_i50566_2_) {
			super(IStructurePieceType.IGLU, p_i50566_2_);
			this.field_207615_d = new ResourceLocation(p_i50566_2_.getString("Template"));
			this.field_207616_e = Rotation.valueOf(p_i50566_2_.getString("Rot"));
			this.func_207614_a(p_i50566_1_);
		}

		private void func_207614_a(TemplateManager p_207614_1_) {
			Template template = p_207614_1_.getTemplateDefaulted(this.field_207615_d);
			PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset(BaileysShopVillagePiece.field_207621_d.get(this.field_207615_d)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
			this.setup(template, this.templatePosition, placementsettings);
		}

		/**
		 * (abstract) Helper method to read subclass data from NBT
		 */
		protected void readAdditional(CompoundNBT tagCompound) {
			super.readAdditional(tagCompound);
			tagCompound.putString("Template", this.field_207615_d.toString());
			tagCompound.putString("Rot", this.field_207616_e.name());
		}

		protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
			if ("chest".equals(function)) {
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				TileEntity tileentity = worldIn.getTileEntity(pos.down());
				if (tileentity instanceof ChestTileEntity) {
					((ChestTileEntity)tileentity).setLootTable(LootTables.CHESTS_IGLOO_CHEST, rand.nextLong());
				}

			}
		}

		public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
			PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset(BaileysShopVillagePiece.field_207621_d.get(this.field_207615_d)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
			BlockPos blockpos = BaileysShopVillagePiece.field_207622_e.get(this.field_207615_d);
			BlockPos blockpos1 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(3 - blockpos.getX(), 0, 0 - blockpos.getZ())));
			int i = p_230383_1_.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
			BlockPos blockpos2 = this.templatePosition;
			this.templatePosition = this.templatePosition.add(0, i - 90 - 1, 0);
			boolean flag = super.func_230383_a_(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
			if (this.field_207615_d.equals(BaileysShopVillagePiece.field_202592_e)) {
				BlockPos blockpos3 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(3, 0, 5)));
				BlockState blockstate = p_230383_1_.getBlockState(blockpos3.down());
				if (!blockstate.isAir() && !blockstate.isIn(Blocks.LADDER)) {
					p_230383_1_.setBlockState(blockpos3, Blocks.SNOW_BLOCK.getDefaultState(), 3);
				}
			}

			this.templatePosition = blockpos2;
			return flag;
		}
	}
	/*public BaileysShopVillagePiece(VillagePieces start, int type, Random rand, StructureBoundingBox p_i45571_4_, EnumFacing facing) {
		super(start, type);
		this.setCoordBaseMode(facing);
		this.boundingBox = p_i45571_4_;
	}

	public static BaileysShopVillagePiece createPiece(StructureVillagePieces.Start start, List<StructureComponent> p_175850_1_, Random rand, int x, int y, int z, EnumFacing facing, int p_175850_7_) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 9, 6, facing);
		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175850_1_, structureboundingbox) == null ? new BaileysShopVillagePiece(start, p_175850_7_, rand, structureboundingbox, facing) : null;
	}

	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if (this.averageGroundLvl < 0) {
			this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

			if (this.averageGroundLvl < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 9 - 1, 0);
		}

		IBlockState cobble = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
		IBlockState carpet = this.getBiomeSpecificBlockState(Blocks.CARPET.getDefaultState());
		IBlockState planks = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
		IBlockState logs = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
		IBlockState glowstone = this.getBiomeSpecificBlockState(Blocks.GLOWSTONE.getDefaultState());
		IBlockState glassPane = this.getBiomeSpecificBlockState(Blocks.GLASS_PANE.getDefaultState());
		IBlockState fence = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());

		IBlockState stairsNorth = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		IBlockState stairsSouth = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));

		// clear inside
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

		// floor
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 0, 5, cobble, cobble, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 8, 1, 5, carpet, carpet, false);

		// front and back wall
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 5, cobble, cobble, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 0, 8, 4, 5, cobble, cobble, false);

		// front and back top wall area
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 0, 4, 4, planks, logs, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 1, 8, 4, 4, planks, logs, false);

		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 0, 5, 3, logs, logs, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 2, 8, 5, 3, logs, logs, false);

		// lights
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 2, 1, 5, 3, glowstone, glowstone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 5, 2, 3, 5, 3, glowstone, glowstone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 5, 3, glowstone, glowstone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 5, 2, 7, 5, 3, glowstone, glowstone, false);

		// side walls
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 8, 4, 0, cobble, cobble, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 5, 8, 4, 5, cobble, cobble, false);

		// side windows
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 0, 6, 2, 0, glassPane, glassPane, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 5, 6, 2, 5, glassPane, glassPane, false);

		// back window
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 2, 8, 3, 3, glassPane, glassPane, false);

		// corners
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 4, 0, logs, logs, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 4, 0, logs, logs, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 5, 0, 4, 5, logs, logs, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 5, 8, 4, 5, logs, logs, false);

		// door
		this.generateDoor(worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.NORTH, Blocks.OAK_DOOR);

		// counter
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 1, 6, 1, 4, planks, planks, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 6, 3, 4, fence, fence, false);

		// roof
		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				this.setBlockState(worldIn, stairsNorth, j, 4 + i, i, structureBoundingBoxIn);
				this.setBlockState(worldIn, stairsSouth, j, 4 + i, 5 - i, structureBoundingBoxIn);
			}
		}

		// foundation
		if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getMaterial() == Material.AIR && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getMaterial() != Material.AIR) {
			this.setBlockState(worldIn, stairsNorth, 1, 0, -1, structureBoundingBoxIn);
		}

		for (int l = 0; l < 6; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.clearCurrentPositionBlocksUpwards(worldIn, k, 9, l, structureBoundingBoxIn);
				this.replaceAirAndLiquidDownwards(worldIn, cobble, k, -1, l, structureBoundingBoxIn);
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

        EntityBailey bailey = new EntityBailey(EntityType.VILLAGER, worldIn);
		bailey.setLocationAndAngles((double) j + 0.5D, (double) k, (double) l + 0.5D, 180F, 0.0F);
        bailey.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(new BlockPos(bailey.getPosX(), bailey.getPosY(), bailey.getPosZ())), SpawnReason.TRIGGERED, null, null);
        worldIn.addEntity(bailey);
    }*/
}