package com.blackgear.meebo.common.registries;

import com.blackgear.meebo.Meebo;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Meebo.MODID);

    public static final RegistryObject<Item> MEEBO_SPAWN_EGG = ITEMS.register("meebo_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.MEEBO, 11061848, 15918921, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
}