package com.blackgear.meebo.common.registries;

import com.blackgear.meebo.Meebo;
import com.blackgear.meebo.common.entity.MeeboEntity;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZER = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Meebo.MODID);

    public static final RegistryObject<EntityDataSerializer<MeeboEntity.State>> MEEBO_STATE = SERIALIZER.register("meebo_state", () -> EntityDataSerializer.simpleEnum(MeeboEntity.State.class));
}