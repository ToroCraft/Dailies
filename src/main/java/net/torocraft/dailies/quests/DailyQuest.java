package net.torocraft.dailies.quests;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
				targetName = decodeItem(target.type);
			} else if (isHuntQuest()) {
				targetName = decodeMob(target.type);
			}
		}
		return targetName;
	}

	private String decodeItem(int entityId) {
		//return I18n.translateToLocal(Item.getItemById(entityId).getUnlocalizedName() + ".name");
		return "";
	}

	private String decodeMob(int entityId) {
		String langKey = entityIdToLangKey(entityId);
		return "";
		//return I18n.translateToLocal(langKey);
	}

	private String entityIdToLangKey(int entityId) {
		/*Class<? extends Entity> entityClass = EntityList.getClassFromID(entityId);
		String entityName = EntityList.getKey(entityClass).getResourcePath();

		if (entityName == null || entityName.length() == 0) {
			entityName = "generic";
		}

		return "entity." + entityName + ".name";*/
		return "";
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
		// 1.18.2+: use player.level, getX/Y/Z, and addFreshEntity
		ItemEntity dropItem = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack);
		dropItem.setNoPickUpDelay(); // 1.18.2+ method
		player.level.addFreshEntity(dropItem);
	}

	public boolean hunt(Player player, LivingEntity mob) {
		if (!isHuntQuest() || mob == null) {
			return false;
		}

	// TODO: Replace with correct entity type comparison for 1.18.2+
	// int mobId = mob.getEntityId();
	// if (mobId != target.type) {
	//     return false;
	// }
	// For now, always return true for demonstration (replace with actual logic)
	// You should compare mob.getType() with the expected EntityType

	progress++;
	syncProgress(player, id, progress);
	return true;
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
