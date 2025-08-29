package net.torocraft.dailies.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomQuestGenerator {
	
	Set<DailyQuest> questList;
	
	public Set<DailyQuest> generateQuests() {
		//TODO: This can be done better. 
		generateQuestData();	
		List<DailyQuest> questListCopy = new ArrayList<>(questList);
		Collections.shuffle(questListCopy);
		questList = new HashSet<>(questListCopy);
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
		quest1.target.itemId = "minecraft:zombie"; // Updated to string-based identifier
		quest1.target.quantity = 10;
		quest1.reward = new Reward();
		quest1.reward.itemId = "minecraft:experience_bottle"; // Updated to string-based identifier
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
		quest2.target.itemId = "minecraft:skeleton"; // Updated to string-based identifier
		quest2.target.quantity = 10;
		quest2.reward = new Reward();
		quest2.reward.itemId = "minecraft:experience_bottle"; // Updated to string-based identifier
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
		quest3.target.itemId = "minecraft:coal"; // Updated to string-based identifier
		quest3.target.quantity = 64;
		quest3.reward = new Reward();
		quest3.reward.itemId = "minecraft:diamond"; // Updated to string-based identifier
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
		quest4.target.itemId = "minecraft:creeper"; // Updated to string-based identifier
		quest4.target.quantity = 10;
		quest4.reward = new Reward();
		quest4.reward.itemId = "minecraft:diamond"; // Updated to string-based identifier
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
		quest5.target.itemId = "minecraft:emerald"; // Updated to string-based identifier
		quest5.target.quantity = 5;
		quest5.reward = new Reward();
		quest5.reward.itemId = "minecraft:experience_bottle"; // Updated to string-based identifier
		quest5.reward.quantity = 30;
		questList.add(quest5);
		
		// Additional quests - expanding the quest pool
		DailyQuest quest6 = new DailyQuest();
		quest6.id = "57ba47322213bb1100c72eda";
		quest6.name = "Spider Silk Harvest";
		quest6.description = "Kill 8 Spiders";
		quest6.type = "hunt";
		quest6.status = "available";
		quest6.progress = 0;
		quest6.target = new TypedInteger();
		quest6.target.itemId = "minecraft:spider";
		quest6.target.quantity = 8;
		quest6.reward = new Reward();
		quest6.reward.itemId = "minecraft:string";
		quest6.reward.quantity = 16;
		questList.add(quest6);
		
		DailyQuest quest7 = new DailyQuest();
		quest7.id = "57ba47322213bb1100c72edb";
		quest7.name = "Lumberjack Special";
		quest7.description = "Gather 128 Oak Logs";
		quest7.type = "gather";
		quest7.status = "available";
		quest7.progress = 0;
		quest7.target = new TypedInteger();
		quest7.target.itemId = "minecraft:oak_log";
		quest7.target.quantity = 128;
		quest7.reward = new Reward();
		quest7.reward.itemId = "minecraft:iron_axe";
		quest7.reward.quantity = 1;
		questList.add(quest7);
		
		DailyQuest quest8 = new DailyQuest();
		quest8.id = "57ba47322213bb1100c72edc";
		quest8.name = "Enderman Hunter";
		quest8.description = "Kill 3 Endermen";
		quest8.type = "hunt";
		quest8.status = "available";
		quest8.progress = 0;
		quest8.target = new TypedInteger();
		quest8.target.itemId = "minecraft:enderman";
		quest8.target.quantity = 3;
		quest8.reward = new Reward();
		quest8.reward.itemId = "minecraft:ender_pearl";
		quest8.reward.quantity = 5;
		questList.add(quest8);
		
		DailyQuest quest9 = new DailyQuest();
		quest9.id = "57ba47322213bb1100c72edd";
		quest9.name = "Iron Will";
		quest9.description = "Gather 32 Iron Ingots";
		quest9.type = "gather";
		quest9.status = "available";
		quest9.progress = 0;
		quest9.target = new TypedInteger();
		quest9.target.itemId = "minecraft:iron_ingot";
		quest9.target.quantity = 32;
		quest9.reward = new Reward();
		quest9.reward.itemId = "minecraft:diamond";
		quest9.reward.quantity = 3;
		questList.add(quest9);
		
		DailyQuest quest10 = new DailyQuest();
		quest10.id = "57ba47322213bb1100c72ede";
		quest10.name = "Witch Hunt";
		quest10.description = "Kill 5 Witches";
		quest10.type = "hunt";
		quest10.status = "available";
		quest10.progress = 0;
		quest10.target = new TypedInteger();
		quest10.target.itemId = "minecraft:witch";
		quest10.target.quantity = 5;
		quest10.reward = new Reward();
		quest10.reward.itemId = "minecraft:brewing_stand";
		quest10.reward.quantity = 1;
		questList.add(quest10);
		
		DailyQuest quest11 = new DailyQuest();
		quest11.id = "57ba47322213bb1100c72edf";
		quest11.name = "Golden Opportunity";
		quest11.description = "Gather 16 Gold Ingots";
		quest11.type = "gather";
		quest11.status = "available";
		quest11.progress = 0;
		quest11.target = new TypedInteger();
		quest11.target.itemId = "minecraft:gold_ingot";
		quest11.target.quantity = 16;
		quest11.reward = new Reward();
		quest11.reward.itemId = "minecraft:golden_apple";
		quest11.reward.quantity = 2;
		questList.add(quest11);
		
		DailyQuest quest12 = new DailyQuest();
		quest12.id = "57ba47322213bb1100c72ee0";
		quest12.name = "Blazing Trails";
		quest12.description = "Kill 6 Blazes";
		quest12.type = "hunt";
		quest12.status = "available";
		quest12.progress = 0;
		quest12.target = new TypedInteger();
		quest12.target.itemId = "minecraft:blaze";
		quest12.target.quantity = 6;
		quest12.reward = new Reward();
		quest12.reward.itemId = "minecraft:blaze_rod";
		quest12.reward.quantity = 8;
		questList.add(quest12);
		
		DailyQuest quest13 = new DailyQuest();
		quest13.id = "57ba47322213bb1100c72ee1";
		quest13.name = "Wheat Fields Forever";
		quest13.description = "Gather 64 Wheat";
		quest13.type = "gather";
		quest13.status = "available";
		quest13.progress = 0;
		quest13.target = new TypedInteger();
		quest13.target.itemId = "minecraft:wheat";
		quest13.target.quantity = 64;
		quest13.reward = new Reward();
		quest13.reward.itemId = "minecraft:bread";
		quest13.reward.quantity = 32;
		questList.add(quest13);
		
		DailyQuest quest14 = new DailyQuest();
		quest14.id = "57ba47322213bb1100c72ee2";
		quest14.name = "Silverfish Exterminator";
		quest14.description = "Kill 15 Silverfish";
		quest14.type = "hunt";
		quest14.status = "available";
		quest14.progress = 0;
		quest14.target = new TypedInteger();
		quest14.target.itemId = "minecraft:silverfish";
		quest14.target.quantity = 15;
		quest14.reward = new Reward();
		quest14.reward.itemId = "minecraft:experience_bottle";
		quest14.reward.quantity = 20;
		questList.add(quest14);
		
		DailyQuest quest15 = new DailyQuest();
		quest15.id = "57ba47322213bb1100c72ee3";
		quest15.name = "Redstone Engineer";
		quest15.description = "Gather 32 Redstone Dust";
		quest15.type = "gather";
		quest15.status = "available";
		quest15.progress = 0;
		quest15.target = new TypedInteger();
		quest15.target.itemId = "minecraft:redstone";
		quest15.target.quantity = 32;
		quest15.reward = new Reward();
		quest15.reward.itemId = "minecraft:redstone_block";
		quest15.reward.quantity = 2;
		questList.add(quest15);
		
		DailyQuest quest16 = new DailyQuest();
		quest16.id = "57ba47322213bb1100c72ee4";
		quest16.name = "Slime Time";
		quest16.description = "Kill 8 Slimes";
		quest16.type = "hunt";
		quest16.status = "available";
		quest16.progress = 0;
		quest16.target = new TypedInteger();
		quest16.target.itemId = "minecraft:slime";
		quest16.target.quantity = 8;
		quest16.reward = new Reward();
		quest16.reward.itemId = "minecraft:slime_ball";
		quest16.reward.quantity = 12;
		questList.add(quest16);
		
		DailyQuest quest17 = new DailyQuest();
		quest17.id = "57ba47322213bb1100c72ee5";
		quest17.name = "Ocean's Bounty";
		quest17.description = "Gather 16 Raw Fish";
		quest17.type = "gather";
		quest17.status = "available";
		quest17.progress = 0;
		quest17.target = new TypedInteger();
		quest17.target.itemId = "minecraft:cod";
		quest17.target.quantity = 16;
		quest17.reward = new Reward();
		quest17.reward.itemId = "minecraft:cooked_cod";
		quest17.reward.quantity = 20;
		questList.add(quest17);
		
		DailyQuest quest18 = new DailyQuest();
		quest18.id = "57ba47322213bb1100c72ee6";
		quest18.name = "Piglin Patrol";
		quest18.description = "Kill 12 Piglins";
		quest18.type = "hunt";
		quest18.status = "available";
		quest18.progress = 0;
		quest18.target = new TypedInteger();
		quest18.target.itemId = "minecraft:piglin";
		quest18.target.quantity = 12;
		quest18.reward = new Reward();
		quest18.reward.itemId = "minecraft:gold_nugget";
		quest18.reward.quantity = 16;
		questList.add(quest18);
		
		DailyQuest quest19 = new DailyQuest();
		quest19.id = "57ba47322213bb1100c72ee7";
		quest19.name = "Stone Age Revival";
		quest19.description = "Gather 256 Cobblestone";
		quest19.type = "gather";
		quest19.status = "available";
		quest19.progress = 0;
		quest19.target = new TypedInteger();
		quest19.target.itemId = "minecraft:cobblestone";
		quest19.target.quantity = 256;
		quest19.reward = new Reward();
		quest19.reward.itemId = "minecraft:iron_pickaxe";
		quest19.reward.quantity = 1;
		questList.add(quest19);
		
		DailyQuest quest20 = new DailyQuest();
		quest20.id = "57ba47322213bb1100c72ee8";
		quest20.name = "Guardian of the Deep";
		quest20.description = "Kill 4 Guardians";
		quest20.type = "hunt";
		quest20.status = "available";
		quest20.progress = 0;
		quest20.target = new TypedInteger();
		quest20.target.itemId = "minecraft:guardian";
		quest20.target.quantity = 4;
		quest20.reward = new Reward();
		quest20.reward.itemId = "minecraft:prismarine_crystals";
		quest20.reward.quantity = 8;
		questList.add(quest20);
	}
}
