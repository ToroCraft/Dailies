package net.torocraft.dailies.network.packets;

import java.util.Collections;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.torocraft.dailies.capabilities.DailiesCapabilityImpl;
import net.torocraft.dailies.client.ClientQuestCache;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestsPacket implements IDailiesPacket<QuestsPacket.Message> {

  public static class Message implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse("dailies:quests"));
    
    public Set<DailyQuest> quests;
    public QuestsFilter filter;
    
    public Message(QuestsFilter filter, Set<DailyQuest> quests) {
      this.quests = quests;
      this.filter = filter;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }
  }

  @Override
  public Message decode(FriendlyByteBuf buf) {
    QuestsFilter filter = buf.readEnum(QuestsFilter.class);
    CompoundTag c = buf.readNbt();
    return new Message(filter, DailiesCapabilityImpl.readQuestList(c, "q"));
  }

  @Override
  public void encode(Message message, FriendlyByteBuf buf) {
    Set<DailyQuest> quests = message.quests;
    if (quests == null) {
      quests = Collections.emptySet();
    }
    CompoundTag c = new CompoundTag();
    DailiesCapabilityImpl.writeQuestsList(c, "q", quests);
    buf.writeEnum(message.filter);
    buf.writeNbt(c);
  }

  @Override
  public void handle(Message message, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
      if (!ctx.flow().isClientbound()) {
        return;
      }
      
      // Only run on client side - avoid direct client imports to prevent server issues
      if (ctx.flow().isClientbound()) {
        handleClientSide(message);
      }
    });
  }
  
  // Separate method for client-side handling to avoid loading client classes on server
  private void handleClientSide(Message message) {
    try {
      // Use ClientQuestCache instead of static fields
      ClientQuestCache cache = ClientQuestCache.getInstance();
      if (QuestsFilter.ACCEPTED.equals(message.filter)) {
        cache.updateAcceptedQuests(message.quests);
      } else {
        cache.updateAvailableQuests(message.quests);
      }
    } catch (Exception e) {
      // Ignore errors on server side
    }
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }


}
