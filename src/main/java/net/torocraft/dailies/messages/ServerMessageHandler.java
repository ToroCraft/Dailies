package net.torocraft.dailies.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.dailies.CommonProxy;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;

public class ServerMessageHandler implements IMessageHandler<StatusRequestToServer, IMessage> {

	@Override
	public IMessage onMessage(final StatusRequestToServer message, MessageContext ctx) {
		if(ctx.side != Side.SERVER) {
			return null;
		}
		
		final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		
		if(player == null) {
			return null;
		}
		
		final WorldServer worldServer = player.getServerWorld();
		
		worldServer.addScheduledTask(new Runnable() {
			public void run() {
				processMessage(message, player);
			}
		});
		
		return null;
	}
	
	void processMessage(StatusRequestToServer message, EntityPlayerMP player) {
		StatusUpdateToClient msg = new StatusUpdateToClient();
		CommonProxy.simpleNetworkWrapper.sendTo(msg, player);
		return;
	}
}
