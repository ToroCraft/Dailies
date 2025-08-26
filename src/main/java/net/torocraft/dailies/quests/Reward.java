package net.torocraft.dailies.quests;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Reward extends TypedInteger {

	public void reward(Player player) {
		ItemStack stack = new ItemStack(Item.byId(type));
		for (int i = 0; i < quantity; i++) {
			ItemEntity dropItem = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack.copy());
			dropItem.setNoPickUpDelay();
			player.level.addFreshEntity(dropItem);
		}
	}

}