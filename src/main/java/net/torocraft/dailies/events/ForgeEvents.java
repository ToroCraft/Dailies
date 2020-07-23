package net.torocraft.dailies.events;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.torocraft.dailies.DailiesContainer;
import net.torocraft.dailies.gui.BaileyInventoryContainer;
import net.torocraft.dailies.gui.BaileyInventoryGui;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class ForgeEvents {

    @SubscribeEvent
    public static final void onRegisterContainerTypes(final RegistryEvent.Register<ContainerType<?>> event)
    {
        //ModContent.registerContainers(event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGuis(final FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(DailiesContainer.CT_DAILIESCONTAINER, BaileyInventoryGui::new);
    }
}
