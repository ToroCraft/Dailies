package net.torocraft.dailies.capabilities;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.dailies.quests.DailyQuest;

public interface IDailiesCapability {

	/**
	 * return true when quest target was hit
	 */
	DailyQuest gather(EntityPlayer player, EntityItem item);

	/**
	 * return true when quest target was hit
	 */
	DailyQuest hunt(EntityPlayer player, EntityLivingBase mob);

	NBTTagCompound writeNBT();

	void readNBT(NBTTagCompound c);

	void acceptQuest(DailyQuest quest);

	void abandonQuest(DailyQuest quest);

	Set<DailyQuest> getAcceptedQuests();

	void setAcceptedQuests(Set<DailyQuest> quests);

	Set<DailyQuest> getCompletedQuests();

	void setCompletedQuests(Set<DailyQuest> quests);

}