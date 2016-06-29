package net.torocraft.dailies;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.messages.StatusRequestToServer;
import net.torocraft.dailies.messages.StatusRequestToServer.ServerMessageHandler;
import net.torocraft.dailies.messages.StatusUpdateToClient;
import net.torocraft.dailies.messages.StatusUpdateToClient.ClientMessageHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class CommonProxy {
	
	public static SimpleNetworkWrapper simpleNetworkWrapper;
	
	public static byte DAILIES_STATUS_MESSAGE_ID = 0;
	
	public void preInit(FMLPreInitializationEvent e) {
		CapabilityDailiesHandler.register();
		NetworkRegistry.INSTANCE.registerGuiHandler(DailiesMod.instance, new DailiesGuiHandler());
		
		simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("BaileysDailies");
		simpleNetworkWrapper.registerMessage(ClientMessageHandler.class, StatusUpdateToClient.class, DAILIES_STATUS_MESSAGE_ID++, Side.CLIENT);
		simpleNetworkWrapper.registerMessage(ServerMessageHandler.class, StatusRequestToServer.class, DAILIES_STATUS_MESSAGE_ID++, Side.SERVER);
	}

	public void init(FMLInitializationEvent e) {

	}

	public void postInit(FMLPostInitializationEvent e) {

	}

	public void displayQuestProgress(DailyQuest quest) {

	}
}
