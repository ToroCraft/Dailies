package net.torocraft.dailies.capabilities;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.torocraft.dailies.DailiesRequester;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesCapabilityImpl implements IDailiesCapability {

	private Set<DailyQuest> availableQuests;
	private Set<DailyQuest> acceptedQuests;
	private Set<DailyQuest> completedQuests;

	@Override
	public DailyQuest gather(EntityPlayer player, EntityItem item) {
		DailyQuest quest = gatherNextQuest(player, item);

		if (quest == null) {
			return null;
		}

		if (quest.isComplete()) {
			quest.reward(player);
			try {
				displayAchievement(quest, player);
			} catch (Exception e) {

			}
			completeQuest(quest, player);
		}

		return quest;
	}

	private DailyQuest gatherNextQuest(EntityPlayer player, EntityItem item) {
		if (acceptedQuests == null) {
			return null;
		}
		for (DailyQuest q : acceptedQuests) {
			if (q.gather(player, item)) {
				return q;
			}
		}
		return null;
	}

	private void completeQuest(final DailyQuest quest, final EntityPlayer player) {
		acceptedQuests.remove(quest);
		if (completedQuests == null) {
			completedQuests = new HashSet<DailyQuest>();
		}
		completedQuests.add(quest);

		new Thread(new Runnable() {
			@Override
			public void run() {
				new DailiesRequester().completeQuest(player.getName(), quest.id);
			}
		}).start();
	}

	@Override
	public DailyQuest hunt(EntityPlayer player, EntityLivingBase mob) {
		DailyQuest quest = huntNextQuest(player, mob);

		if (quest == null) {
			return null;
		}

		if (quest.isComplete()) {
			quest.reward(player);
			try {
				if (player.worldObj.isRemote) {
					displayAchievement(quest, player);
				}
			} catch (Exception e) {

			}
			completeQuest(quest, player);
		}

		return quest;
	}

	private DailyQuest huntNextQuest(EntityPlayer player, EntityLivingBase mob) {
		if (acceptedQuests == null) {
			return null;
		}
		for (DailyQuest q : acceptedQuests) {
			if (q.hunt(player, mob)) {
				return q;
			}
		}
		return null;
	}

	private void displayAchievement(DailyQuest quest, EntityPlayer player) {

		if (FMLCommonHandler.instance().getSide().isClient()) {
			Achievement achievement = new Achievement(quest.getDisplayName(), "dailyquestcompleted", 0, 0,
					Item.getItemById(quest.target.type), (Achievement) null);
			Minecraft.getMinecraft().guiAchievement.displayAchievement(achievement);
		}
	}

	@Override
	public NBTTagCompound writeNBT() {
		NBTTagCompound c = new NBTTagCompound();
		writeQuestsList(c, "availableQuests", availableQuests);
		writeQuestsList(c, "acceptedQuests", acceptedQuests);
		writeQuestsList(c, "completedQuests", completedQuests);
		return c;
	}

	@Override
	public void readNBT(NBTTagCompound b) {
		if (b == null) {
			acceptedQuests = new HashSet<DailyQuest>();
			return;
		}
		
		availableQuests = readQuestList(b, "availableQuests");
		acceptedQuests = readQuestList(b, "acceptedQuests");
		completedQuests = readQuestList(b, "completedQuests");
	}

	private void writeQuestsList(NBTTagCompound c, String key, Set<DailyQuest> quests) {
		NBTTagList list = new NBTTagList();
		if (quests != null) {
			for (DailyQuest quest : quests) {
				list.appendTag(quest.writeNBT());
			}
		}
		c.setTag(key, list);
	}

	private Set<DailyQuest> readQuestList(NBTTagCompound b, String key) {
		Set<DailyQuest> quests = new HashSet<DailyQuest>();
		NBTTagList list = (NBTTagList) b.getTag(key);

		if (list == null) {
			return quests;
		}

		for (int i = 0; i < list.tagCount(); i++) {
			DailyQuest quest = new DailyQuest();
			quest.readNBT(list.getCompoundTagAt(i));
			quests.add(quest);
		}

		return quests;
	}

	@Override
	public void acceptQuest(String playerName, DailyQuest quest) {
		if (acceptedQuests == null) {
			return;
		}

		DailyQuest playerQuest = (DailyQuest) quest.clone();
		playerQuest.date = System.currentTimeMillis();
		acceptedQuests.add(playerQuest);
		availableQuests.remove(quest);
		new DailiesRequester().acceptQuest(playerName, quest.id);
	}

	@Override
	public void abandonQuest(String playerName, DailyQuest quest) {
		if (acceptedQuests == null) {
			return;
		}

		acceptedQuests.remove(quest);
		new DailiesRequester().abandonQuest(playerName, quest.id);
	}
	/*
	 * private void setDefaultQuests() { quests = new ArrayList<DailyQuest>();
	 * DailyQuest quest = new DailyQuest(); Reward reward = new Reward();
	 * reward.quantity = 20; reward.type = 384; TypedInteger target = new
	 * TypedInteger(); target.type = 12; target.quantity = 2; quest.type =
	 * "gather"; quest.reward = reward; quest.target = target;
	 * 
	 * quests.add(quest);
	 * 
	 * quest = new DailyQuest(); reward = new Reward(); reward.quantity = 50;
	 * reward.type = 264; target = new TypedInteger(); target.type = 3;
	 * target.quantity = 2; quest.type = "gather"; quest.reward = reward;
	 * quest.target = target;
	 * 
	 * quests.add(quest);
	 * 
	 * quest = new DailyQuest(); reward = new Reward(); reward.quantity = 30;
	 * reward.type = 384; target = new TypedInteger(); target.type = 101;
	 * target.quantity = 2; quest.type = "hunt"; quest.reward = reward;
	 * quest.target = target;
	 * 
	 * quests.add(quest); }
	 */
	
	@Override
	public Set<DailyQuest> getAvailableQuests() {
		return availableQuests;
	}

	@Override
	public Set<DailyQuest> getAcceptedQuests() {
		return acceptedQuests;
	}

	@Override
	public Set<DailyQuest> getCompletedQuests() {
		return completedQuests;
	}
	
	@Override 
	public void setAvailableQuests(Set<DailyQuest> quests) {
		this.availableQuests = quests;
	}

	@Override
	public void setAcceptedQuests(Set<DailyQuest> quests) {
		this.acceptedQuests = quests;
	}

	@Override
	public void setCompletedQuests(Set<DailyQuest> quests) {
		this.completedQuests = quests;
	}
	
	@Override
	public DailyQuest getAcceptedQuestById(String questId) {
		if(acceptedQuests == null) {
			return null;
		}
		
		DailyQuest quest = null;
		for(DailyQuest q : acceptedQuests) {
			if(q.id.equals(questId)) {
				quest = q;
			}
		}
		return quest;
	}
	
	@Override
	public DailyQuest getAvailableQuestById(String questId) {
		if(availableQuests == null) {
			return null;
		}
		
		DailyQuest quest = null;
		for(DailyQuest q : availableQuests) {
			if(q.id.equals(questId)) {
				quest = q;
			}
		}
		return quest;
	}
}