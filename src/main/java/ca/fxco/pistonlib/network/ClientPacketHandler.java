package ca.fxco.pistonlib.network;

import ca.fxco.api.pistonlib.pistonLogic.controller.PistonController;
import ca.fxco.pistonlib.network.packets.PistonEventS2CPayload;
import ca.fxco.pistonlib.pistonLogic.structureRunners.DecoupledStructureRunner;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;

public class ClientPacketHandler {

    public static void handle(PistonEventS2CPayload packet, PacketSender packetSender) {
        PistonController controller = packet.pistonBlock().pl$getPistonController();
        new DecoupledStructureRunner(controller.newStructureRunner(
                Minecraft.getInstance().level,
                packet.pos(),
                packet.dir(),
                1,
                packet.extend(),
                controller::newStructureResolver
        )).run();
    }

}
