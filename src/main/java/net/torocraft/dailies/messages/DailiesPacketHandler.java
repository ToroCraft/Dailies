package net.torocraft.dailies.messages;

import java.util.Set;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesPacketHandler {
	
	public static int messageId = 12;
	
	public static SimpleNetworkWrapper INSTANCE = null;
	
	public static Set<DailyQuest> acceptedQuests = null;
	
	public DailiesPacketHandler() {
		
	}
	
	public static int nextId() {
		return messageId++;
	}
	
	public static void init() {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("BaileysDailies");
		INSTANCE.registerMessage(StatusRequestToServer.Handler.class, StatusRequestToServer.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(StatusUpdateToClient.Handler.class, StatusUpdateToClient.class, nextId(), Side.CLIENT);
	}
	
	public static void sendRequestToServer() {
		acceptedQuests = null;
		INSTANCE.sendToServer(new StatusRequestToServer());
	}
}
