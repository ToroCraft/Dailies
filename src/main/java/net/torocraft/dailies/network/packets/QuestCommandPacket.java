package net.torocraft.dailies.network.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.attachments.DailiesAttachmentTypes;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestCommandPacket implements IDailiesPacket<QuestCommandPacket.Message> {

  public enum QuestCommand { ACCEPT, ABANDON }

  public static class Message implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse("dailies:quest_command"));
    
    public String questId;
    public QuestCommand command;

    public Message(String questId, QuestCommand command) {
      this.questId = questId;
      this.command = command;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }
  }

  @Override
  public Message decode(FriendlyByteBuf buf) {
    String questId = buf.readUtf();
    QuestCommand command = buf.readEnum(QuestCommand.class);
    return new Message(questId, command);
  }

  @Override
  public void encode(Message message, FriendlyByteBuf buf) {
    buf.writeUtf(message.questId);
    buf.writeEnum(message.command);
  }

  @Override
  public void handle(Message message, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      ServerPlayer player = (ServerPlayer) ctx.player();
      if(player == null) {
        System.out.println("QuestCommandPacket: Player is null");
        return;
      }
      
      System.out.println("QuestCommandPacket: Received " + message.command + " command for quest: " + message.questId);
      
      var d = player.getData(DailiesAttachmentTypes.DAILIES_DATA);
      if (d != null) {
        DailyQuest quest = null;
        
        // For ACCEPT commands, look in available quests
        // For ABANDON commands, look in accepted quests
        if (QuestCommand.ACCEPT.equals(message.command)) {
          quest = d.getAvailableQuestById(message.questId);
          System.out.println("QuestCommandPacket: Found available quest for accept: " + (quest != null ? quest.name : "null"));
        } else if (QuestCommand.ABANDON.equals(message.command)) {
          quest = d.getAcceptedQuestById(message.questId);
          System.out.println("QuestCommandPacket: Found accepted quest for abandon: " + (quest != null ? quest.name : "null"));
        }
        
        if (quest == null) {
          System.out.println("QuestCommandPacket: Quest not found for ID: " + message.questId);
          return;
        }
        
        try {
          if (QuestCommand.ABANDON.equals(message.command)) {
            System.out.println("QuestCommandPacket: Abandoning quest: " + quest.name);
            d.abandonQuest(player, quest);
          } else if (QuestCommand.ACCEPT.equals(message.command)) {
            System.out.println("QuestCommandPacket: Accepting quest: " + quest.name);
            d.acceptQuest(player, quest);
          }
        } catch (DailiesException e) {
          System.out.println("QuestCommandPacket: Exception during quest operation: " + e.getMessage());
          player.sendSystemMessage(e.getMessageAsTextComponent());
        }

        PacketHandler.questsUpdate(player, QuestsFilter.ACCEPTED, d.getAcceptedQuests());
        PacketHandler.questsUpdate(player, QuestsFilter.AVAILABLE, d.getAvailableQuests());
        System.out.println("QuestCommandPacket: Sent quest updates to client");
      } else {
        System.out.println("QuestCommandPacket: Player dailies data is null");
      }
    });
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }

}
