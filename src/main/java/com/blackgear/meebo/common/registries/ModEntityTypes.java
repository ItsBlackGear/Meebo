package com.blackgear.meebo.common.registries;

import com.blackgear.meebo.Meebo;
import com.blackgear.meebo.common.entity.MeeboEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Meebo.MODID);

    public static final RegistryObject<EntityType<MeeboEntity>> MEEBO = ENTITIES.register("meebo", () -> EntityType.Builder.of(MeeboEntity::new, MobCategory.CREATURE).sized(0.9F, 1.5F).clientTrackingRange(8).updateInterval(2).build("meebo"));
}