package net.torocraft.dailies.network;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IDailiesPacket<M> {
  M decode(PacketBuffer buf);
  void encode(M message, PacketBuffer buf);
  void handle(M message, Supplier<NetworkEvent.Context> ctx);
  Class<M> getDataClass();
}