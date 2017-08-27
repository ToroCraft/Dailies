package net.torocraft.dailies.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;

public class AbandonQuestRequest implements IMessage {

	private String questId;
	
	public AbandonQuestRequest() {
		
	}
	
	public AbandonQuestRequest(String questId) {
		this.questId = questId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		questId = ByteBufUtils.readUTF8String(buf);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, questId);
	}
	
	public static class Handler implements IMessageHandler<AbandonQuestRequest, IMessage> {

		@Override
		public IMessage onMessage(final AbandonQuestRequest message, MessageContext ctx) {
			if(ctx.side != Side.SERVER) {
				return null;
			}
			
			final EntityPlayerMP player = ctx.getServerHandler().player;
			
			if(player == null) {
				return null;
			}
			
			final WorldServer worldServer = player.getServerWorld();
			
			worldServer.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					processMessage(message, player);
				}
			});
			
			return null;
		}
		
		void processMessage(AbandonQuestRequest message, EntityPlayerMP player) {
			IDailiesCapability cap = player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
			DailyQuest quest = cap.getAcceptedQuestById(message.questId);
			
			if(quest == null) {
				return;
			}
			
			cap.abandonQuest(player, quest);
			DailiesPacketHandler.INSTANCE.sendTo(new AvailableQuestsToClient(cap.getAvailableQuests()), player);
			DailiesPacketHandler.INSTANCE.sendTo(new AcceptedQuestsToClient(cap.getAcceptedQuests()), player);
			
			return;
		}
	}
}
