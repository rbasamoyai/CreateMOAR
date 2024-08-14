package rbasamoyai.createmoar;

import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.createmoar.network.MOARNetwork;

public class MOARCommonEvents {

    public static void onPlayerLogin(ServerPlayer player) {
        MOARNetwork.sendVersionCheck(player);
    }

}
