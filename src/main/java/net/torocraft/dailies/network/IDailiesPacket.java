package net.torocraft.dailies.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IDailiesPacket<M extends CustomPacketPayload> {
  M decode(FriendlyByteBuf buf);
  void encode(M message, FriendlyByteBuf buf);
  void handle(M message, IPayloadContext ctx);
  Class<M> getDataClass();
}