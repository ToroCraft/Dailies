package net.torocraft.dailies.gui;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.DailiesContainer;

public class MenuRegistryHandler {
    
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DailiesMod.MODID);
    
    public static final RegistryObject<MenuType<DailiesContainer>> DAILIES_CONTAINER = MENU_TYPES.register("dailies_container",
            () -> new MenuType<>(DailiesContainer::new));
    
    public static final RegistryObject<MenuType<BaileyInventoryContainer>> BAILEY_CONTAINER = MENU_TYPES.register("bailey_container",
            () -> new MenuType<>(BaileyInventoryContainer::new));
    
    public static void init(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
