package rbasamoyai.createmoar.fabric;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.createmoar.MOARCommonEvents;

public class MOARCommonEventsFabric {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(MOARCommonEventsFabric::onPlayerJoin);
    }

    public static void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        MOARCommonEvents.onPlayerLogin(handler.getPlayer());
    }

}
