package rbasamoyai.createmoar;

import java.util.function.BiConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.Level;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElementsHandler;
import rbasamoyai.createmoar.network.MOARNetwork;

public class MOARCommonEvents {

    public static void onPlayerLogin(ServerPlayer player) {
        MOARNetwork.sendVersionCheck(player);
    }

    public static void onLoadServersideLevel(ServerLevel level) {
        CreateMOAR.REACTOR_MANAGER.levelLoaded(level);
    }

    public static void onUnloadServersideLevel(ServerLevel level) {
        CreateMOAR.REACTOR_MANAGER.levelUnloaded(level);
    }

    public static void onServersideLevelTickStart(Level level) {
        if (!level.isClientSide)
            CreateMOAR.REACTOR_MANAGER.tickLevelPre(level);
    }

    public static void onServerStopped(MinecraftServer server) {
        CreateMOAR.REACTOR_MANAGER.cleanUp();
    }

    public static void addServerReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> cons) {
        cons.accept(CreateMOAR.path("nuclear_elements_listener"), NuclearElementsHandler.ReloadListener.INSTANCE);
    }

}
