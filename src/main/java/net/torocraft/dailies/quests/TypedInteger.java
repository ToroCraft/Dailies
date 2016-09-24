package net.torocraft.dailies.quests;

import net.minecraft.nbt.NBTTagCompound;

public class TypedInteger {
	public int type;
	public int subType;
	public int quantity;

	public NBTTagCompound writeNBT() {
		NBTTagCompound c = new NBTTagCompound();
		c.setInteger("type", type);
		c.setInteger("subType", subType);
		c.setInteger("quantity", quantity);
		return c;
	}

	public void readNBT(NBTTagCompound c) {
		if (c == null) {
			return;
		}
		type = c.getInteger("type");
		subType = c.getInteger("subType");
		quantity = c.getInteger("quantity");
	}
}