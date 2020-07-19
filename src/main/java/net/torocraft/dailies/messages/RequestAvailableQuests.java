package net.torocraft.dailies.messages;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;

class RequestAvailableQuests {

	public RequestAvailableQuests(PacketBuffer buf) {
		buf.readInt();
	}

	void decode(PacketBuffer buf) {
		buf.readInt();
	}

	void encode(PacketBuffer buf) {
		buf.writeInt(1);
	}

	void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			// Work that needs to be threadsafe (most work)
			ServerPlayerEntity player = ctx.get().getSender(); // the client that sent this packet

			if(player == null) {
				return;
			}

			NetworkManager manager = Minecraft.getInstance().getConnection().getNetworkManager();

			player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).ifPresent((d) -> {
				if(!isSet(d.getAvailableQuests())) {
					DailiesPacketHandler.INSTANCE.sendTo(new AvailableQuestsToClient(new HashSet<DailyQuest>()), manager, NetworkDirection.PLAY_TO_CLIENT);
				} else {
					DailiesPacketHandler.INSTANCE.sendTo(new AvailableQuestsToClient(d.getAvailableQuests()), manager, NetworkDirection.PLAY_TO_CLIENT);
				}
			});
		});

		ctx.get().setPacketHandled(true);
	}

	private static boolean isSet(Set<DailyQuest> set) {
		return set != null && set.size() > 0;
	}
}
