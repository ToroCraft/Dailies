package net.torocraft.dailies.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ClientMessageHandler implements IMessageHandler<StatusUpdateToClient, IMessage> {

	@Override
	public IMessage onMessage(final StatusUpdateToClient message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT) {
			return null;
		}
		
		Minecraft minecraft = Minecraft.getMinecraft();
		final WorldClient worldClient = minecraft.theWorld;
		minecraft.addScheduledTask(new Runnable() {
			public void run() {
				processMessage(worldClient, message);
			}
		});
		
		return null;
	}
	
	void processMessage(WorldClient worldClient, StatusUpdateToClient message) {
		System.out.println("MADE IT HERE");
	}

}
