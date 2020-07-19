package net.torocraft.dailies.quests;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Reward extends TypedInteger {

	public void reward(PlayerEntity player) {
		ItemStack stack = new ItemStack(Item.getItemById(type));
		for (int i = 0; i < quantity; i++) {
			ItemEntity dropItem = new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack.copy());
			dropItem.setNoPickupDelay();
			player.world.addEntity(dropItem);
		}
	}

}