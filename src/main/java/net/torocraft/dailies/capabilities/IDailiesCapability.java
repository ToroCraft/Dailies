package net.torocraft.dailies.capabilities;

import java.util.Set;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.quests.DailyQuest;

public interface IDailiesCapability {

	void hunt(Player player, LivingEntity mob);

	CompoundTag writeNBT();

	void readNBT(CompoundTag c);

	void acceptQuest(Player player, DailyQuest quest) throws DailiesException;

	void abandonQuest(Player player, DailyQuest quest);
	
	void completeQuest(Player player, DailyQuest quest);

	Set<DailyQuest> getAcceptedQuests();

	void setAcceptedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getCompletedQuests();

	void setCompletedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getAvailableQuests();

	void setAvailableQuests(Set<DailyQuest> quests);

	DailyQuest getAcceptedQuestById(String questId);

	DailyQuest getAvailableQuestById(String questId);

	void sendAcceptedQuestsToClient(Player player);

}