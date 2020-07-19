package net.torocraft.dailies.entities.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBailey<T extends Entity> extends SegmentedModel<T> {
	/** The head box of the VillagerModel */
	public ModelRenderer villagerHead;
	/** The body of the VillagerModel */
	public ModelRenderer villagerBody;
	/** The arms of the VillagerModel */
	public ModelRenderer villagerArms;
	/** The right leg of the VillagerModel */
	public ModelRenderer rightVillagerLeg;
	/** The left leg of the VillagerModel */
	public ModelRenderer leftVillagerLeg;
	public ModelRenderer villagerNose;

	public ModelBailey(float scale) {
		this(scale, 64, 64);
	}

	public ModelBailey(float scale, int width, int height) {
		float rotYOffset = 0.0F;
		this.villagerHead = (new ModelRenderer(this)).setTextureSize(width, height);
		this.villagerHead.setRotationPoint(0.0F, 0.0F + rotYOffset, 0.0F);
		this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, scale);
		this.villagerHead.setTextureOffset(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, scale + 0.5F);
		this.villagerNose = (new ModelRenderer(this)).setTextureSize(width, height);
		this.villagerNose.setRotationPoint(0.0F, rotYOffset - 2.0F, 0.0F);
		this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -2.0F, -5.0F, 2, 3, 2, scale);
		this.villagerHead.addChild(this.villagerNose);
		this.villagerBody = (new ModelRenderer(this)).setTextureSize(width, height);
		this.villagerBody.setRotationPoint(0.0F, 0.0F + rotYOffset, 0.0F);
		this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, scale);
		this.villagerBody.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 20, 6, scale + 0.5F);
		this.villagerArms = (new ModelRenderer(this)).setTextureSize(width, height);
		this.villagerArms.setRotationPoint(0.0F, 0.0F + rotYOffset + 2.0F, 0.0F);
		this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -1.0F, 4, 10, 4, scale);
		this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -1.0F, 4, 10, 4, scale);
		this.rightVillagerLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(width, height);
		this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F + rotYOffset, 0.0F);
		this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
		this.leftVillagerLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(width, height);
		this.leftVillagerLeg.mirror = true;
		this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F + rotYOffset, 0.0F);
		this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
	}

	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.getParts().forEach((p_228272_8_) -> {
			p_228272_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		});
	}

	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(this.villagerHead, this.villagerBody, this.rightVillagerLeg, this.leftVillagerLeg, this.villagerArms);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		boolean flag = false;
		if (entityIn instanceof AbstractVillagerEntity) {
			flag = ((AbstractVillagerEntity)entityIn).getShakeHeadTicks() > 0;
		}

		this.villagerHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
		this.villagerHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
		if (flag) {
			this.villagerHead.rotateAngleZ = 0.3F * MathHelper.sin(0.45F * ageInTicks);
			this.villagerHead.rotateAngleX = 0.4F;
		} else {
			this.villagerHead.rotateAngleZ = 0.0F;
		}

		this.villagerArms.rotationPointY = 3.0F;
		this.villagerArms.rotationPointZ = -1.0F;
		this.villagerArms.rotateAngleX = -0.75F;
		this.rightVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
		this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
		this.rightVillagerLeg.rotateAngleY = 0.0F;
		this.leftVillagerLeg.rotateAngleY = 0.0F;
	}
}