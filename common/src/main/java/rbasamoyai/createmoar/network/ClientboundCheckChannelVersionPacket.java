package rbasamoyai.createmoar.network;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.utils.EnvExecute;

public record ClientboundCheckChannelVersionPacket(String serverVersion) implements CommonPacket {

    public static final ResourceLocation ID = CreateMOAR.path("version_check");

	public ClientboundCheckChannelVersionPacket(FriendlyByteBuf buf) {
		this(buf.readUtf());
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(this.serverVersion);
	}

	@Override
	public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
		EnvExecute.executeOnClient(() -> () -> MOARNetworkClient.checkVersion(this));
	}

    @Override public ResourceLocation name() { return ID; }

}
