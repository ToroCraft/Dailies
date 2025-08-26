package net.torocraft.dailies.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.entities.EntityRegistryHandler;

public class ItemRegistryHandler {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DailiesMod.MODID);
    
    // Bailey spawn egg - creates a spawn egg that can be used in creative mode
    public static final RegistryObject<ForgeSpawnEggItem> BAILEY_SPAWN_EGG = ITEMS.register("bailey_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistryHandler.BAILEY, 0x8B4513, 0xF5DEB3, 
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    
    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
