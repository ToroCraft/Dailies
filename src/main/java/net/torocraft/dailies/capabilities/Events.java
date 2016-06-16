package net.torocraft.dailies.capabilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.quests.DailyQuest;

public class Events {

	/*
	 * SubscribeEvent public void harvestDrops(HarvestDropsEvent event) {
	 * IDailiesCapability dailes = getCapability(event.getHarvester()); if
	 * (dailes == null) { return; }
	 * 
	 * dailes.gather(1); System.out.println(dailes.statusMessage());
	 * event.getHarvester().addChatMessage(new
	 * TextComponentString(TextFormatting.RED + "" + dailes.statusMessage())); }
	 */

	@SubscribeEvent
	public void onGather(EntityItemPickupEvent event) {
		IDailiesCapability dailies = getCapability(event.getEntityPlayer());
		if (dailies == null) {
			return;
		}

		DailyQuest quest = dailies.gather(event.getEntityPlayer(), event.getItem());

		if (quest != null) {
			event.setCanceled(true);
			event.getItem().setDead();
		}

		DailiesMod.proxy.displayQuestProgress(quest);
	}
	/*
	 * @SubscribeEvent public void onPlayerJoin(EntityJoinWorldEvent event) {
	 * if(!(event.getEntity() instanceof EntityPlayer)){ return; }
	 * 
	 * IDailiesCapability dailes =
	 * getCapability((EntityPlayer)event.getEntity()); if (dailes == null) {
	 * return; }
	 * 
	 * }
	 */

	@SubscribeEvent
	public void onHunt(LivingDeathEvent event) {

		EntityPlayer player = null;

		EntityLivingBase e = (EntityLivingBase) event.getEntity();
		DamageSource source = event.getSource();

		if (source.getEntity() instanceof EntityPlayer) {
			player = (EntityPlayer) source.getEntity();
		}

		if (player == null) {
			return;
		}

		IDailiesCapability dailies = getCapability(player);
		if (dailies == null) {
			return;
		}

		DailyQuest quest = dailies.hunt(player, e);

		DailiesMod.proxy.displayQuestProgress(quest);
	}

	@SubscribeEvent
	public void onDeath(PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			return;
		}

		IDailiesCapability newDailies = getCapability(event.getEntityPlayer());
		IDailiesCapability originalDailies = getCapability(event.getOriginal());

		if (newDailies == null || originalDailies == null) {
			return;
		}

		newDailies.readNBT(originalDailies.writeNBT());
	}

	@SubscribeEvent
	public void onSave(PlayerEvent.SaveToFile event) {
		IDailiesCapability dailies = getCapability(event.getEntityPlayer());
		if (dailies == null) {
			return;
		}
		event.getEntityPlayer().getEntityData().setTag(CapabilityDailiesHandler.NAME, dailies.writeNBT());
	}

	@SubscribeEvent
	public void onLoad(PlayerEvent.LoadFromFile event) {
		IDailiesCapability dailies = getCapability(event.getEntityPlayer());
		if (dailies == null) {
			return;
		}
		dailies.readNBT((NBTTagCompound) event.getEntityPlayer().getEntityData().getTag(CapabilityDailiesHandler.NAME));
	}

	private IDailiesCapability getCapability(EntityPlayer player) {
		if (isMissingCapability(player)) {
			return null;
		}
		return player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
	}

	private boolean isMissingCapability(EntityPlayer player) {
		return player == null || !player.hasCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
	}

	@SubscribeEvent
	public void onEntityLoad(AttachCapabilitiesEvent.Entity event) {

		if (!(event.getEntity() instanceof EntityPlayer)) {
			return;
		}

		try {
			System.out.println("loading cap to player: " + ((EntityPlayer) event.getEntity()).getName());
		} catch (Exception e) {
			System.out.println("loading cap to player [" + event.getEntity().getClass().getName() + "]");
		}

		event.addCapability(new ResourceLocation(CapabilityDailiesHandler.NAME), new Provider());

	}

}