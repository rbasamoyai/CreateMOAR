package rbasamoyai.createmoar.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MOARNetwork {

	private static final String VERSION = "1.0.0";

    private static final Map<ResourceLocation, Function<FriendlyByteBuf, CommonPacket>> PACKETS_BY_ID = new HashMap<>();

    public static void init() {
        PACKETS_BY_ID.put(ClientboundCheckChannelVersionPacket.ID, ClientboundCheckChannelVersionPacket::new);
    }

	public static void sendToServer(Consumer<Packet<?>> listener, CommonPacket packet) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.encode(buf);
		listener.accept(new ServerboundCustomPayloadPacket(packet.name(), buf));
	}

    public static void sendToPlayer(ServerPlayer player, CommonPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.encode(buf);
        player.connection.send(new ClientboundCustomPayloadPacket(packet.name(), buf));
    }

    @Nullable
    public static CommonPacket constructCommonPacket(ResourceLocation id, FriendlyByteBuf data) {
        if (!PACKETS_BY_ID.containsKey(id))
            return null;
        Function<FriendlyByteBuf, CommonPacket> cons = PACKETS_BY_ID.get(id);
        return cons.apply(data);
    }

	public static void sendVersionCheck(ServerPlayer player) {
		sendToPlayer(player, new ClientboundCheckChannelVersionPacket(VERSION));
	}

	public static boolean checkVersion(String version) { return VERSION.equals(version); }

}
