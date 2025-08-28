package net.torocraft.dailies.items;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.torocraft.dailies.DailiesMod;

@EventBusSubscriber(modid = DailiesMod.MODID)
public class ItemRegistryHandler {
    
    // Use the specialized DeferredRegister.Items which provides registerItem method
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DailiesMod.MODID);
    
    // Bailey spawn egg - temporarily disabled due to NeoForge 1.21.4 registration complexity
    // TODO: Re-implement spawn egg using proper NeoForge 1.21.4 patterns
    /*
    public static final DeferredItem<SpawnEggItem> BAILEY_SPAWN_EGG = ITEMS.registerItem("bailey_spawn_egg",
            props -> new SpawnEggItem(
                    // The entity type to spawn - this lambda executes when the item is being created
                    EntityRegistryHandler.BAILEY.get(),
                    // The properties passed into the lambda
                    props
            ));
    */
    
    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
    
        @SubscribeEvent
    public static void registerItemsToTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            // TODO: Re-add spawn egg when registration is fixed
            // event.accept(BAILEY_SPAWN_EGG.get());
        }
    }
}
