package net.torocraft.dailies.entities.render;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityBailey.BaileyVariant;
import net.torocraft.dailies.entities.model.ModelBailey;

@OnlyIn(Dist.CLIENT)
public class RenderBailey extends MobRenderer<EntityBailey, ModelBailey<EntityBailey>> {
	private static final ResourceLocation baileyTextureSavanna = new ResourceLocation(DailiesMod.MODID + ":textures/entity/baileysavanna.png");
	private static final ResourceLocation baileyTextureTaiga = new ResourceLocation(DailiesMod.MODID + ":textures/entity/baileytaiga.png");
	private static final ResourceLocation baileyTextureDesert = new ResourceLocation(DailiesMod.MODID + ":textures/entity/baileydesert.png");
	private static final ResourceLocation baileyTexturePlains = new ResourceLocation(DailiesMod.MODID + ":textures/entity/baileyplains.png");
	
	private static final Map<BaileyVariant, ResourceLocation> textures = new HashMap<BaileyVariant, ResourceLocation>();
	
	static {
		textures.put(BaileyVariant.SAVANNA, baileyTextureSavanna);
		textures.put(BaileyVariant.TAIGA, baileyTextureTaiga);
		textures.put(BaileyVariant.DESERT, baileyTextureDesert);
		textures.put(BaileyVariant.PLAINS, baileyTexturePlains);
	}

	public RenderBailey(EntityRendererManager renderManagerIn, IReloadableResourceManager resourceManagerIn) {
		super(renderManagerIn, new ModelBailey<>(0.0F), 0.5F);
	}

	public RenderBailey(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new ModelBailey<>(0.0F), 0.5F);
	}

	public ModelBailey getMainModel() {
		return (ModelBailey) super.getEntityModel();
	}

	@Override
	public void render(EntityBailey entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	public ResourceLocation getEntityTexture(EntityBailey entity) {
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

		//GlStateManager.scale(f, f, f);
	}
}