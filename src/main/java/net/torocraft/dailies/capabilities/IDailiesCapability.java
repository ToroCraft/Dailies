package net.torocraft.dailies.capabilities;

import java.util.Set;

import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.quests.DailyQuest;

public interface IDailiesCapability {

	void hunt(PlayerEntity player, LivingEntity mob);

	CompoundNBT writeNBT();

	void readNBT(CompoundNBT c);

	void acceptQuest(PlayerEntity player, DailyQuest quest) throws DailiesException;

	void abandonQuest(PlayerEntity player, DailyQuest quest);
	
	void completeQuest(PlayerEntity player, DailyQuest quest);

	Set<DailyQuest> getAcceptedQuests();

	void setAcceptedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getCompletedQuests();

	void setCompletedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getAvailableQuests();

	void setAvailableQuests(Set<DailyQuest> quests);

	DailyQuest getAcceptedQuestById(String questId);

	DailyQuest getAvailableQuestById(String questId);

	void sendAcceptedQuestsToClient(PlayerEntity player);

}