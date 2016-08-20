package net.torocraft.dailies;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.entities.Entities;
import net.torocraft.dailies.messages.DailiesPacketHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent e) {
		CapabilityDailiesHandler.register();
		DailiesPacketHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(DailiesMod.instance, new DailiesGuiHandler());
	}

	public void init(FMLInitializationEvent e) {
		Entities.init();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}

	public void displayQuestProgress(DailyQuest quest) {

	}
}
