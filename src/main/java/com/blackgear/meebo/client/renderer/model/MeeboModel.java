package com.blackgear.meebo.client.renderer.model;

import com.blackgear.meebo.common.entity.MeeboEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MeeboModel<T extends MeeboEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;

    public MeeboModel(ModelPart root) {
        this.root = root;
        ModelPart body = this.root.getChild("body");
        this.head = body.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 0).addBox(-2.0F, -2.5F, -1.5F, 4.0F, 2.0F, 3.0F).texOffs(0, 8).addBox(-1.0F, -5.5F, -1.5F, 2.0F, 3.0F, 3.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(10, 8).addBox(-1.5225F, -1.1776F, -1.5F, 1.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.7802F, -0.025F, 0.0F, 0.0F, 0.3054F));
        body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(18, 8).addBox(0.5225F, -1.1776F, -1.5F, 1.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.7802F, -0.025F, 0.0F, 0.0F, -0.3054F));
        body.addOrReplaceChild("wheel_l", CubeListBuilder.create().texOffs(24, 26).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 3.0F, 3.0F), PartPose.offset(2.5F, -1.0F, 0.0F));
        body.addOrReplaceChild("wheel_r", CubeListBuilder.create().texOffs(24, 26).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 3.0F, 3.0F), PartPose.offset(-2.5F, -1.0F, 0.0F));
        body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -4.125F, -2.0F, 5.0F, 4.0F, 4.0F).texOffs(0, 18).addBox(2.5F, -3.125F, -1.0F, 1.0F, 2.0F, 2.0F).texOffs(0, 14).addBox(-2.5F, -7.125F, 0.0F, 5.0F, 3.0F, 0.0F).texOffs(16, 14).addBox(-3.5F, -3.125F, -1.0F, 1.0F, 2.0F, 2.0F), PartPose.offset(0.0F, -4.875F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);

        this.root.xScale = 2.5F;
        this.root.yScale = 2.5F;
        this.root.zScale = 2.5F;

        this.root.y = -36F;
    }
}