package net.torocraft.dailies.network.packets;

import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class GetQuestsPacket implements IDailiesPacket<GetQuestsPacket.Message> {

  public enum QuestsFilter { AVAILABLE, ACCEPTED };

  public static class Message {
    public QuestsFilter filter;

    public Message(QuestsFilter filter) {
      this.filter = filter;
    }
  }

  @Override
  public Message decode(FriendlyByteBuf buf) {
    return new Message(buf.readEnum(QuestsFilter.class));
  }

  @Override
  public void encode(Message message, FriendlyByteBuf buf) {
    buf.writeEnum(message.filter);
  }

  @Override
  public void handle(Message message, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if(player == null) {
        return;
      }
      player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY).ifPresent(d -> {
        Set<DailyQuest> quests;
        if (QuestsFilter.ACCEPTED.equals(message.filter)) {
          quests = d.getAcceptedQuests();
        } else {
          quests = d.getAvailableQuests();
        }
        PacketHandler.questsUpdate(player, message.filter, quests);
      });
    });
    ctx.get().setPacketHandled(true);
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }

}