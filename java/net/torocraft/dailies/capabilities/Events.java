package net.torocraft.dailies.capabilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.network.DailiesNetworkException;
import net.torocraft.dailies.network.QuestInventoryFetcher;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.RandomQuestGenerator;

public class Events {

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

		dailies.hunt(player, e);
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

		event.addCapability(new ResourceLocation(CapabilityDailiesHandler.NAME), new Provider());
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		final EntityPlayer player = event.player;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				setupDailiesData(player);
			}
		}).start();
	}
	
	private void setupDailiesData(EntityPlayer player) {;
		IDailiesCapability cap = player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
		Set<DailyQuest> serversDailyQuests = getDailyQuests(player);
		
		List<DailyQuest> openDailyQuests = new ArrayList<DailyQuest>();
		List<DailyQuest> acceptedDailyQuests = new ArrayList<DailyQuest>();

		for (DailyQuest quest : serversDailyQuests) {
			if ("available".equals(quest.status)) {
				openDailyQuests.add(quest);
			} else if ("accepted".equals(quest.status)) {
				acceptedDailyQuests.add(quest);
			}
		}
		
		cap.setAvailableQuests(new HashSet<DailyQuest>(openDailyQuests));
		cap.setAcceptedQuests(new HashSet<DailyQuest>(acceptedDailyQuests));
		
		cap.writeNBT();
	}
	
	private Set<DailyQuest> getDailyQuests(EntityPlayer player) {
		Set<DailyQuest> quests = new HashSet<DailyQuest>();
		try {
			quests = new QuestInventoryFetcher(player).getQuestInventory();
		} catch (DailiesNetworkException e) {
			player.sendMessage(e.getMessageAsTextComponent());
			player.sendMessage(new TextComponentString("Randomly generating quests instead."));
			quests = new RandomQuestGenerator().generateQuests();
		} catch (DailiesException e) {
			player.sendMessage(e.getMessageAsTextComponent());
		}
		return quests;
	}
}