package com.blackgear.meebo;

import com.blackgear.meebo.common.entity.MeeboEntity;
import com.blackgear.meebo.common.registries.ModDataSerializers;
import com.blackgear.meebo.common.registries.ModEntityTypes;
import com.blackgear.meebo.common.registries.ModItems;
import com.blackgear.meebo.common.registries.ModNetworkHandler;
import com.blackgear.meebo.common.registries.ModSoundEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Meebo.MODID)
public class Meebo {
    public static final String MODID  = "meebo";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public Meebo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModEntityTypes.ENTITIES.register(modEventBus);
        ModDataSerializers.SERIALIZER.register(modEventBus);
        ModSoundEvents.SOUNDS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerAttributes);
        
        MinecraftForge.EVENT_BUS.addListener(this::spawnMeebo);
        MinecraftForge.EVENT_BUS.addListener(this::onModifyingSize);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworkHandler::bootstrap);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.MEEBO.get(), MeeboEntity.createAttributes().build());
    }

    private void spawnMeebo(TickEvent.ServerTickEvent event) {
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (!MeeboHelper.getNearbyMeebos(player).isEmpty()) {
                MeeboHelper.getNearbyMeebos(player).forEach(meebo -> {
                    if (meebo.getBoundedUUID() != null && meebo.level.getPlayerByUUID(meebo.getBoundedUUID()) == null) {
                        meebo.discard();
                    }
                });

                MeeboEntity meebo = MeeboHelper.getBoundedMeebo(player);
                if (meebo != null) {
                    meebo.applyToMorph(player);
                    meebo.tick();
                    meebo.applyPostTick(player);
                    player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(meebo.getMaxHealth());
                    player.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(meebo.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                    meebo.setHealth(player.getHealth());

                    if (meebo.isDeadOrDying()) {
                        player.kill();
                        restoreDefaults(player);
                    }
                } else {
                    restoreDefaults(player);
                }
            } else {
                restoreDefaults(player);
            }
        });
    }

    private static void restoreDefaults(ServerPlayer player) {
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
        player.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    public void onModifyingSize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {
            MeeboEntity meebo = MeeboHelper.getBoundedMeebo(player);

            if (meebo != null) {
                meebo.setPose(event.getPose());

                EntityDimensions dimensions = EntityDimensions.scalable(0.9F, 1.5F);

                if (event.getPose() == Pose.CROUCHING) {
                    dimensions = dimensions.scale(1, 0.85F);
                }

                event.setNewSize(dimensions, false);
                event.setNewEyeHeight(dimensions.height * 0.85F);
            }
        }
    }
}