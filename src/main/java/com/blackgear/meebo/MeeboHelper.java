package com.blackgear.meebo;

import com.blackgear.meebo.common.entity.MeeboEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeeboHelper {
    public static TargetingConditions meeboTargetConditions(Player player) {
        return TargetingConditions.forNonCombat().ignoreInvisibilityTesting().range(12).ignoreInvisibilityTesting().selector(entity -> {
            if (entity instanceof MeeboEntity meebo) {
                if (meebo.getBoundedUUID() != null && meebo.getBoundedUUID().equals(player.getUUID())) {
                    return !meebo.getRenderBound();
                }
            }
            return false;
        });
    }

    public static List<MeeboEntity> getNearbyMeebos(Player player) {
        return player.level.getNearbyEntities(MeeboEntity.class, meeboTargetConditions(player), player, player.getBoundingBox().inflate(12));
    }

    public static @Nullable MeeboEntity getBoundedMeebo(Player player) {
        return getNearbyMeebos(player).stream().findFirst().orElse(null);
    }
}