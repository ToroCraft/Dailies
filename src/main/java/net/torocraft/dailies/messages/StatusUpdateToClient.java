package net.torocraft.dailies.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class StatusUpdateToClient implements IMessage {
	
	public StatusUpdateToClient() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int i = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(1);
	}
}
