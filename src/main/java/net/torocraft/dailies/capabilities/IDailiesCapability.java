package net.torocraft.dailies.capabilities;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.quests.DailyQuest;

public interface IDailiesCapability {

	/**
	 * return true when quest target was hit
	 */
//	DailyQuest gather(EntityPlayer player, EntityItem item);

	/**
	 * return true when quest target was hit
	 */
	void hunt(EntityPlayer player, EntityLivingBase mob);

	NBTTagCompound writeNBT();

	void readNBT(NBTTagCompound c);

	void acceptQuest(EntityPlayer player, DailyQuest quest) throws DailiesException;

	void abandonQuest(EntityPlayer player, DailyQuest quest);
	
	void completeQuest(EntityPlayer player, DailyQuest quest);

	Set<DailyQuest> getAcceptedQuests();

	void setAcceptedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getCompletedQuests();

	void setCompletedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getAvailableQuests();

	void setAvailableQuests(Set<DailyQuest> quests);

	DailyQuest getAcceptedQuestById(String questId);

	DailyQuest getAvailableQuestById(String questId);

	void sendAcceptedQuestsToClient(EntityPlayer player);

}