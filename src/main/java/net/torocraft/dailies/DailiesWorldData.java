package net.torocraft.dailies;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesWorldData extends SavedData {

	public static final String MODNAME = "DailiesMod";

	private Set<DailyQuest> dailyQuests;



	public void load(CompoundTag nbt) {
		dailyQuests = readQuestList(nbt, "dailies");
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		writeQuestsList(nbt, "dailies", dailyQuests);
		return nbt;
	}

	public Set<DailyQuest> getDailyQuests() {
		return dailyQuests;
	}

	public void setDailyQuests(Set<DailyQuest> dailies) {
		this.dailyQuests = dailies;
		setDirty();
	}

	private void writeQuestsList(CompoundTag c, String key, Set<DailyQuest> quests) {
		ListTag list = new ListTag();
		for (DailyQuest quest : quests) {
			list.add(quest.writeNBT());
		}
		c.put(key, list);
	}

	private Set<DailyQuest> readQuestList(CompoundTag b, String key) {
		Set<DailyQuest> quests = new HashSet<DailyQuest>();
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
}
