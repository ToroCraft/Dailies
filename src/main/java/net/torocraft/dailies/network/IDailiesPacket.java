package net.torocraft.dailies.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IDailiesPacket<M> {
  M decode(FriendlyByteBuf buf);
  void encode(M message, FriendlyByteBuf buf);
  void handle(M message, Supplier<NetworkEvent.Context> ctx);
  Class<M> getDataClass();
}