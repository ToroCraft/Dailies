package net.torocraft.dailies.quests;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.torocraft.dailies.DailiesException;
import net.torocraft.dailies.network.remote.ProgressUpdater;

public class DailyQuest {

	public String name;
	public String description;
	public String type;
	public String status;
	public TypedInteger target;
	public Reward reward;
	public boolean rewardFulfilled;
	public int progress;
	public String id;
	public long date;

	public transient String targetName;

	public String getStatusMessage() {
		return getDisplayName() + " (" + Math.round(100 * progress / target.quantity) + "% complete)";
	}

	public DailyQuest clone() {
		DailyQuest quest = new DailyQuest();
		quest.readNBT(writeNBT());
		return quest;
	}

	public String getDisplayName() {
		if (isSet(name) && isSet(description)) {
			if (isGatherQuest()) {
				return "⇓ " + name + ": " + description;
			} else if (isHuntQuest()) {
				return "⚔ " + name + ": " + description;
			}
		}
		if (isGatherQuest()) {
			return "⇓ Gather Quest: collect " + target.quantity + " pieces of " + targetItemName();
		} else if (isHuntQuest()) {
			return "⚔ Hunt Quest: kill " + target.quantity + " " + targetItemName() + " mobs";
		}
		return "...";
	}

	public boolean isHuntQuest() {
		return "hunt".equals(type);
	}

	public boolean isGatherQuest() {
		return "gather".equals(type);
	}

	private String targetItemName() {
		if (targetName == null) {
			if (isGatherQuest()) {
				targetName = decodeItem(target.getItemIdentifier());
			} else if (isHuntQuest()) {
				targetName = decodeMob(target.getItemIdentifier());
			}
		}
		return targetName;
	}

	private String decodeItem(String itemIdentifier) {
		// Get Item from string identifier using modern Forge registry system
		try {
			net.minecraft.resources.ResourceLocation resourceLocation = net.minecraft.resources.ResourceLocation.parse(itemIdentifier);
			net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(resourceLocation);
			if (item != null) {
				return item.getName().getString();
			}
		} catch (Exception e) {
			// Log error and fall back to identifier
			System.err.println("Failed to resolve item identifier for display name: " + itemIdentifier);
		}
		// Fallback to identifier itself if resolution fails
		return itemIdentifier;
	}

	private String decodeMob(String entityIdentifier) {
		// Get EntityType from string identifier using modern Forge registry system
		try {
			net.minecraft.resources.ResourceLocation resourceLocation = net.minecraft.resources.ResourceLocation.parse(entityIdentifier);
			net.minecraft.world.entity.EntityType<?> entityType = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getValue(resourceLocation);
			if (entityType != null) {
				return entityType.getDescription().getString();
			}
		} catch (Exception e) {
			// Log error and fall back to identifier
			System.err.println("Failed to resolve entity identifier for display name: " + entityIdentifier);
		}
		// Fallback to identifier itself if resolution fails
		return entityIdentifier;
	}
	
	/**
	 * Maps legacy integer IDs to modern EntityType for name resolution
	 */
	private EntityType<?> getEntityTypeFromId(int entityId) {
		switch (entityId) {
			case 50: return EntityType.CREEPER;
			case 51: return EntityType.SKELETON;
			case 52: return EntityType.SPIDER;
			case 54: return EntityType.ZOMBIE;
			case 55: return EntityType.SLIME;
			case 56: return EntityType.GHAST;
			case 57: return EntityType.ZOMBIFIED_PIGLIN; // Was Zombie Pigman
			case 58: return EntityType.ENDERMAN;
			case 59: return EntityType.CAVE_SPIDER;
			case 60: return EntityType.SILVERFISH;
			case 61: return EntityType.BLAZE;
			case 62: return EntityType.MAGMA_CUBE;
			case 63: return EntityType.ENDER_DRAGON;
			case 64: return EntityType.WITHER;
			case 65: return EntityType.BAT;
			case 66: return EntityType.WITCH;
			case 67: return EntityType.ENDERMITE;
			case 68: return EntityType.GUARDIAN;
			case 90: return EntityType.PIG;
			case 91: return EntityType.SHEEP;
			case 92: return EntityType.COW;
			case 93: return EntityType.CHICKEN;
			case 94: return EntityType.SQUID;
			case 95: return EntityType.WOLF;
			case 96: return EntityType.MOOSHROOM; // Was MUSHROOM_COW
			case 97: return EntityType.SNOW_GOLEM; // Was SNOWMAN
			case 98: return EntityType.OCELOT;
			case 99: return EntityType.IRON_GOLEM;
			case 100: return EntityType.HORSE;
			case 101: return EntityType.RABBIT;
			case 120: return EntityType.VILLAGER;
			default: return null;
		}
	}
	
	/**
	 * Maps legacy integer IDs to modern Item for name resolution
	 */
	private Item getItemFromId(int itemId) {
		// Map common item IDs to their modern equivalents (same as BaileyInventory)
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
			default: return Items.DIRT; // Fallback
		}
	}

	private void syncProgress(final Player player, final String questId, final int progress) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					new ProgressUpdater(player, questId, progress).update();
				} catch (DailiesException e) {
					//player.sendMessage(e.getMessageAsTextComponent());
				}
			}

		}).start();
	}

	public void dropNewStack(Player player, ItemEntity item, int amount) {
		ItemStack stack = item.getItem().copy();
		stack.setCount(amount);
		// 1.21.4+: use player.level(), getX/Y/Z, and addFreshEntity
		ItemEntity dropItem = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
		dropItem.setNoPickUpDelay(); // 1.18.2+ method
		player.level().addFreshEntity(dropItem);
	}

	public boolean hunt(Player player, LivingEntity mob) {
		if (!isHuntQuest() || mob == null) {
			return false;
		}

		// Compare entity type using modern EntityType system
		if (!isTargetMob(mob)) {
			return false;
		}

		progress++;
		syncProgress(player, id, progress);
		return true;
	}

	/**
	 * Check if the given mob matches the target for this hunt quest
	 * Uses string-based entity type comparison with modern EntityType system
	 */
	private boolean isTargetMob(LivingEntity mob) {
		try {
			// Get the entity's ResourceLocation identifier
			net.minecraft.resources.ResourceLocation mobId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
			String mobIdentifier = mobId != null ? mobId.toString() : "";
			
			// Compare with target identifier
			String targetIdentifier = target.getItemIdentifier();
			return mobIdentifier.equals(targetIdentifier);
		} catch (Exception e) {
			System.err.println("Failed to match mob for quest: " + e.getMessage());
			return false;
		}
	}

	public void reward(Player player) {
		if (reward != null) {
			reward.reward(player);
		}
	}

	public CompoundTag writeNBT() {
		CompoundTag c = new CompoundTag();
		c.putString("type", type);
		c.putInt("progress", progress);
		c.put("target", target.writeNBT());
		c.put("reward", reward.writeNBT());
		c.putBoolean("rewardFulfilled", rewardFulfilled);
		c.putLong("date", date);
		c.putString("id", id);
		c.putString("name", name);
		c.putString("description", description);
		c.putString("status", status);
		return c;
	}

	public void readNBT(CompoundTag c) {
		if (c == null) {
			return;
		}
		type = c.getString("type");
		progress = c.getInt("progress");
		date = c.getLong("date");
		id = c.getString("id");
		name = c.getString("name");
		description = c.getString("description");
		status = c.getString("status");
		rewardFulfilled = c.getBoolean("rewardFulfilled");

		target = new TypedInteger();
		reward = new Reward();

		target.readNBT(cast(c.get("target")));
		reward.readNBT(cast(c.get("reward")));
	}

	private CompoundTag cast(Tag c) {
		if (c == null) {
			return null;
		}
		return (CompoundTag) c;
	}

	public boolean isComplete() {
		return progress >= target.quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DailyQuest other = (DailyQuest) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	private boolean isSet(String s) {
		return s != null && s.length() > 0;
	}
}
