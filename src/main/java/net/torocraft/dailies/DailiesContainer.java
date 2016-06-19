package net.torocraft.dailies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.torocraft.dailies.entities.EntityBailey;


public class DailiesContainer extends Container {

	public DailiesContainer(EntityPlayer player, EntityBailey bailey, World world) {
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		
		return true;
	}

}
