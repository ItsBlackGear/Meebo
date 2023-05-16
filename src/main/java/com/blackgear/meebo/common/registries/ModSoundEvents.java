package com.blackgear.meebo.common.registries;

import com.blackgear.meebo.Meebo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Meebo.MODID);

    public static final Supplier<SoundEvent> MEEBO_IDLE = create("entity.meebo.idle");
    public static final Supplier<SoundEvent> MEEBO_ANGRY = create("entity.meebo.angry");
    public static final Supplier<SoundEvent> MEEBO_SURPRISED = create("entity.meebo.surprised");
    public static final Supplier<SoundEvent> MEEBO_BIGEYE = create("entity.meebo.bigeye");
    public static final Supplier<SoundEvent> MEEBO_SAD = create("entity.meebo.sad");
    public static final Supplier<SoundEvent> MEEBO_HURT = create("entity.meebo.hurt");
    public static final Supplier<SoundEvent> MEEBO_DEATH = create("entity.meebo.death");

    private static Supplier<SoundEvent> create(String key) {
        return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(Meebo.MODID, key)));
    }
}