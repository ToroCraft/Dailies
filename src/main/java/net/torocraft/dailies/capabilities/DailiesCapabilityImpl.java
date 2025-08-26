package net.torocraft.dailies.capabilities;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.network.remote.QuestActionHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesCapabilityImpl implements IDailiesCapability {

	private Set<DailyQuest> availableQuests = new HashSet<>();
	private Set<DailyQuest> acceptedQuests = new HashSet<>();
	private Set<DailyQuest> completedQuests = new HashSet<>();

	@Override
		public void completeQuest(final Player player, final DailyQuest quest) {
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
				try {
					new QuestActionHandler(player, quest.id).complete();
				} catch (DailiesException e) {
					//player.sendMessage(e.getMessageAsTextComponent());
				}
			}
		}).start();
	}

	@Override
	   public void hunt(Player playerIn, LivingEntity mob) {
		   ServerPlayer player = (ServerPlayer) playerIn;

		DailyQuest quest = huntNextQuest(player, mob);

		if (quest == null) {
			return;
		}

		if (quest.isComplete()) { 
			quest.reward(player);
			completeQuest(player, quest);
		} else {
			PacketHandler.questProgressUpdate(player, quest);
		}
		PacketHandler.questsUpdate(player, QuestsFilter.ACCEPTED, getAcceptedQuests());
	}

	private DailyQuest huntNextQuest(Player player, LivingEntity mob) {
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

	private void displayAchievement(DailyQuest quest, Player player) {
		//DailiesPacketHandler.INSTANCE.sendTo(new AchievementToClient(quest), player);
	}

	@Override
	public CompoundTag writeNBT() {
		CompoundTag c = new CompoundTag();
		writeQuestsList(c, "availableQuests", availableQuests);
		writeQuestsList(c, "acceptedQuests", acceptedQuests);
		writeQuestsList(c, "completedQuests", completedQuests);
		return c;
	}

	@Override
	public void readNBT(CompoundTag b) {
		if (b == null) {
			acceptedQuests = new HashSet<DailyQuest>();
			return;
		}
		
		availableQuests = readQuestList(b, "availableQuests");
		acceptedQuests = readQuestList(b, "acceptedQuests");
		completedQuests = readQuestList(b, "completedQuests");
	}

	public static void writeQuestsList(CompoundTag c, String key, Set<DailyQuest> quests) {
		ListTag list = new ListTag();
		if (quests != null) {
			for (DailyQuest quest : quests) {
				list.add(quest.writeNBT());
			}
		}
		c.put(key, list);
	}

	public static Set<DailyQuest> readQuestList(CompoundTag b, String key) {
		Set<DailyQuest> quests = new HashSet<>();
		ListTag list = (ListTag) b.get(key);
		if (list == null) {
			return quests;
		}
		for (int i = 0; i < list.size(); i++) {
			DailyQuest quest = new DailyQuest();
			quest.readNBT(list.getCompound(i));
			quests.add(quest);
		}
		return quests;
	}

	@Override
	public void acceptQuest(Player player, DailyQuest quest) throws DailiesException {
		if (acceptedQuests == null) {
			return;
		}
		
		if (acceptedQuests.size() >= DailiesMod.MAX_QUESTS_ACCEPTABLE) {
			throw DailiesException.ACCEPTED_QUEST_LIMIT_HIT();
		}
		
		DailyQuest playerQuest = (DailyQuest) quest.clone();
		playerQuest.date = System.currentTimeMillis();
		acceptedQuests.add(playerQuest);
		availableQuests.remove(quest);
		new QuestActionHandler(player, quest.id).accept();
	}

	@Override
	public void abandonQuest(Player player, DailyQuest quest) {
		if (acceptedQuests == null) {
			return;
		}

		quest.progress = 0;
		acceptedQuests.remove(quest);
		
		if (questWasAcceptedToday(quest)) {
			availableQuests.add(quest);
		}
		
		try {
			new QuestActionHandler(player, quest.id).abandon();
		} catch (DailiesException e) {
			//player.sendMessage(e.getMessageAsTextComponent());
		}
	}
	
	private boolean questWasAcceptedToday(DailyQuest quest) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		long timeAtTheStartOfToday = cal.getTimeInMillis();
		return quest.date >= timeAtTheStartOfToday;
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
	public void sendAcceptedQuestsToClient(Player player) {
		//DailiesPacketHandler.INSTANCE.sendTo(new AcceptedQuestsToClient(getAcceptedQuests()), (EntityPlayerMP)player);
	}

	private static class Factory implements Callable<IDailiesCapability> {

		@Override
		public IDailiesCapability call() throws Exception {
			return new DailiesCapabilityImpl();
		}
	}
}