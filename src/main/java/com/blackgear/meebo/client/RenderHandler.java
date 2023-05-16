package com.blackgear.meebo.client;

import com.blackgear.meebo.MeeboHelper;
import com.blackgear.meebo.common.entity.MeeboEntity;
import com.blackgear.meebo.common.network.CopyMeeboC2SPacket;
import com.blackgear.meebo.common.network.SummonMeeboC2SPacket;
import com.blackgear.meebo.common.registries.ModEntityTypes;
import com.blackgear.meebo.common.registries.ModNetworkHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().getConnection() != null) {
            if (KeyBindings.MEEBO_MORPH.consumeClick()) {
                ModNetworkHandler.sendToServer(new SummonMeeboC2SPacket());
            }

            KeyBindings.BINDINGS.forEach(mapping -> {
                if (KeyBindings.MEEBO_ANGRY.isDown()) {
                    ModNetworkHandler.sendToServer(new CopyMeeboC2SPacket(MeeboEntity.State.ANGRY.ordinal()));
                } else if (KeyBindings.MEEBO_SURPRISED.isDown()) {
                    ModNetworkHandler.sendToServer(new CopyMeeboC2SPacket(MeeboEntity.State.SURPRISED.ordinal()));
                } else if (KeyBindings.MEEBO_BIGEYE.isDown()) {
                    ModNetworkHandler.sendToServer(new CopyMeeboC2SPacket(MeeboEntity.State.BIGEYE.ordinal()));
                } else if (KeyBindings.MEEBO_SAD.isDown()) {
                    ModNetworkHandler.sendToServer(new CopyMeeboC2SPacket(MeeboEntity.State.SAD.ordinal()));
                } else {
                    ModNetworkHandler.sendToServer(new CopyMeeboC2SPacket(MeeboEntity.State.NORMAL.ordinal()));
                }
            });
        }
    }

    private static boolean flag = false;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderingPlayer(RenderPlayerEvent.Pre event) {
        if(flag) return;

        flag = true;

        Player player = event.getEntity();
        if (player instanceof AbstractClientPlayer) {
            if (!MeeboHelper.getNearbyMeebos(player).isEmpty()) {
                MeeboEntity meebo = MeeboHelper.getBoundedMeebo(player);

                event.setCanceled(true);
                if (meebo != null) {
                    meebo.updateRotation(player);
                    renderMorph(player, meebo, event.getPoseStack(), event.getPartialTick(), event.getMultiBufferSource());
                } else {
                    renderMorph(player, null, event.getPoseStack(), event.getPartialTick(), event.getMultiBufferSource());
                }
            }
        }

        flag = false;
    }

    private static void renderMorph(Player player, @Nullable MeeboEntity original, PoseStack matrices, float partialTick, MultiBufferSource buffer) {
        matrices.pushPose();

        if (original != null) {
            MeeboEntity meebo = ModEntityTypes.MEEBO.get().create(player.level);
            if (meebo != null) {
                meebo.updateRotation(player);
                meebo.applyToMorph(player);
                meebo.tick();
                meebo.applyPostTick(player);
                meebo.setBoundedUUID(player.getUUID());
                meebo.copyState(original);

                renderEntity(meebo, matrices, partialTick, buffer);
            }
        } else {
            renderEntity(player, matrices, partialTick, buffer);
        }

        matrices.popPose();
    }

    private static void renderEntity(Entity entity, PoseStack matrices, float partialTick, MultiBufferSource buffer) {
        EntityRenderer<? super Entity> manager = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        manager.render(entity, 0, partialTick, matrices, buffer, manager.getPackedLightCoords(entity, partialTick));
    }
}