
package net.torocraft.dailies.entities.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.torocraft.dailies.gui.BaileyInventoryGui;
import net.torocraft.dailies.gui.MenuRegistryHandler;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.entities.model.ModelBailey;
import net.torocraft.dailies.entities.model.ModelLayers;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "dailies", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderRegistryHandler {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(MenuRegistryHandler.DAILIES_CONTAINER.get(), BaileyInventoryGui::new);
		});
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistryHandler.BAILEY.get(), RenderBailey::new);
	}

	@SubscribeEvent
	public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelLayers.BAILEY, ModelBailey::createBodyLayer);
	}
}

