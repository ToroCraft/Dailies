package net.torocraft.dailies.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.torocraft.dailies.DailiesGuiHandler;
import net.torocraft.dailies.DailiesMod;

public class EntityBailey extends EntityVillager implements IEntityAdditionalSpawnData {
	
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
	private MerchantRecipeList buyingList;

	public EntityBailey(World world) {
		super(world);
		this.setSize(0.6F, 1.95F);
	}

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(EntityBailey.class, NAME, entityId, DailiesMod.instance, 60, 2, true, 0xeca58c, 0xba12c8);
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		boolean flag = stack != null && stack.getItem() == Items.SPAWN_EGG;

		if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()) {
			if (!this.worldObj.isRemote && (this.buyingList == null || !this.buyingList.isEmpty())) {
				player.openGui(DailiesMod.instance, DailiesGuiHandler.getGuiID(), this.worldObj,
						player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
			}

			player.addStat(StatList.TALKED_TO_VILLAGER);
			return true;
		} else {
			return super.processInteract(player, hand, stack);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		Team team = this.getTeam();
		String name = this.getCustomNameTag();

		if (name == null || name.length() == 0) {
			name = "Bailey";
		}

		TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(team, name));
		textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
		textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
		return textcomponentstring;
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
		setVariantByCurrentBiome();
		return data;
	}

	private void setVariantByCurrentBiome() {
		Biome biome = worldObj.getBiomeGenForCoords(getPosition());
		if (biome instanceof BiomeTaiga) {
			variant = BaileyVariant.TAIGA;
		} else if (biome instanceof BiomeDesert) {
			variant = BaileyVariant.DESERT;
		} else if (biome instanceof BiomePlains) {
			variant = BaileyVariant.PLAINS;
		} else if (biome instanceof BiomeSavanna) {
			variant = BaileyVariant.SAVANNA;
		} else {
			variant = BaileyVariant.random();
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		try {
			variant = BaileyVariant.valueOf(compound.getString("BaileyVariant"));
		} catch (Exception e) {
			setVariantByCurrentBiome();
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (variant != null) {
			compound.setString("BaileyVariant", variant.toString());
		}
	}

	@Override
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
	}
}
