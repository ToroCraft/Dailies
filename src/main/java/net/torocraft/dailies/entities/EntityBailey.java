package net.torocraft.dailies.entities;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.dailies.DailiesGuiHandler;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.entities.render.RenderBailey;

public class EntityBailey extends EntityVillager {

	private MerchantRecipeList buyingList;
	
	public static String NAME = "bailey";

	public EntityBailey(World world) {
		super(world);
		this.setSize(0.6F, 1.95F);
	}

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(EntityBailey.class, NAME, entityId, DailiesMod.instance, 60, 2, true);
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
		boolean flag = stack != null && stack.getItem() == Items.SPAWN_EGG;

		if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()) {
			if (!this.worldObj.isRemote && (this.buyingList == null || !this.buyingList.isEmpty())) {
				player.openGui(DailiesMod.instance, DailiesGuiHandler.getGuiID(), this.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
			}

			player.addStat(StatList.TALKED_TO_VILLAGER);
			return true;
		} else {
			return super.processInteract(player, hand, stack);
		}
	}
}
