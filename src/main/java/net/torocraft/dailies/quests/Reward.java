package net.torocraft.dailies.quests;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Reward extends TypedInteger {

	public void reward(Player player) {
		// Convert integer ID to ResourceLocation and get item from registry
		Item rewardItem = getItemFromType(type);
		ItemStack stack = new ItemStack(rewardItem);
		for (int i = 0; i < quantity; i++) {
			ItemEntity dropItem = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack.copy());
			dropItem.setNoPickUpDelay();
			player.level.addFreshEntity(dropItem);
		}
	}

	/**
	 * Convert legacy integer item ID to modern Item from registry
	 * This maintains compatibility with existing save data while using modern APIs
	 */
	private Item getItemFromType(int itemId) {
		// For now, map common item IDs to their modern equivalents
		// TODO: Consider migrating save data to use ResourceLocation strings instead of integers
		switch (itemId) {
			case 1: return Items.STONE;
			case 2: return Items.GRASS_BLOCK;
			case 3: return Items.DIRT;
			case 4: return Items.COBBLESTONE;
			case 5: return Items.OAK_PLANKS;
			case 264: return Items.DIAMOND;
			case 265: return Items.IRON_INGOT;
			case 266: return Items.GOLD_INGOT;
			case 287: return Items.STRING;
			case 318: return Items.FLINT;
			case 348: return Items.GLOWSTONE_DUST;
			case 353: return Items.SUGAR;
			case 354: return Items.CAKE;
			case 367: return Items.ROTTEN_FLESH;
			case 375: return Items.SPIDER_EYE;
			case 376: return Items.FERMENTED_SPIDER_EYE;
			default: 
				// Fallback to dirt if unknown ID
				return Items.DIRT;
		}
	}

}