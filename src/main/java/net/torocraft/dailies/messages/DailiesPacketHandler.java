package net.torocraft.dailies.messages;

import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.torocraft.dailies.quests.DailyQuest;

public class DailiesPacketHandler {
	
	public static int messageId = 12;

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("BaileysDailies", "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	
	public static Set<DailyQuest> availableQuests = null;
	public static Set<DailyQuest> acceptedQuests = null;
	
	public DailiesPacketHandler() { }
	
	public static int nextId() {
		return messageId++;
	}
	
	public static void init() {
		INSTANCE.registerMessage(nextId(), RequestAvailableQuests.class, RequestAvailableQuests::encode, RequestAvailableQuests::new, RequestAvailableQuests::handle);
		INSTANCE.registerMessage(nextId(), AvailableQuestsToClient.class, AvailableQuestsToClient::encode, AvailableQuestsToClient::new, AvailableQuestsToClient::handle);

		/*
		INSTANCE.registerMessage(RequestAcceptedQuests.Handler.class, RequestAcceptedQuests.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(AcceptQuestRequest.Handler.class, AcceptQuestRequest.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(AbandonQuestRequest.Handler.class, AbandonQuestRequest.class, nextId(), Side.SERVER);
		INSTANCE.registerMessage(AcceptedQuestsToClient.Handler.class, AcceptedQuestsToClient.class, nextId(), Side.CLIENT);
		INSTANCE.registerMessage(AchievementToClient.Handler.class, AchievementToClient.class, nextId(), Side.CLIENT);
		INSTANCE.registerMessage(QuestProgressToClient.Handler.class, QuestProgressToClient.class, nextId(), NetworkDirection.PLAY_TO_CLIENT);
		*/
	}
}
