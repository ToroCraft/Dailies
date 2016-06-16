package net.torocraft.dailies;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {
		CapabilityDailiesHandler.register();
	}

	public void init(FMLInitializationEvent e) {

	}

	public void postInit(FMLPostInitializationEvent e) {

	}

	public void displayQuestProgress(DailyQuest quest) {

	}
}
