package net.torocraft.dailies.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class DailiesStorage implements Capability.IStorage<IDailiesCapability> {

	@Override
	public CompoundNBT writeNBT(Capability<IDailiesCapability> capability, IDailiesCapability instance, Direction side) {
		return instance.writeNBT();
	}

	@Override
	public void readNBT(Capability<IDailiesCapability> capability, IDailiesCapability instance, Direction side, INBT nbt) {
		if (instance == null) {
			return;
		}

		CompoundNBT c = null;

		if (nbt != null && nbt instanceof CompoundNBT) {
			c = (CompoundNBT) nbt;
		}

		instance.readNBT(c);
	}
}