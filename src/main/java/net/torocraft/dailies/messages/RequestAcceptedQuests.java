package net.torocraft.dailies.messages;

import java.util.HashSet;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;

public class RequestAcceptedQuests implements IMessage {
	
	public RequestAcceptedQuests() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(1);
	}
	
	public static class Handler implements IMessageHandler<RequestAcceptedQuests, IMessage> {
		
		public Handler() {}
		
		@Override
		public IMessage onMessage(final RequestAcceptedQuests message, MessageContext ctx) {
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
		
		void processMessage(RequestAcceptedQuests message, EntityPlayerMP player) {
			IDailiesCapability cap = player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
		
			if(!isSet(cap.getAcceptedQuests())) {
				DailiesPacketHandler.INSTANCE.sendTo(new AcceptedQuestsToClient(new HashSet<DailyQuest>()), player);
			} else {
				DailiesPacketHandler.INSTANCE.sendTo(new AcceptedQuestsToClient(cap.getAcceptedQuests()), player);
			}
		
			return;
		}
		
		private boolean isSet(Set<DailyQuest> set) {
			return set != null && set.size() > 0;
		}
	}
}
