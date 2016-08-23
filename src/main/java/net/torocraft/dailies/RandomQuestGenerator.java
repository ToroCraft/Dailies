package net.torocraft.dailies;

import java.util.HashSet;
import java.util.Set;

import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.Reward;
import net.torocraft.dailies.quests.TypedInteger;

public class RandomQuestGenerator {
	
	Set<DailyQuest> questList;
	
	public Set<DailyQuest> generateQuests() {
		generateQuestData();
		return questList;
	}
	
	private void generateQuestData() {
		questList = new HashSet<DailyQuest>();
		
		DailyQuest quest1 = new DailyQuest();
		quest1.id = "57ba47322213bb1100c72ed5";
		quest1.name = "Here There Be Zombos";
		quest1.description = "Kill 10 Zombies";
		quest1.type = "hunt";
		quest1.status = "available";
		quest1.progress = 0;
		quest1.target = new TypedInteger();
		quest1.target.type = 54;
		quest1.target.quantity = 10;
		quest1.reward = new Reward();
		quest1.reward.type = 384;
		quest1.reward.quantity = 30;
		questList.add(quest1);
		
		DailyQuest quest2 = new DailyQuest();
		quest2.id = "57ba47322213bb1100c72ed7";
		quest2.name = "Ranged Ragers";
		quest2.description = "Kill 10 Skeletons";
		quest2.type = "hunt";
		quest2.status = "available";
		quest2.progress = 0;
		quest2.target = new TypedInteger();
		quest2.target.type = 51;
		quest2.target.quantity = 10;
		quest2.reward = new Reward();
		quest2.reward.type = 384;
		quest2.reward.quantity = 30;
		questList.add(quest2);
		
		DailyQuest quest3 = new DailyQuest();
		quest3.id = "57ba47322213bb1100c72ed9";
		quest3.name = "Pressure and Time";
		quest3.description = "Gather 64 Coal";
		quest3.type = "gather";
		quest3.status = "available";
		quest3.progress = 0;
		quest3.target = new TypedInteger();
		quest3.target.type = 263;
		quest3.target.quantity = 64;
		quest3.reward = new Reward();
		quest3.reward.type = 264;
		quest3.reward.quantity = 2;
		questList.add(quest3);
		
		DailyQuest quest4 = new DailyQuest();
		quest4.id = "57ba47322213bb1100c72ed8";
		quest4.name = "Hiss Hiss Boom Boom";
		quest4.description = "Kill 10 Creepers";
		quest4.type = "hunt";
		quest4.status = "available";
		quest4.progress = 0;
		quest4.target = new TypedInteger();
		quest4.target.type = 50;
		quest4.target.quantity = 10;
		quest4.reward = new Reward();
		quest4.reward.type = 264;
		quest4.reward.quantity = 2;
		questList.add(quest4);
						
		DailyQuest quest5 = new DailyQuest();
		quest5.id = "57ba47322213bb1100c72ed6";
		quest5.name = "Mean Green";
		quest5.description = "Gather 5 Emeralds";
		quest5.type = "gather";
		quest5.status = "available";
		quest5.progress = 0;
		quest5.target = new TypedInteger();
		quest5.target.type = 388;
		quest5.target.quantity = 5;
		quest5.reward = new Reward();
		quest5.reward.type = 384;
		quest5.reward.quantity = 30;
		questList.add(quest5);
	}
}
