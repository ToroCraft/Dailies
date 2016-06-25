package net.torocraft.dailies.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class StatusRequestToServer implements IMessage {

	public StatusRequestToServer() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(1);
	}

}
