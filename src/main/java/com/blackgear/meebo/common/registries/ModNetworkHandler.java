package com.blackgear.meebo.common.registries;

import com.blackgear.meebo.Meebo;
import com.blackgear.meebo.common.network.CopyMeeboC2SPacket;
import com.blackgear.meebo.common.network.SummonMeeboC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Meebo.MODID, "network"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    protected static int packetID = 0;

    public static int getPacketID() {
        return packetID++;
    }
    public static void bootstrap() {
        INSTANCE.messageBuilder(SummonMeeboC2SPacket.class, getPacketID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SummonMeeboC2SPacket::new)
                .encoder(SummonMeeboC2SPacket::toBytes)
                .consumerMainThread(SummonMeeboC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(CopyMeeboC2SPacket.class, getPacketID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CopyMeeboC2SPacket::new)
                .encoder(CopyMeeboC2SPacket::toBytes)
                .consumerMainThread(CopyMeeboC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}