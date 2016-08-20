package net.torocraft.dailies.messages;

import java.util.Set;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.BaileyInventory;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesPacketHandler {
	
	public static int messageId = 12;
	
	public static SimpleNetworkWrapper INSTANCE = null;
	
	public static Set<DailyQuest> availableQuests = null;
	public static Set<DailyQuest> acceptedQuests = null;
	
	public DailiesPacketHandler() {
	}
	
	public static int nextId() {
		return messageId++;
	}
	
	public static void init() {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("BaileysDailies");
		INSTANCE.registerMessage(RequestAvailableQuests.Handler.class, RequestAvailableQuests.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(RequestAcceptedQuests.Handler.class, RequestAcceptedQuests.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(AcceptQuestRequest.Handler.class, AcceptQuestRequest.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(AbandonQuestRequest.Handler.class, AbandonQuestRequest.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(AvailableQuestsToClient.Handler.class, AvailableQuestsToClient.class, nextId(), Side.CLIENT);
		INSTANCE.registerMessage(AcceptedQuestsToClient.Handler.class, AcceptedQuestsToClient.class, nextId(), Side.CLIENT);
	}
}
