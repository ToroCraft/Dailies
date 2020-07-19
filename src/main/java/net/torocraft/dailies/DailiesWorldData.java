package net.torocraft.dailies;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldSavedData;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesWorldData extends WorldSavedData {

	public static final String MODNAME = "DailiesMod";

	private Set<DailyQuest> dailyQuests;

	public DailiesWorldData() {
		super(MODNAME);
	}

	public DailiesWorldData(String name) {
		super(name);
	}

	@Override
	public void read(CompoundNBT nbt) {
		dailyQuests = readQuestList(nbt, "dailies");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		writeQuestsList(nbt, "dailies", dailyQuests);
		return nbt;
	}

	public Set<DailyQuest> getDailyQuests() {
		return dailyQuests;
	}

	public void setDailyQuests(Set<DailyQuest> dailies) {
		this.dailyQuests = dailies;
		markDirty();
	}

	private void writeQuestsList(CompoundNBT c, String key, Set<DailyQuest> quests) {
		ListNBT list = new ListNBT();
		for (DailyQuest quest : quests) {
			list.add(quest.writeNBT());
		}
		c.put(key, list);
	}

	private Set<DailyQuest> readQuestList(CompoundNBT b, String key) {
		Set<DailyQuest> quests = new HashSet<DailyQuest>();
		ListNBT list = (ListNBT) b.get(key);

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
