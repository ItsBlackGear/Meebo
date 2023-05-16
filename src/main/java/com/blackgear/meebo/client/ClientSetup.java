package com.blackgear.meebo.client;

import com.blackgear.meebo.Meebo;
import com.blackgear.meebo.client.renderer.MeeboRenderer;
import com.blackgear.meebo.client.renderer.model.MeeboModel;
import com.blackgear.meebo.common.registries.ModEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meebo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.MEEBO_MORPH);
        event.register(KeyBindings.MEEBO_ANGRY);
        event.register(KeyBindings.MEEBO_SURPRISED);
        event.register(KeyBindings.MEEBO_BIGEYE);
        event.register(KeyBindings.MEEBO_SAD);
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.MEEBO.get(), MeeboRenderer::new);
    }

    @SubscribeEvent
    public static void registerModel(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MeeboRenderer.LAYER, MeeboModel::createBodyLayer);
    }
}