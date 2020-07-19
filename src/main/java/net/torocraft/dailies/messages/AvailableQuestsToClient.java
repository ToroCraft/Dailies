package net.torocraft.dailies.messages;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.torocraft.dailies.quests.DailyQuest;

public class AvailableQuestsToClient {

	private Set<DailyQuest> availableQuests;
	private String availableQuestsJson;

    public AvailableQuestsToClient (PacketBuffer buf) {
        availableQuestsJson = buf.readString();
    }

    public AvailableQuestsToClient(Set<DailyQuest > availableQuests) {
        this.availableQuests = availableQuests;
        serializeQuests();
    }

    void decode(PacketBuffer buf) {
        availableQuestsJson = buf.readString();
        deserializeQuests();
    }

    void encode(PacketBuffer buf) {
        buf.writeString(availableQuestsJson);
    }
	
	
	private void serializeQuests() {
		if(availableQuests != null) {
			availableQuestsJson = new Gson().toJson(availableQuests);
		} else {
			availableQuestsJson = "";
		}
	}
	
	private void deserializeQuests() {
		DailyQuest[] dailyQuests = new Gson().fromJson(availableQuestsJson, DailyQuest[].class);
		availableQuests = new HashSet<DailyQuest>();
		for(DailyQuest quest : dailyQuests) {
			availableQuests.add(quest);
		}
	}
	
	public String getQuestsJson() {
		return this.availableQuestsJson;
	}

    static void handle(AvailableQuestsToClient message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient() && ctx.get().getDirection().getOriginationSide().isServer()) {
                PlayerEntity player = Minecraft.getInstance().player;

                if (player == null) {
                    return;
                }

                if(message.availableQuests != null) {
                    DailiesPacketHandler.availableQuests = message.availableQuests;
                } else {
                    DailiesPacketHandler.availableQuests = null;
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}