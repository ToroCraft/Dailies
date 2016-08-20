package net.torocraft.dailies.entities.render;


import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.model.ModelBailey;

@SideOnly(Side.CLIENT)
public class RenderBailey extends RenderLiving<EntityBailey> {
	private static final ResourceLocation librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");

	public RenderBailey(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBailey(0.0F), 0.5F);
		this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
	}

	public ModelVillager getMainModel() {
		return (ModelVillager) super.getMainModel();
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(EntityBailey entity) {
		// TODO Auto-generated method stub
		return librarianVillagerTextures;
	}


	/**
	 * Allows the render to do any OpenGL state modifications necessary before
	 * the model is rendered. Args: entityLiving, partialTickTime
	 */
	protected void preRenderCallback(RenderBailey entitylivingbaseIn, float partialTickTime) {
		float f = 0.9375F;

		this.shadowSize = 0.5F;

		GlStateManager.scale(f, f, f);
	}
}