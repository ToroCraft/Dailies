
package net.torocraft.dailies.entities.render;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.torocraft.dailies.gui.BaileyInventoryGui;
import net.torocraft.dailies.gui.MenuRegistryHandler;
import net.torocraft.dailies.entities.EntityRegistryHandler;
import net.torocraft.dailies.entities.model.ModelBailey;
import net.torocraft.dailies.entities.model.ModelLayers;

@EventBusSubscriber(modid = "dailies", value = Dist.CLIENT)
public class RenderRegistryHandler {
	@SubscribeEvent
	public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
		event.register(MenuRegistryHandler.DAILIES_CONTAINER.get(), BaileyInventoryGui::new);
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

