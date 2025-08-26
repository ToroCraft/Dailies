package net.torocraft.dailies.quests;

import net.minecraft.nbt.CompoundTag;

public class TypedInteger {
	public int type;
	public int subType;
	public int quantity;
	public String nbt;

	public CompoundTag writeNBT() {
		CompoundTag c = new CompoundTag();
		c.putInt("type", type);
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
		type = c.getInt("type");
		subType = c.getInt("subType");
		quantity = c.getInt("quantity");
		nbt = c.getString("nbt");
	}
}
