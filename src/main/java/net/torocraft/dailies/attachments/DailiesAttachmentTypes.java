package net.torocraft.dailies.attachments;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.capabilities.DailiesCapabilityImpl;
import net.torocraft.dailies.capabilities.IDailiesCapability;

import java.util.function.Supplier;

public class DailiesAttachmentTypes {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DailiesMod.MODID);
    
    public static final Supplier<AttachmentType<DailiesCapabilityImpl>> DAILIES_DATA = ATTACHMENT_TYPES.register(
        "dailies_data", () -> AttachmentType.builder(() -> new DailiesCapabilityImpl()).build()
    );
    
    public static IDailiesCapability getDailiesData(net.minecraft.world.entity.Entity entity) {
        return entity.getData(DAILIES_DATA.get());
    }
    
    public static boolean hasDailiesData(net.minecraft.world.entity.Entity entity) {
        return entity.hasData(DAILIES_DATA.get());
    }
    
    public static void setDailiesData(net.minecraft.world.entity.Entity entity, DailiesCapabilityImpl data) {
        entity.setData(DAILIES_DATA.get(), data);
    }
}
