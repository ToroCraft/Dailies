package net.torocraft.dailies.messages;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.quests.DailyQuest;

public class AvailableQuestsToClient implements IMessage {

	private Set<DailyQuest> availableQuests;
	private String availableQuestsJson;
	
	public AvailableQuestsToClient() {
		
	}
	
	public AvailableQuestsToClient(Set<DailyQuest > availableQuests) {
		this.availableQuests = availableQuests;
		serializeQuests();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		availableQuestsJson = ByteBufUtils.readUTF8String(buf);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, availableQuestsJson);
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
	
	
	public static class Handler implements IMessageHandler<AvailableQuestsToClient, IMessage> {
		
		public Handler() {}
		
		@Override
		public IMessage onMessage(final AvailableQuestsToClient message, MessageContext ctx) {
			if(ctx.side != Side.CLIENT) {
				return null;
			}
			
			Minecraft minecraft = Minecraft.getMinecraft();
			final WorldClient worldClient = minecraft.theWorld;
			minecraft.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					processMessage(worldClient, message);
				}
			});
			
			return null;
		}
		
		void processMessage(WorldClient worldClient, AvailableQuestsToClient message) {
			message.deserializeQuests();
			if(message.availableQuests != null) {
				DailiesPacketHandler.availableQuests = message.availableQuests;
			}
			return;
		}
	}
}
