package net.torocraft.dailies.messages;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EmptyServerMessageHandler implements IMessageHandler<StatusUpdateToClient, IMessage> {

	@Override
	public IMessage onMessage(StatusUpdateToClient message, MessageContext ctx) {
		return null;
	}

}
