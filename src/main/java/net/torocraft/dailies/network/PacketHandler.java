package net.torocraft.dailies.network;

import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.network.packets.GetQuestsPacket;
import net.torocraft.dailies.network.packets.QuestCommandPacket;
import net.torocraft.dailies.network.packets.QuestProgressPacket;
import net.torocraft.dailies.network.packets.QuestsPacket;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand;
import net.torocraft.dailies.quests.DailyQuest;

public class PacketHandler {

  private static final String PROTOCOL_VERSION = "1";

  public static void questsUpdate(ServerPlayer player, QuestsFilter filterUsed, Set<DailyQuest> quests) {
    PacketDistributor.sendToPlayer(player, new QuestsPacket.Message(filterUsed, quests));
  }

  public static void getQuests(QuestsFilter filter) {
    PacketDistributor.sendToServer(new GetQuestsPacket.Message(filter));
  }

  public static void questProgressUpdate(ServerPlayer player, DailyQuest quest) {
    PacketDistributor.sendToPlayer(player, new QuestProgressPacket.Message(quest));
  }

  public static void questCommand(String questId, QuestCommand command) {
    PacketDistributor.sendToServer(new QuestCommandPacket.Message(questId, command));
  }

  public static void init() {
    // Registration is now handled via RegisterPayloadHandlersEvent in DailiesMod
    // This method is kept for compatibility but doesn't need to do anything
  }
  
    /**
   * Register all packet types with NeoForge networking system.
   * Should be called from RegisterPayloadHandlersEvent
   */
  public static void registerPayloads(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(DailiesMod.MODID).versioned(PROTOCOL_VERSION);
    
    // Register all packet types with their handlers using StreamCodec
    var getQuestsHandler = new GetQuestsPacket();
    registrar.playToServer(GetQuestsPacket.Message.TYPE, 
        StreamCodec.ofMember(getQuestsHandler::encode, getQuestsHandler::decode), 
        getQuestsHandler::handle);
    
    var questsHandler = new QuestsPacket();
    registrar.playToClient(QuestsPacket.Message.TYPE,
        StreamCodec.ofMember(questsHandler::encode, questsHandler::decode), 
        questsHandler::handle);
        
    var questProgressHandler = new QuestProgressPacket();
    registrar.playToClient(QuestProgressPacket.Message.TYPE,
        StreamCodec.ofMember(questProgressHandler::encode, questProgressHandler::decode), 
        questProgressHandler::handle);
        
    var questCommandHandler = new QuestCommandPacket();
    registrar.playToServer(QuestCommandPacket.Message.TYPE,
        StreamCodec.ofMember(questCommandHandler::encode, questCommandHandler::decode), 
        questCommandHandler::handle);
  }
}
