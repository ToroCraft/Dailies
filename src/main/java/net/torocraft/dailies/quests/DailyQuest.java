package net.torocraft.dailies.quests;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
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

	private void syncProgress(final PlayerEntity player, final String questId, final int progress) {
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

	public void dropNewStack(PlayerEntity player, ItemEntity item, int amount) {
		ItemStack stack = item.getItem().copy();
		stack.setCount(amount);
		ItemEntity dropItem = new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
		dropItem.setNoPickupDelay();
		player.world.addEntity(dropItem);
	}

	public boolean hunt(PlayerEntity player, LivingEntity mob) {
		if (!isHuntQuest() || mob == null) {
			return false;
		}

		int mobId = mob.getEntityId();

		if (mobId != target.type) {
			return false;
		}

		progress++;
		syncProgress(player, id, progress);

		return true;
	}

	public void reward(PlayerEntity player) {
		if (reward != null) {
			reward.reward(player);
		}
	}

	public CompoundNBT writeNBT() {
		CompoundNBT c = new CompoundNBT();
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

	public void readNBT(CompoundNBT c) {
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

	private CompoundNBT cast(INBT c) {
		if (c == null) {
			return null;
		}
		return (CompoundNBT) c;
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
