package net.torocraft.dailies.entities.render;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.torocraft.dailies.DailiesMod;
import net.torocraft.dailies.entities.EntityBailey;
import net.torocraft.dailies.entities.EntityBailey.BaileyVariant;
import net.torocraft.dailies.entities.model.ModelBailey;
import net.torocraft.dailies.entities.model.ModelLayers;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)  
public class RenderBailey extends MobRenderer<EntityBailey, LivingEntityRenderState, ModelBailey> {
    private static final ResourceLocation baileyTextureSavanna = ResourceLocation.fromNamespaceAndPath(DailiesMod.MODID, "textures/entity/baileysavanna.png");
    private static final ResourceLocation baileyTextureTaiga = ResourceLocation.fromNamespaceAndPath(DailiesMod.MODID, "textures/entity/baileytaiga.png");
    private static final ResourceLocation baileyTextureDesert = ResourceLocation.fromNamespaceAndPath(DailiesMod.MODID, "textures/entity/baileydesert.png");
    private static final ResourceLocation baileyTexturePlains = ResourceLocation.fromNamespaceAndPath(DailiesMod.MODID, "textures/entity/baileyplains.png");	private static final Map<BaileyVariant, ResourceLocation> textures = new HashMap<>();

	static {
		textures.put(BaileyVariant.SAVANNA, baileyTextureSavanna);
		textures.put(BaileyVariant.TAIGA, baileyTextureTaiga);
		textures.put(BaileyVariant.DESERT, baileyTextureDesert);
		textures.put(BaileyVariant.PLAINS, baileyTexturePlains);
	}

	public RenderBailey(EntityRendererProvider.Context context) {
		super(context, new ModelBailey(context.bakeLayer(ModelLayers.BAILEY)), 0.5F);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public void extractRenderState(EntityBailey entity, LivingEntityRenderState renderState, float partialTick) {
		super.extractRenderState(entity, renderState, partialTick);
		// Extract Bailey-specific render state if needed
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(LivingEntityRenderState renderState) {
		// For now, return default texture since we need entity access for variant
		return baileyTextureSavanna;
	}

	@Override
	protected void scale(LivingEntityRenderState renderState, PoseStack poseStack) {
		float f = 0.9375F;
		// Scale logic would need to be adapted for render state
		poseStack.scale(f, f, f);
	}
}