package rbasamoyai.createmoar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.createmoar.network.CommonPacket;
import rbasamoyai.createmoar.network.MOARNetwork;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerGamePacketListener {

    @Shadow public abstract ServerPlayer getPlayer();

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void createmoar$handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        CommonPacket cpacket = MOARNetwork.constructCommonPacket(packet.getIdentifier(), packet.getData());
        if (cpacket == null)
            return;
        this.getPlayer().getServer().execute(() -> {
            cpacket.handle(this.getPlayer().getServer(), this, this.getPlayer());
        });
        if (ci.isCancellable())
            ci.cancel();
    }

}
