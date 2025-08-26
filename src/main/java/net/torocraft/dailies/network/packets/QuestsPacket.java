package net.torocraft.dailies.network.packets;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.capabilities.DailiesCapabilityImpl;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

/**
 * Sent from the server to a player in response to a GetQuests packet.
 * 
 * This packet will save the quests sent from the server to the 
 * static fields in the DailiesMod class for use by the client.
 */
public class QuestsPacket implements IDailiesPacket<QuestsPacket.Message> {

  public static class Message {
    public Set<DailyQuest> quests;
    public QuestsFilter filter;
    
    public Message(QuestsFilter filter, Set<DailyQuest> quests) {
      this.quests = quests;
      this.filter = filter;
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
  public void handle(Message message, Supplier<Context> ctx) {
    System.out.println("************** QuestsPacket");
    ctx.get().enqueueWork(() -> {
      if (!ctx.get().getDirection().getReceptionSide().isClient()) {
        return;
      }
      Minecraft client = Minecraft.getInstance();
      Player player = client.player;
      if (player == null) {
        return;
      }
      // TODO: Replace static quest fields with a proper client quest cache or capability
      if (QuestsFilter.ACCEPTED.equals(message.filter)) {
        DailiesMod.acceptedQuests = message.quests;
      } else {
        DailiesMod.availableQuests = message.quests;
      }
    });
    ctx.get().setPacketHandled(true);
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }


}
