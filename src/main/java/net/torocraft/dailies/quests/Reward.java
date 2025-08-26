package net.torocraft.dailies.quests;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Reward extends TypedInteger {

	public void reward(Player player) {
		// Get item using string-based identifier
		Item rewardItem = getItemFromIdentifier(getItemIdentifier());
		ItemStack stack = new ItemStack(rewardItem);
		for (int i = 0; i < quantity; i++) {
			ItemEntity dropItem = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack.copy());
			dropItem.setNoPickUpDelay();
			player.level.addFreshEntity(dropItem);
		}
	}

	/**
	 * Get Item from string identifier using modern Forge registry system
	 */
	private Item getItemFromIdentifier(String identifier) {
		try {
			ResourceLocation resourceLocation = new ResourceLocation(identifier);
			Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
			if (item != null) {
				return item;
			}
		} catch (Exception e) {
			// Log error and fall back to dirt
			System.err.println("Failed to resolve item identifier: " + identifier);
		}
		// Fallback to dirt if identifier resolution fails
		return Items.DIRT;
	}

	/**
	 * Convert legacy integer item ID to modern Item from registry
	 * @deprecated Use getItemFromIdentifier(String) instead
	 */
	@Deprecated
	private Item getItemFromType(int itemId) {
		// Convert to string identifier and use modern system
		String identifier = convertLegacyIdToString(itemId);
		return getItemFromIdentifier(identifier);
	}
	
	/**
	 * Convert legacy integer item ID to ResourceLocation string
	 * @deprecated Legacy support only, use string identifiers directly
	 */
	@Deprecated
	private String convertLegacyIdToString(int itemId) {
		switch (itemId) {
			case 1: return "minecraft:stone";
			case 2: return "minecraft:grass_block";
			case 3: return "minecraft:dirt";
			case 4: return "minecraft:cobblestone";
			case 5: return "minecraft:oak_planks";
			case 263: return "minecraft:coal";
			case 264: return "minecraft:diamond";
			case 265: return "minecraft:iron_ingot";
			case 266: return "minecraft:gold_ingot";
			case 287: return "minecraft:string";
			case 318: return "minecraft:flint";
			case 348: return "minecraft:glowstone_dust";
			case 353: return "minecraft:sugar";
			case 354: return "minecraft:cake";
			case 367: return "minecraft:rotten_flesh";
			case 375: return "minecraft:spider_eye";
			case 376: return "minecraft:fermented_spider_eye";
			case 384: return "minecraft:experience_bottle";
			case 388: return "minecraft:emerald";
			default: return "minecraft:dirt";
		}
	}
}