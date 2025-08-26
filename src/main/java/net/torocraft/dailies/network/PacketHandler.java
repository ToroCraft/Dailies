package net.torocraft.dailies.network;

import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.network.packets.GetQuestsPacket;
import net.torocraft.dailies.network.packets.QuestCommandPacket;
import net.torocraft.dailies.network.packets.QuestProgressPacket;
import net.torocraft.dailies.network.packets.QuestsPacket;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.network.packets.QuestCommandPacket.QuestCommand;
import net.torocraft.dailies.quests.DailyQuest;

public class PacketHandler {

  private static final String PROTOCOL_VERSION = "1";
  private static int id = 1;

  private static SimpleChannel INSTANCE;

  public static void questsUpdate(ServerPlayer player, QuestsFilter filterUsed, Set<DailyQuest> quests) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new QuestsPacket.Message(filterUsed, quests));
  }

  public static void getQuests(QuestsFilter filter) {
    INSTANCE.sendToServer(new GetQuestsPacket.Message(filter));
  }

  public static void questProgressUpdate(ServerPlayer player, DailyQuest quest) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new QuestProgressPacket.Message(quest));
  }

  public static void questCommand(String questId, QuestCommand command) {
    INSTANCE.sendToServer(new QuestCommandPacket.Message(questId, command));
  }

  public static void init() {
    // Initialize the network channel first
    INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(DailiesMod.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    
    // Then register all packets
    register(GetQuestsPacket.class);
    register(QuestsPacket.class);
    register(QuestProgressPacket.class);
    register(QuestCommandPacket.class);
  }

  @SuppressWarnings("unchecked")
  private static <P extends IDailiesPacket> void register(Class<P> clazz) {
    try {
      P packet = clazz.getDeclaredConstructor().newInstance();
      INSTANCE.registerMessage(id++, packet.getDataClass(), packet::encode, packet::decode, packet::handle);
    } catch (Exception e) {
      throw new RuntimeException("Failed to register packet: " + clazz.getSimpleName(), e);
    }
  }
}
