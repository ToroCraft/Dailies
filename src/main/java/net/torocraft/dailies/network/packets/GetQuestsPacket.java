package net.torocraft.dailies.network.packets;

import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
  public Message decode(PacketBuffer buf) {
    return new Message(buf.readEnumValue(QuestsFilter.class));
  }

  @Override
  public void encode(Message message, PacketBuffer buf) {
    buf.writeEnumValue(message.filter);
  }

  @Override
  public void handle(Message message, Supplier<Context> ctx) {
    System.out.println("************** GetQuestsPacket");
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      if(player == null) {
        return;
      }
      player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).ifPresent(d -> {
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
