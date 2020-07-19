package net.torocraft.dailies.messages;

/*import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.ClientProxy;
import net.torocraft.dailies.quests.DailyQuest;

public class QuestProgressToClient implements IMessage {

	private DailyQuest quest;
	private String questJson;
	
	public QuestProgressToClient() {
		
	}
	
	public QuestProgressToClient(DailyQuest quest) {
		this.quest = quest;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		questJson = ByteBufUtils.readUTF8String(buf);
		deserializeQuest();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		serializeQuest();
		ByteBufUtils.writeUTF8String(buf, questJson);
	}
	
	private void serializeQuest() {
		if(quest != null) {
			questJson = new Gson().toJson(quest);
		} else {
			questJson = "";
		}
	}
	
	private void deserializeQuest() {
		this.quest = new Gson().fromJson(questJson, DailyQuest.class);
	}
	
	private DailyQuest getQuest() {
		return quest;
	}
	
	public static class Handler implements IMessageHandler<QuestProgressToClient, IMessage> {

		@Override
		public IMessage onMessage(final QuestProgressToClient message, MessageContext ctx) {
			if(ctx.side != Side.CLIENT) {
				return null;
			}
			
			Minecraft minecraft = Minecraft.getMinecraft();
			minecraft.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					processMessage(message);
				}
			});
			
			return null;
		}
		
		void processMessage(QuestProgressToClient message) {
			DailyQuest quest = message.getQuest();
			if(quest != null) {
				ClientProxy.displayQuestProgress(quest);
			}
			return;
		}	
	}
}
*/