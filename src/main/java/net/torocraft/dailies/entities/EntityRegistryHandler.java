package net.torocraft.dailies.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.torocraft.dailies.DailiesMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.torocraft.dailies.DailiesMod.MODID;

public class EntityRegistryHandler {

    private static final Logger LOGGER = LogManager.getLogger(MODID + " EntityRegistryHandler");
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DailiesMod.MODID);

    public static void init(){
        LOGGER.debug("REGISTERING ENTITIES");
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //Entities
    public static final RegistryObject<EntityType<EntityBailey>> BAILEY = ENTITY_TYPES.register("bailey", () -> EntityType.Builder.<EntityBailey>of(EntityBailey::new, net.minecraft.world.entity.MobCategory.MISC).sized(1F, 2F).build("baileyentity"));
}