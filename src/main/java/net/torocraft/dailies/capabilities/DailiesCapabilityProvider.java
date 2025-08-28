package net.torocraft.dailies.capabilities;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DailiesCapabilityProvider {

	public static final String NAME = "dailiescapability";

	// DeferredRegister for attachment types
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "dailies");

	// AttachmentType replaces the old Capability system
	public static final net.neoforged.neoforge.registries.DeferredHolder<AttachmentType<?>, AttachmentType<DailiesCapabilityImpl>> DAILIES_DATA = 
		ATTACHMENT_TYPES.register("dailies_data", () -> AttachmentType.builder(DailiesCapabilityImpl::new).build());

	public static void register() {
		// Registration is handled by DeferredRegister
	}

	public static DailiesCapabilityImpl getDailiesData(net.minecraft.world.entity.Entity entity) {
		return entity.getData(DAILIES_DATA.get());
	}

	public static boolean hasDailiesData(net.minecraft.world.entity.Entity entity) {
		return entity.hasData(DAILIES_DATA.get());
	}

	public static void setDailiesData(net.minecraft.world.entity.Entity entity, DailiesCapabilityImpl data) {
		entity.setData(DAILIES_DATA.get(), data);
	}
}