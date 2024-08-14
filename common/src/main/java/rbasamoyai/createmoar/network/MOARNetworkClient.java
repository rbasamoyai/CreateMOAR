package rbasamoyai.createmoar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class MOARNetworkClient {

    public static void checkVersion(ClientboundCheckChannelVersionPacket pkt) {
        if (MOARNetwork.checkVersion(pkt.serverVersion()))
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null)
            mc.getConnection().onDisconnect(Component.literal("Create MOAR on the client uses a different network format than the server.")
                .append(" Please use a matching format."));
    }

    public static void sendToServer(CommonPacket pkt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null)
            MOARNetwork.sendToServer(mc.getConnection()::send, pkt);
    }

    private MOARNetworkClient() {}

}
