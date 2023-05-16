package com.blackgear.meebo.client.renderer;

import com.blackgear.meebo.Meebo;
import com.blackgear.meebo.client.renderer.model.MeeboModel;
import com.blackgear.meebo.common.entity.MeeboEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MeeboRenderer extends MobRenderer<MeeboEntity, MeeboModel<MeeboEntity>> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(Meebo.MODID, "meebo"), "main");
    
    private static final Map<MeeboEntity.State, ResourceLocation> TEXTURES = new ImmutableMap.Builder<MeeboEntity.State, ResourceLocation>()
            .put(MeeboEntity.State.NORMAL, new ResourceLocation(Meebo.MODID, "textures/entity/meebo/meebo_normal.png"))
            .put(MeeboEntity.State.ANGRY, new ResourceLocation(Meebo.MODID, "textures/entity/meebo/meebo_angry.png"))
            .put(MeeboEntity.State.SURPRISED, new ResourceLocation(Meebo.MODID, "textures/entity/meebo/meebo_surprised.png"))
            .put(MeeboEntity.State.BIGEYE, new ResourceLocation(Meebo.MODID, "textures/entity/meebo/meebo_bigeye.png"))
            .put(MeeboEntity.State.SAD, new ResourceLocation(Meebo.MODID, "textures/entity/meebo/meebo_sad.png"))
            .build();

    public MeeboRenderer(EntityRendererProvider.Context context) {
        super(context, new MeeboModel<>(context.bakeLayer(LAYER)), 0.5F);
    }

    @Override
    public void render(MeeboEntity meebo, float p_115456_, float p_115457_, PoseStack matrices, MultiBufferSource buffer, int p_115460_) {
        super.render(meebo, p_115456_, p_115457_, matrices, buffer, p_115460_);
    }

    @Override
    public ResourceLocation getTextureLocation(MeeboEntity meebo) {
        return TEXTURES.get(meebo.getState());
    }
}