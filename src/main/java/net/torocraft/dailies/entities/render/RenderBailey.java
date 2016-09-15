package net.torocraft.dailies.entities.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityBailey.BaileyVariant;
import net.torocraft.dailies.entities.model.ModelBailey;

@SideOnly(Side.CLIENT)
public class RenderBailey extends RenderLiving<EntityBailey> {
	private static final ResourceLocation baileyTextureSavanna = new ResourceLocation("dailiesmod:textures/entity/baileySavanna.png");
	private static final ResourceLocation baileyTextureTaiga = new ResourceLocation("dailiesmod:textures/entity/baileyTaiga.png");
	private static final ResourceLocation baileyTextureDesert = new ResourceLocation("dailiesmod:textures/entity/baileyDesert.png");
	private static final ResourceLocation baileyTexturePlains = new ResourceLocation("dailiesmod:textures/entity/baileyPlains.png");
	
	private static final Map<BaileyVariant, ResourceLocation> textures = new HashMap<BaileyVariant, ResourceLocation>();
	
	static {
		textures.put(BaileyVariant.SAVANNA, baileyTextureSavanna);
		textures.put(BaileyVariant.TAIGA, baileyTextureTaiga);
		textures.put(BaileyVariant.DESERT, baileyTextureDesert);
		textures.put(BaileyVariant.PLAINS, baileyTexturePlains);
	}

	public RenderBailey(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBailey(0.0F), 0.5F);
	}

	public ModelBailey getMainModel() {
		return (ModelBailey) super.getMainModel();
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityBailey entity) {
		if (entity.variant == null) {
			return baileyTextureSavanna;
		}
		return textures.get(entity.variant);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before
	 * the model is rendered. Args: entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityBailey entitylivingbaseIn, float partialTickTime) {
		float f = 0.9375F;

		if (entitylivingbaseIn.getGrowingAge() < 0) {
			f = (float) ((double) f * 0.5D);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		GlStateManager.scale(f, f, f);
	}
}