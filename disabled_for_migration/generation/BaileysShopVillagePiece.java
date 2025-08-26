package net.torocraft.dailies.generation;

// TODO: MIGRATE TO 1.19.2+ - This file needs structure generation API updates
// Structure generation was completely rewritten in 1.18+
// See: https://docs.minecraftforge.net/en/1.19.x/worldgen/structures/
// PRIORITY: Low - disable for now, migrate after core functionality works

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.Mirror;
import net.minecraft.world.level.levelgen.structure.Rotation;

public class BaileysShopVillagePiece extends StructurePiece {
	public static final StructurePieceType BAILEYS_SHOP = new StructurePieceType(BaileysShopVillagePiece::new) {};

	private final ResourceLocation templateLocation;
	private final Rotation rotation;
	private final BlockPos position;

	public BaileysShopVillagePiece(StructureManager structureManager, ResourceLocation templateLocation, BlockPos position, Rotation rotation, BoundingBox box) {
		super(BAILEYS_SHOP, 0, box);
		this.templateLocation = templateLocation;
		this.rotation = rotation;
		this.position = position;
	}

	public BaileysShopVillagePiece(CompoundTag nbt) {
		super(BAILEYS_SHOP, nbt);
		this.templateLocation = new ResourceLocation(nbt.getString("Template"));
		this.rotation = Rotation.valueOf(nbt.getString("Rot"));
		this.position = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putString("Template", this.templateLocation.toString());
		nbt.putString("Rot", this.rotation.name());
		nbt.putInt("PosX", this.position.getX());
		nbt.putInt("PosY", this.position.getY());
		nbt.putInt("PosZ", this.position.getZ());
	}
}