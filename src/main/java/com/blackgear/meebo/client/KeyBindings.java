package com.blackgear.meebo.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeyBindings {
    public static final List<KeyMapping> BINDINGS = new ArrayList<>();

    public static final KeyMapping MEEBO_MORPH = new KeyMapping("key.meebo.morph", GLFW.GLFW_KEY_Y, "key.meebo");
    public static final KeyMapping MEEBO_ANGRY = interaction("key.meebo.angry", GLFW.GLFW_KEY_U, "key.meebo.interactions");
    public static final KeyMapping MEEBO_BIGEYE = interaction("key.meebo.bigeye", GLFW.GLFW_KEY_I, "key.meebo.interactions");
    public static final KeyMapping MEEBO_SURPRISED = interaction("key.meebo.surprise", GLFW.GLFW_KEY_O, "key.meebo.interactions");
    public static final KeyMapping MEEBO_SAD = interaction("key.meebo.sad", GLFW.GLFW_KEY_K, "key.meebo.interactions");

    public static KeyMapping interaction(String key, int id, String category) {
        KeyMapping mapping = new KeyMapping(key, id, category);
        BINDINGS.add(mapping);
        return mapping;
    }
}