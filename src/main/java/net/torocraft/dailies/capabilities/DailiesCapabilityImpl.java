package net.torocraft.dailies.capabilities;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.DailiesRequester;
import net.torocraft.dailies.messages.AcceptedQuestsToClient;
import net.torocraft.dailies.messages.AchievementToClient;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.messages.QuestProgressToClient;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesCapabilityImpl implements IDailiesCapability {

	private Set<DailyQuest> availableQuests;
	private Set<DailyQuest> acceptedQuests;
	private Set<DailyQuest> completedQuests;

	@Override
	public void completeQuest(final DailyQuest quest, final EntityPlayer player) {
		acceptedQuests.remove(quest);
		
		if (completedQuests == null) {
			completedQuests = new HashSet<DailyQuest>();
		}
		
		completedQuests.add(quest);
		
		displayAchievement(quest, player);
		sendAcceptedQuestsToClient(player);

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
			completeQuest(quest, player);
		} else {
			DailiesPacketHandler.INSTANCE.sendTo(new QuestProgressToClient(quest), (EntityPlayerMP)player);
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
		DailiesPacketHandler.INSTANCE.sendTo(new AchievementToClient(quest), (EntityPlayerMP)player);
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

		quest.progress = 0;
		acceptedQuests.remove(quest);
		availableQuests.add(quest);
		new DailiesRequester().abandonQuest(playerName, quest.id);
	}
	
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
	
	@Override
	public void sendAcceptedQuestsToClient(EntityPlayer player) {
		DailiesPacketHandler.INSTANCE.sendTo(new AcceptedQuestsToClient(getAcceptedQuests()), (EntityPlayerMP)player);
	}
}