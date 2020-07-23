package net.torocraft.dailies.network.packets;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
  public Message decode(PacketBuffer buf) {
    String questId = buf.readString();
    QuestCommand command = buf.readEnumValue(QuestCommand.class);
    return new Message(questId, command);
  }

  @Override
  public void encode(Message message, PacketBuffer buf) {
    buf.writeString(message.questId);
    buf.writeEnumValue(message.command);
  }

  @Override
  public void handle(Message message, Supplier<Context> ctx) {
    System.out.println("************** QuestCommandPacket");
    ctx.get().enqueueWork(() -> {

      ServerPlayerEntity player = ctx.get().getSender();
      if(player == null) {
        return;
      }
      
      player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).ifPresent(d -> {
        DailyQuest quest = d.getAcceptedQuestById(message.questId);

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
          player.sendMessage(e.getMessageAsTextComponent(), Util.field_240973_b_);
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
