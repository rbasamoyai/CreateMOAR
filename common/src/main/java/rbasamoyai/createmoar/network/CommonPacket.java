package rbasamoyai.createmoar.network;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface CommonPacket {

    void encode(FriendlyByteBuf buf);
    void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender);
    ResourceLocation name();

}
