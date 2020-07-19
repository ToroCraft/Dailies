package net.torocraft.dailies.entities.render;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.torocraft.dailies.entities.EntityRegistryHandler;

public class RenderRegistryHandler {
	
	public static void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.BAILEY.get(), RenderBailey::new);
	}
}

