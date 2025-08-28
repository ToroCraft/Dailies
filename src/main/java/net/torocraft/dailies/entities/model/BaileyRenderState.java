package net.torocraft.dailies.entities.model;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.torocraft.dailies.entities.EntityBailey;

public class BaileyRenderState extends EntityRenderState {
    public EntityBailey.BaileyVariant variant;
    public float headYRot;
    public float headXRot;
    public float limbSwing;
    public float limbSwingAmount;
}
