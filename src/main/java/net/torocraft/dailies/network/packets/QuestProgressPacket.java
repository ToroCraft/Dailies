package net.torocraft.dailies.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestProgressPacket implements IDailiesPacket<QuestProgressPacket.Message> {

  public static class Message implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse("dailies:quest_progress"));
    
    private DailyQuest quest;

    public Message(DailyQuest quest) {
      this.quest = quest;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
    }
  }

  @Override
  public Message decode(FriendlyByteBuf buf) {
    DailyQuest quest = new DailyQuest();
    quest.readNBT(buf.readNbt());
    return new Message(quest);
  }

  @Override
  public void encode(Message message, FriendlyByteBuf buf) {
    buf.writeNbt(message.quest.writeNBT());
  }

  @Override
  public void handle(Message message, IPayloadContext ctx) {
    System.out.println("************** QuestProgressPacket");
    ctx.enqueueWork(() -> {
      System.out.println("** Quest Progress Update: ");
      System.out.println(message.quest);
    });
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }


}
