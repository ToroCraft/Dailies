package net.torocraft.dailies.entities;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.dailies.DailiesGuiHandler;
import net.torocraft.dailies.DailiesMod;

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
	protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
//        this.tasks.addTask(9, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
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
}
