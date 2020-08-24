package net.torocraft.dailies.events;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.gui.BaileyInventoryGui;

import static net.torocraft.dailies.DailiesContainer.CT_DAILIESCONTAINER;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class ForgeEvents {

    @SubscribeEvent
    public static final void onRegisterContainerTypes(final RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().register(CT_DAILIESCONTAINER);
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event)
    {
        EntityType<EntityBailey> bailey = EntityType.Builder.create(EntityBailey::new, EntityClassification.AMBIENT).build(DailiesMod.MODID + ":bailey");
        event.getRegistry().register(new SpawnEggItem(bailey, 0x39ff14, 0x8A2BE2, new Item.Properties().group(ItemGroup.MISC)).setRegistryName("bailey_entity_egg"));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGuis(final FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(CT_DAILIESCONTAINER, BaileyInventoryGui::new);
    }
}
