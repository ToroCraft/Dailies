package net.torocraft.dailies.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DailiesCapabilityProvider implements ICapabilityProvider, net.minecraftforge.common.capabilities.ICapabilitySerializable<CompoundTag> {

	public static final String NAME = "dailiescapability";

	public static final Capability<IDailiesCapability> DAILIES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

	private final LazyOptional<IDailiesCapability> instance = LazyOptional.of(DailiesCapabilityImpl::new);

	public static void register() {
		// Registration is now handled via CapabilityToken in 1.18.2+
		// If a Codec is needed for sync, add here. Otherwise, this is sufficient.
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == DAILIES_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")).readNBT(nbt);
	}
}