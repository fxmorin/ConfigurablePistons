package ca.fxco.pistonlib.network.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

// PistonLib Packet
public interface PLPayload extends CustomPacketPayload {

    /**
     * Called on the render thread!
     */
    default void handleClient(PacketSender packetSender) {}

    /**
     * Called on the server thread!
     */
    default void handleServer(MinecraftServer server, ServerPlayer fromPlayer, PacketSender packetSender) {}

}
