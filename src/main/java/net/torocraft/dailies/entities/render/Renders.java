package net.torocraft.dailies.entities.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.torocraft.dailies.entities.EntityBailey;

public class Renders {
	
	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBailey.class, new IRenderFactory<EntityBailey>() {

			@Override
			public Render<? super EntityBailey> createRenderFor(RenderManager manager) {
				return new RenderBailey(manager);
			}
			
		});
	}
}
