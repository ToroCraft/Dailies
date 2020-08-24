package net.torocraft.dailies.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.*;
import net.minecraftforge.fml.network.NetworkHooks;
import net.torocraft.dailies.DailiesContainer;

import javax.annotation.Nullable;

import static net.minecraft.stats.Stats.TALKED_TO_VILLAGER;

public class EntityBailey extends VillagerEntity {

	public static enum BaileyVariant {
		TAIGA, PLAINS, DESERT, SAVANNA;
		private static final List<BaileyVariant> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();
		public static BaileyVariant random()  {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
	
	public static String NAME = "bailey";
	public BaileyVariant variant;


	public EntityBailey(EntityType<? extends VillagerEntity> type, World worldIn) {
		super(type, worldIn);

	}

	public static void init(int entityId) {
		//EntitySpawnPlacementRegistry.register(EntityBailey.class, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, );
		//EntityRegistry.registerModEntity(new ResourceLocation(DailiesMod.MODID, NAME), EntityBailey.class, NAME, entityId, DailiesMod.instance, 60, 2, true, 0xeca58c, 0xba12c8);
	}

	@Override
	public void setHealth(float health) {
		super.setHealth(10);
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		if (this.isAlive() && !this.isChild()) {
			if (!this.world.isRemote) {
				NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return new TranslationTextComponent("Bailey's Dailies");
					}

					@Override
					public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
						return new DailiesContainer(id, inventory);
					}
				});
			}

			player.addStat(TALKED_TO_VILLAGER);
			return ActionResultType.FAIL;
		} else {
			return super.func_230254_b_(player, hand);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		Team team = this.getTeam();
		//String name = this.getCustomName().toString();

		//if (name == null || name.length() == 0) {
			//name = "Bailey";
		//}

		StringTextComponent textcomponentstring = new StringTextComponent("bailey");//new StringTextComponent(ScorePlayerTeam.func_237500_a_(team, new StringTextComponent("name")).toString());
		//textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
		//textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
		return textcomponentstring;
	}

	@Override
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
		setVariantByCurrentBiome();
		return data;
	}

	private void setVariantByCurrentBiome() {
		Biome biome = world.getBiome(getPositionUnderneath());
		if (biome instanceof TaigaBiome) {
			variant = BaileyVariant.TAIGA;
		} else if (biome instanceof DesertBiome) {
			variant = BaileyVariant.DESERT;
		} else if (biome instanceof PlainsBiome) {
			variant = BaileyVariant.PLAINS;
		} else if (biome instanceof SavannaBiome) {
			variant = BaileyVariant.SAVANNA;
		} else {
			variant = BaileyVariant.random();
		}
	}
	
	/*@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		try {
			variant = BaileyVariant.valueOf(compound.getString("BaileyVariant"));
		} catch (Exception e) {
			setVariantByCurrentBiome();
		}
	}



	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (variant != null) {
			compound.putString("BaileyVariant", variant.toString());
		}
	}*/

	/*@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, String.valueOf(variant));
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		try {
			variant = BaileyVariant.valueOf(ByteBufUtils.readUTF8String(additionalData));
		} catch (Exception e) {
			setVariantByCurrentBiome();
		}
	}*/
}
