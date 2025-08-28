package net.torocraft.dailies.gui;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.DailiesContainer;

public class MenuRegistryHandler {
    
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.MENU, DailiesMod.MODID);
    
    public static final net.neoforged.neoforge.registries.DeferredHolder<MenuType<?>, MenuType<DailiesContainer>> DAILIES_CONTAINER = MENU_TYPES.register("dailies_container",
            () -> new MenuType<>(DailiesContainer::new, FeatureFlags.DEFAULT_FLAGS));
    
    public static final net.neoforged.neoforge.registries.DeferredHolder<MenuType<?>, MenuType<BaileyInventoryContainer>> BAILEY_CONTAINER = MENU_TYPES.register("bailey_container",
            () -> new MenuType<>(BaileyInventoryContainer::new, FeatureFlags.DEFAULT_FLAGS));
    
    public static void init(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
