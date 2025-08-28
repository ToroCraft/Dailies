package net.torocraft.dailies.network.packets;

import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.torocraft.dailies.attachments.DailiesAttachmentTypes;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.quests.DailyQuest;

public class GetQuestsPacket implements IDailiesPacket<GetQuestsPacket.Message> {

  public enum QuestsFilter { AVAILABLE, ACCEPTED };

  public static class Message implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse("dailies:get_quests"));
    
    public QuestsFilter filter;

    public Message(QuestsFilter filter) {
      this.filter = filter;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
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
  public void handle(Message message, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      ServerPlayer player = (ServerPlayer) ctx.player();
      if(player == null) {
        return;
      }
      var d = player.getData(DailiesAttachmentTypes.DAILIES_DATA);
      if (d != null) {
        Set<DailyQuest> quests;
        if (QuestsFilter.ACCEPTED.equals(message.filter)) {
          quests = d.getAcceptedQuests();
        } else {
          quests = d.getAvailableQuests();
        }
        PacketHandler.questsUpdate(player, message.filter, quests);
      }
    });
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }

}