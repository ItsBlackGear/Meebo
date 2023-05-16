package com.blackgear.meebo.common.network;

import com.blackgear.meebo.MeeboHelper;
import com.blackgear.meebo.common.entity.MeeboEntity;
import com.blackgear.meebo.common.registries.ModEntityTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SummonMeeboC2SPacket {
    public SummonMeeboC2SPacket() {

    }

    public SummonMeeboC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER
            ServerPlayer player = context.getSender();
            if (player != null) {

                // check for nearby meebos
                if (MeeboHelper.getNearbyMeebos(player).isEmpty()) {
                    MeeboEntity meebo = new MeeboEntity(ModEntityTypes.MEEBO.get(), player.level);
                    meebo.setBoundedUUID(player.getUUID());
                    meebo.setRenderBound(false);
                    meebo.setNoAi(true);
                    meebo.setPos(player.position());
                    player.level.addFreshEntity(meebo);
                } else {
                    MeeboEntity meebo = MeeboHelper.getBoundedMeebo(player);
                    if (meebo != null) meebo.discard();
                }
            }
        });
        context.setPacketHandled(true);
    }
}
