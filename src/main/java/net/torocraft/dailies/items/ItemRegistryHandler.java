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
    
    // Bailey spawn egg - commented out for now, will implement later
    // TODO: Fix spawn egg registration for NeoForge 1.21.8
    /*
    public static final DeferredItem<SpawnEggItem> BAILEY_SPAWN_EGG = ITEMS.register("bailey_spawn_egg",
            () -> new SpawnEggItem(
                    // The entity type to spawn
                    EntityRegistryHandler.BAILEY.get(),
                    // Default brown colors for the spawn egg
                    0x8B4513, // Brown (primary color)  
                    0xDEB887, // Burlywood (secondary color)
                    // Item properties
                    new net.minecraft.world.item.Item.Properties().stacksTo(16)
            ));
    */
    
    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        
        // TODO: Re-enable when spawn egg is fixed
        // Add spawn egg setup event listener
        // modEventBus.addListener(ItemRegistryHandler::commonSetup);
    }
    
    /*
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register spawn egg with its entity type - this helps Minecraft know how to render it
            SpawnEggItem.byId(EntityRegistryHandler.BAILEY.get());
        });
    }
    */
    
        @SubscribeEvent
    public static void registerItemsToTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            // Add Bailey spawn egg to the spawn eggs creative tab
            // TODO: Uncomment when spawn egg is fixed
            // event.accept(BAILEY_SPAWN_EGG.get());
        }
    }
}
