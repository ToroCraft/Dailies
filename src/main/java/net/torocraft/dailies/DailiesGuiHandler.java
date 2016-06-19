package net.torocraft.dailies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class DailiesGuiHandler implements IGuiHandler {
	
	private static final int BAILEY_GUI_ID = 69;
	public static int getGuiID() {return BAILEY_GUI_ID;}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == BAILEY_GUI_ID) {
			return new DailiesContainer(player, null, world);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == BAILEY_GUI_ID) {
			return new DailiesGuiContainer(player, world, x, y, z);
		}
		return null;
	}
}
