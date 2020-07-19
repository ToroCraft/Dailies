package net.torocraft.dailies.quests;

import net.minecraft.nbt.CompoundNBT;

public class TypedInteger {
	public int type;
	public int subType;
	public int quantity;
	public String nbt;

	public CompoundNBT writeNBT() {
		CompoundNBT c = new CompoundNBT();
		c.putInt("type", type);
		c.putInt("subType", subType);
		c.putInt("quantity", quantity);
		if (nbt != null) {
			c.putString("nbt", nbt);
		}
		return c;
	}

	public void readNBT(CompoundNBT c) {
		if (c == null) {
			return;
		}
		type = c.getInt("type");
		subType = c.getInt("subType");
		quantity = c.getInt("quantity");
		nbt = c.getString("nbt");
	}
}
