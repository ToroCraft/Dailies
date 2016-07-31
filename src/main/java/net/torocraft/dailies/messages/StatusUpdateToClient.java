package net.torocraft.dailies.messages;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.quests.DailyQuest;

public class StatusUpdateToClient implements IMessage {
	
	private Set<DailyQuest> acceptedQuests;
	private String acceptedQuestJson;
	
	public StatusUpdateToClient() {
		
	}
	
	public StatusUpdateToClient(Set<DailyQuest > acceptedQuests) {
		this.acceptedQuests = acceptedQuests;
		serializeQuests();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		acceptedQuestJson = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, acceptedQuestJson);
	}
	
	private void serializeQuests() {
		if(acceptedQuests != null) {
			acceptedQuestJson = new Gson().toJson(acceptedQuests);
		} else {
			acceptedQuestJson = "";
		}
	}
	
	private void deserializeQuests() {
		DailyQuest[] dailyQuests = new Gson().fromJson(acceptedQuestJson, DailyQuest[].class);
		acceptedQuests = new HashSet<DailyQuest>();
		for(DailyQuest quest : dailyQuests) {
			acceptedQuests.add(quest);
		}
	}
	
	public String getQuestsJson() {
		return this.acceptedQuestJson;
	}
	
	public static class Handler implements IMessageHandler<StatusUpdateToClient, IMessage> {
		
		public Handler() {}
		
		@Override
		public IMessage onMessage(final StatusUpdateToClient message, MessageContext ctx) {
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
		
		void processMessage(WorldClient worldClient, StatusUpdateToClient message) {
			message.deserializeQuests();
			if(message.acceptedQuests != null) {
				DailiesPacketHandler.acceptedQuests = message.acceptedQuests;
			}
			return;
		}
	}
}
