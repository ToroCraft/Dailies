package net.torocraft.dailies.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DailiesCapabilityProvider implements ICapabilitySerializable<INBT> {

	public static final String NAME = "dailiescapability";

	@CapabilityInject(IDailiesCapability.class)
	public static Capability<IDailiesCapability> DAILIES_CAPABILITY = null;

	private LazyOptional<IDailiesCapability> instance = LazyOptional.of(DailiesCapabilityImpl::new);

	public static void register() {
		CapabilityManager.INSTANCE.register(IDailiesCapability.class, new DailiesStorage(), DailiesCapabilityImpl::new);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		return capability == DAILIES_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}


	@Override
	public INBT serializeNBT() {
		return DAILIES_CAPABILITY.getStorage().writeNBT(DAILIES_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		DAILIES_CAPABILITY.getStorage().readNBT(DAILIES_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
	}
}