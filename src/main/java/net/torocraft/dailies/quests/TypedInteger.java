package net.torocraft.dailies.quests;

import net.minecraft.nbt.CompoundTag;

public class TypedInteger {
	// New string-based identifier (preferred)
	public String itemId;
	// Legacy integer type (for backward compatibility)
	public int type;
	public int subType;
	public int quantity;
	public String nbt;

	public CompoundTag writeNBT() {
		CompoundTag c = new CompoundTag();
		// Save string identifier if available, otherwise fall back to integer
		if (itemId != null && !itemId.isEmpty()) {
			c.putString("itemId", itemId);
		} else {
			c.putInt("type", type);
		}
		c.putInt("subType", subType);
		c.putInt("quantity", quantity);
		if (nbt != null) {
			c.putString("nbt", nbt);
		}
		return c;
	}

	public void readNBT(CompoundTag c) {
		if (c == null) {
			return;
		}
		// Read string identifier first, fall back to integer for backward compatibility
		if (c.contains("itemId")) {
			itemId = c.getString("itemId");
			// Convert legacy integer to string if needed for migration
			if (c.contains("type") && (itemId == null || itemId.isEmpty())) {
				type = c.getInt("type");
				itemId = convertLegacyIdToString(type);
			}
		} else {
			type = c.getInt("type");
			itemId = convertLegacyIdToString(type);
		}
		subType = c.getInt("subType");
		quantity = c.getInt("quantity");
		nbt = c.getString("nbt");
	}
	
	/**
	 * Convert legacy integer item ID to modern ResourceLocation string
	 */
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
			// Entity IDs for hunt quests
			case 50: return "minecraft:creeper";
			case 51: return "minecraft:skeleton";
			case 52: return "minecraft:spider";
			case 54: return "minecraft:zombie";
			case 55: return "minecraft:slime";
			case 56: return "minecraft:ghast";
			case 57: return "minecraft:zombified_piglin";
			case 58: return "minecraft:enderman";
			case 59: return "minecraft:cave_spider";
			case 60: return "minecraft:silverfish";
			case 61: return "minecraft:blaze";
			case 62: return "minecraft:magma_cube";
			case 63: return "minecraft:ender_dragon";
			case 64: return "minecraft:wither";
			case 65: return "minecraft:bat";
			case 66: return "minecraft:witch";
			case 67: return "minecraft:endermite";
			case 68: return "minecraft:guardian";
			case 90: return "minecraft:pig";
			case 91: return "minecraft:sheep";
			case 92: return "minecraft:cow";
			case 93: return "minecraft:chicken";
			case 94: return "minecraft:squid";
			case 95: return "minecraft:wolf";
			case 96: return "minecraft:mooshroom";
			case 97: return "minecraft:snow_golem";
			case 98: return "minecraft:ocelot";
			case 99: return "minecraft:iron_golem";
			case 100: return "minecraft:horse";
			case 101: return "minecraft:rabbit";
			case 120: return "minecraft:villager";
			default: return "minecraft:dirt"; // Fallback
		}
	}
	
	/**
	 * Get the item identifier as a string, using the new itemId field or converting from legacy type
	 */
	public String getItemIdentifier() {
		if (itemId != null && !itemId.isEmpty()) {
			return itemId;
		}
		return convertLegacyIdToString(type);
	}
}
