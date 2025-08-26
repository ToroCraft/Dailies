package net.torocraft.dailies.network.packets;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import net.torocraft.dailies.network.IDailiesPacket;
import net.torocraft.dailies.quests.DailyQuest;

/**
 * Sent from the server to a player to update quest progress.
 */
public class QuestProgressPacket implements IDailiesPacket<QuestProgressPacket.Message> {

  public static class Message {
    private DailyQuest quest;

    public Message(DailyQuest quest) {
      this.quest = quest;
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
  public void handle(Message message, Supplier<Context> ctx) {
    System.out.println("************** QuestProgressPacket");
    ctx.get().enqueueWork(() -> {
      //ClientProxy.displayQuestProgress(quest);
      System.out.println("** Quest Progress Update: ");
      System.out.println(message.quest);
    });
    ctx.get().setPacketHandled(true);
  }

  @Override
  public Class<Message> getDataClass() {
    return Message.class;
  }


}
