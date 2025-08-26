package net.torocraft.dailies.entities.render;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityBailey.BaileyVariant;
import net.torocraft.dailies.entities.model.ModelBailey;
import net.torocraft.dailies.entities.model.ModelLayers;

@OnlyIn(Dist.CLIENT)
public class RenderBailey extends MobRenderer<EntityBailey, ModelBailey<EntityBailey>> {
	private static final ResourceLocation baileyTextureSavanna = new ResourceLocation(DailiesMod.MODID, "textures/entity/baileysavanna.png");
	private static final ResourceLocation baileyTextureTaiga = new ResourceLocation(DailiesMod.MODID, "textures/entity/baileytaiga.png");
	private static final ResourceLocation baileyTextureDesert = new ResourceLocation(DailiesMod.MODID, "textures/entity/baileydesert.png");
	private static final ResourceLocation baileyTexturePlains = new ResourceLocation(DailiesMod.MODID, "textures/entity/baileyplains.png");

	private static final Map<BaileyVariant, ResourceLocation> textures = new HashMap<>();

	static {
		textures.put(BaileyVariant.SAVANNA, baileyTextureSavanna);
		textures.put(BaileyVariant.TAIGA, baileyTextureTaiga);
		textures.put(BaileyVariant.DESERT, baileyTextureDesert);
		textures.put(BaileyVariant.PLAINS, baileyTexturePlains);
	}

	public RenderBailey(EntityRendererProvider.Context context) {
		super(context, new ModelBailey<>(context.bakeLayer(ModelLayers.BAILEY)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityBailey entity) {
		if (entity.variant == null) {
			return baileyTextureSavanna;
		}
		return textures.getOrDefault(entity.variant, baileyTextureSavanna);
	}

	@Override
	protected void scale(EntityBailey entity, PoseStack poseStack, float partialTickTime) {
		float f = 0.9375F;
		if (entity.getAge() < 0) {
			f = (float) ((double) f * 0.5D);
		}
		poseStack.scale(f, f, f);
	}
}