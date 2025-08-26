package net.torocraft.dailies.network.packets;

import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestCommandPacket implements IDailiesPacket<QuestCommandPacket.Message> {

  public enum QuestCommand { ACCEPT, ABANDON }

  public static class Message {
    public String questId;
    public QuestCommand command;

    public Message(String questId, QuestCommand command) {
      this.questId = questId;
      this.command = command;
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
  public void handle(Message message, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if(player == null) {
        return;
      }
      player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).ifPresent(d -> {
        DailyQuest quest = null;
        
        // For ACCEPT commands, look in available quests
        // For ABANDON commands, look in accepted quests
        if (QuestCommand.ACCEPT.equals(message.command)) {
          quest = d.getAvailableQuestById(message.questId);
        } else if (QuestCommand.ABANDON.equals(message.command)) {
          quest = d.getAcceptedQuestById(message.questId);
        }
        
        if (quest == null) {
          return;
        }
        
        try {
          if (QuestCommand.ABANDON.equals(message.command)) {
            d.abandonQuest(player, quest);
          } else if (QuestCommand.ACCEPT.equals(message.command)) {
            d.acceptQuest(player, quest);
          }
        } catch (DailiesException e) {
          player.sendSystemMessage(e.getMessageAsTextComponent());
        }

        PacketHandler.questsUpdate(player, QuestsFilter.ACCEPTED, d.getAcceptedQuests());
        PacketHandler.questsUpdate(player, QuestsFilter.AVAILABLE, d.getAvailableQuests());
      });

    });
    ctx.get().setPacketHandled(true);
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }

}
