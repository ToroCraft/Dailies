package net.torocraft.dailies.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.torocraft.dailies.BaileyInventory;
import net.torocraft.dailies.DailiesContainer;

public class DailiesGuiHandler implements IGuiHandler {
	
	private static final int BAILEY_GUI_ID = 69;
	public static int getGuiID() {return BAILEY_GUI_ID;}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int entityId, int y, int z) {
		if(ID == BAILEY_GUI_ID) {
			return new DailiesContainer(player, new BaileyInventory(), world);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int entityId, int y, int z) {
		if(ID == BAILEY_GUI_ID) {
			return new DailiesGuiContainer(player, new BaileyInventory(), world);
		}
		return null;
	}
}
