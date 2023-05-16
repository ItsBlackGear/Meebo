package com.blackgear.meebo.common.network;

import com.blackgear.meebo.MeeboHelper;
import com.blackgear.meebo.common.entity.MeeboEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CopyMeeboC2SPacket {
    private final int index;

    public CopyMeeboC2SPacket(int index) {
        this.index = index;
    }

    public CopyMeeboC2SPacket(FriendlyByteBuf buf) {
        this.index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER
            ServerPlayer player = context.getSender();
            if (player != null) {

                // check for nearby meebos
                if (!MeeboHelper.getNearbyMeebos(player).isEmpty()) {
                    MeeboEntity.State state = MeeboEntity.State.values()[this.index];
                    MeeboEntity meebo = MeeboHelper.getBoundedMeebo(player);
                    if (meebo != null && meebo.getState() != state) {
                        meebo.transitionTo(state);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}