package net.torocraft.dailies.network;

import java.util.Set;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.network.packets.GetQuestsPacket;
import net.torocraft.dailies.network.packets.QuestCommandPacket;
import net.torocraft.dailies.network.packets.QuestProgressPacket;
import net.torocraft.dailies.network.packets.QuestsPacket;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand;
import net.torocraft.dailies.quests.DailyQuest;

public class PacketHandler {

  public static void questsUpdate(ServerPlayerEntity player, QuestsFilter filterUsed, Set<DailyQuest> quests) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new QuestsPacket.Message(filterUsed, quests));
  }

  public static void getQuests(QuestsFilter filter) {
    INSTANCE.sendToServer(new GetQuestsPacket.Message(filter));
  }

  public static void questProgressUpdate(ServerPlayerEntity player, DailyQuest quest) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new QuestProgressPacket.Message(quest));
  }

  public static void questCommand(String questId, QuestCommand command) {
    INSTANCE.sendToServer(new QuestCommandPacket.Message(questId, command));
  }
  
  private static final String PROTOCOL_VERSION = "1";
  private static int id = 1;

  private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
      new ResourceLocation(DailiesMod.MODID, "main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals
  );

  public static void init() {
    register(GetQuestsPacket.class);
    register(QuestsPacket.class);
    register(QuestProgressPacket.class);
    register(QuestCommandPacket.class);
  }

  private static <M, P extends IDailiesPacket<M>> void register(Class<P> clazz) {
    try {
      P packet = clazz.newInstance();
      Class<M> messageClass = packet.getDataClass();
      INSTANCE.registerMessage(id++, messageClass, packet::encode,  packet::decode, packet::handle);
    }catch(Exception e){
      throw new RuntimeException("Failed to register network message " + clazz, e);
    }
  }
}
