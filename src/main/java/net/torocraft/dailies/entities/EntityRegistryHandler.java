package net.torocraft.dailies.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.torocraft.dailies.DailiesMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.torocraft.dailies.DailiesMod.MODID;

public class EntityRegistryHandler {

    private static final Logger LOGGER = LogManager.getLogger(MODID + " EntityRegistryHandler");
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DailiesMod.MODID);

    public static void init(IEventBus modEventBus){
        LOGGER.debug("REGISTERING ENTITIES");
        ENTITY_TYPES.register(modEventBus);
    }

    //Entities
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBailey>> BAILEY = ENTITY_TYPES.register("bailey", 
        () -> EntityType.Builder.of(EntityBailey::new, MobCategory.MISC).sized(1F, 2F).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(DailiesMod.MODID, "bailey"))));
}