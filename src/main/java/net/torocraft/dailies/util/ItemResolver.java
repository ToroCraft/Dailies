package net.torocraft.dailies.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Utility class for resolving items and entities from string identifiers.
 * Provides methods for both modern string-based lookups and legacy integer migration.
 */
public class ItemResolver {
    
    /**
     * Get Item from string identifier using modern Forge registry system
     * @param identifier The ResourceLocation string (e.g., "minecraft:coal")
     * @return The Item instance, or dirt as fallback
     */
    public static Item getItemFromIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return Items.DIRT;
        }
        
        try {
            ResourceLocation resourceLocation = new ResourceLocation(identifier);
            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
            return item != null ? item : Items.DIRT;
        } catch (Exception e) {
            System.err.println("Failed to resolve item identifier: " + identifier);
            return Items.DIRT;
        }
    }
    
    /**
     * Get EntityType from string identifier using modern Forge registry system
     * @param identifier The ResourceLocation string (e.g., "minecraft:zombie")
     * @return The EntityType instance, or null if not found
     */
    public static EntityType<?> getEntityTypeFromIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return null;
        }
        
        try {
            ResourceLocation resourceLocation = new ResourceLocation(identifier);
            return ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
        } catch (Exception e) {
            System.err.println("Failed to resolve entity identifier: " + identifier);
            return null;
        }
    }
    
    /**
     * Get display name for an item from its identifier
     * @param identifier The ResourceLocation string (e.g., "minecraft:coal")
     * @return The display name, or the identifier itself as fallback
     */
    public static String getItemDisplayName(String identifier) {
        Item item = getItemFromIdentifier(identifier);
        try {
            return item.getDescription().getString();
        } catch (Exception e) {
            return identifier;
        }
    }
    
    /**
     * Get display name for an entity from its identifier
     * @param identifier The ResourceLocation string (e.g., "minecraft:zombie")
     * @return The display name, or the identifier itself as fallback
     */
    public static String getEntityDisplayName(String identifier) {
        EntityType<?> entityType = getEntityTypeFromIdentifier(identifier);
        if (entityType != null) {
            try {
                return entityType.getDescription().getString();
            } catch (Exception e) {
                return identifier;
            }
        }
        return identifier;
    }
    
    /**
     * Validate if a string identifier represents a valid item
     * @param identifier The ResourceLocation string to check
     * @return true if the identifier represents a valid item
     */
    public static boolean isValidItemIdentifier(String identifier) {
        try {
            ResourceLocation resourceLocation = new ResourceLocation(identifier);
            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
            return item != null && item != Items.AIR;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate if a string identifier represents a valid entity type
     * @param identifier The ResourceLocation string to check
     * @return true if the identifier represents a valid entity type
     */
    public static boolean isValidEntityIdentifier(String identifier) {
        try {
            ResourceLocation resourceLocation = new ResourceLocation(identifier);
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
            return entityType != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Convert legacy integer item ID to modern ResourceLocation string
     * @deprecated Use string identifiers directly in new code
     */
    @Deprecated
    public static String convertLegacyItemIdToString(int itemId) {
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
    
    /**
     * Convert legacy integer entity ID to modern ResourceLocation string
     * @deprecated Use string identifiers directly in new code
     */
    @Deprecated
    public static String convertLegacyEntityIdToString(int entityId) {
        switch (entityId) {
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
            default: return "minecraft:pig"; // Fallback entity
        }
    }
}
