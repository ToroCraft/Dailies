package net.torocraft.dailies.entities.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.torocraft.dailies.entities.EntityBailey;

public class Renders {
	
	public static void registerRenders() {
		System.out.println("registering renders");
		/*
		 * RenderingRegistry.registerEntityRenderingHandler(EntityBailey.class,
		 * new IRenderFactory<EntityBailey>() {
		 * 
		 * @Override public Render<? super EntityBailey>
		 * createRenderFor(RenderManager manager) { return new
		 * RenderBailey(manager); }
		 * 
		 * });
		 */
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		registerRender(EntityBailey.class, new RenderBailey(rm));
	}

	private static void registerRender(Class<? extends Entity> e, Render<? extends Entity> renderer) {
		RenderingRegistry.registerEntityRenderingHandler(e, renderer);
	}
}
