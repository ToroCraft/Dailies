package net.torocraft.dailies.messages;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.advancements.Advancement;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.quests.DailyQuest;

public class AchievementToClient implements IMessage {

	private DailyQuest completedQuest;
	private String completedQuestJson;
	
	public AchievementToClient() {
		
	}
	
	public AchievementToClient(DailyQuest quest) {
		this.completedQuest = quest;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		completedQuestJson = ByteBufUtils.readUTF8String(buf);
		deserializeQuest();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		serializeQuest();
		ByteBufUtils.writeUTF8String(buf, completedQuestJson);
	}
	
	private void serializeQuest() {
		if(completedQuest != null) {
			completedQuestJson = new Gson().toJson(completedQuest);
		} else {
			completedQuestJson = "";
		}
	}
	
	private void deserializeQuest() {
		this.completedQuest = new Gson().fromJson(completedQuestJson, DailyQuest.class);
	}
	
	private DailyQuest getCompletedQuest() {
		return completedQuest;
	}
	
	public static class Handler implements IMessageHandler<AchievementToClient, IMessage> {

		@Override
		public IMessage onMessage(final AchievementToClient message, MessageContext ctx) {
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
		
		void processMessage(AchievementToClient message) {
			DailyQuest quest = message.getCompletedQuest();
			if(quest != null) {
				//Achievement achievement = new Achievement(quest.getDisplayName(), "dailyquestcompleted", 0, 0,
						//Item.getItemById(quest.target.type), (Achievement) null);
				//Minecraft.getMinecraft().guiAchievement.displayAchievement(achievement);
			}
			return;
		}	
	}
}
