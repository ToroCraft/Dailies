package net.torocraft.dailies.entities.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class ModelBailey extends EntityModel<LivingEntityRenderState> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart arms;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public ModelBailey(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.arms = root.getChild("arms");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("head",
			CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8)
				.texOffs(24, 0).addBox(-1.0F, -2.0F, -5.0F, 2, 3, 2), // nose
			PartPose.offset(0.0F, 0.0F, 0.0F)
		);
		root.addOrReplaceChild("body",
			CubeListBuilder.create()
				.texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6)
				.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 20, 6, new CubeDeformation(0.5F)),
			PartPose.offset(0.0F, 0.0F, 0.0F)
		);
		root.addOrReplaceChild("arms",
			CubeListBuilder.create()
				.texOffs(44, 22).addBox(-8.0F, -2.0F, -1.0F, 4, 10, 4)
				.texOffs(44, 22).addBox(4.0F, -2.0F, -1.0F, 4, 10, 4),
			PartPose.offset(0.0F, 2.0F, 0.0F)
		);
		root.addOrReplaceChild("right_leg",
			CubeListBuilder.create()
				.texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4),
			PartPose.offset(-2.0F, 12.0F, 0.0F)
		);
		root.addOrReplaceChild("left_leg",
			CubeListBuilder.create()
				.texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4),
			PartPose.offset(2.0F, 12.0F, 0.0F)
		);
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(LivingEntityRenderState renderState) {
		this.head.yRot = renderState.yRot * ((float)Math.PI / 180F);
		this.head.xRot = renderState.xRot * ((float)Math.PI / 180F);
		// Add more animation logic as needed
	}

	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		head.render(poseStack, buffer, packedLight, packedOverlay);
		body.render(poseStack, buffer, packedLight, packedOverlay);
		arms.render(poseStack, buffer, packedLight, packedOverlay);
		rightLeg.render(poseStack, buffer, packedLight, packedOverlay);
		leftLeg.render(poseStack, buffer, packedLight, packedOverlay);
	}
}