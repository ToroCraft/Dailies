package net.torocraft.dailies.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;

import javax.annotation.Nonnull;

import static net.minecraft.stats.Stats.TALKED_TO_VILLAGER;

public class EntityBailey extends Villager {

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


	public EntityBailey(EntityType<? extends Villager> type, Level worldIn) {
		super(type, worldIn);
	}

	public static void init(int entityId) {
		//EntitySpawnPlacementRegistry.register(EntityBailey.class, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.WORLD_SURFACE, );
		//EntityRegistry.registerModEntity(ResourceLocation.parse(DailiesMod.MODID, NAME), EntityBailey.class, NAME, entityId, DailiesMod.instance, 60, 2, true, 0xeca58c, 0xba12c8);
	}

	@Override
	public void setHealth(float health) {
		super.setHealth(10);
	}

	@Nonnull
	@Override
	public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
		if (this.isAlive() && !this.isBaby()) {
			if (!this.level().isClientSide) {
				   ((ServerPlayer)player).openMenu(new net.minecraft.world.MenuProvider() {
					   @Override
					   public Component getDisplayName() {
						   return Component.literal("Bailey's Dailies");
					   }

					   @Override
					   @Nonnull
				   public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, @Nonnull net.minecraft.world.entity.player.Inventory inventory, @Nonnull Player player) {
						   return new net.torocraft.dailies.DailiesContainer(id, inventory);
					   }
				   });
			}
			player.awardStat(TALKED_TO_VILLAGER);
			return InteractionResult.SUCCESS;
		} else {
			return super.mobInteract(player, hand);
		}
	}

	@Override
	   public Component getDisplayName() {
		   return Component.literal("Bailey");
	   }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData data) {
        this.setCustomName(Component.literal("Bailey"));
        return super.finalizeSpawn(world, difficulty, reason, data);
    }	   private void setVariantByCurrentBiome() {
		   Holder<Biome> biomeHolder = this.level().getBiome(this.blockPosition());
		   ResourceKey<Biome> biomeKey = biomeHolder.unwrap().left().orElse(null);
		   if (biomeKey == Biomes.TAIGA) {
			   variant = BaileyVariant.TAIGA;
		   } else if (biomeKey == Biomes.DESERT) {
			   variant = BaileyVariant.DESERT;
		   } else if (biomeKey == Biomes.SAVANNA) {
			   variant = BaileyVariant.SAVANNA;
		   } else {
			   variant = BaileyVariant.PLAINS;
		   }
	   }
	
	// NBT read/write methods should be implemented using addAdditionalSaveData and readAdditionalSaveData in 1.18.2+
	@Override
	public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (variant != null) {
			compound.putString("BaileyVariant", variant.toString());
		}
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		try {
			variant = BaileyVariant.valueOf(compound.getString("BaileyVariant"));
		} catch (Exception e) {
			setVariantByCurrentBiome();
		}
	}


}
