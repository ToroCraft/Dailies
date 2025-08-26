package net.torocraft.dailies.capabilities;

import net.minecraft.nbt.CompoundTag;

// In 1.19.2+, capabilities no longer use IStorage
// This class is no longer needed - remove it or make it a simple helper
public class DailiesStorage {
    
    public static CompoundTag writeNBT(IDailiesCapability capability) {
        return capability.writeNBT();
    }
    
    public static void readNBT(IDailiesCapability capability, CompoundTag nbt) {
        capability.readNBT(nbt);
    }
}