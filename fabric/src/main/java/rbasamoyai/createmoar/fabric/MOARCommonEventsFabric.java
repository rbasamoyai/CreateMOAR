package rbasamoyai.createmoar.fabric;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import rbasamoyai.createmoar.MOARCommonEvents;

public class MOARCommonEventsFabric {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(MOARCommonEventsFabric::onPlayerJoin);
        ServerWorldEvents.LOAD.register(MOARCommonEventsFabric::onLoadServerLevel);
        ServerWorldEvents.UNLOAD.register(MOARCommonEventsFabric::onUnloadServerLevel);
        ServerTickEvents.START_WORLD_TICK.register(MOARCommonEventsFabric::onServerStartTick);
        ServerLifecycleEvents.SERVER_STOPPED.register(MOARCommonEventsFabric::onServerStopped);

        MOARCommonEvents.addServerReloadListeners(MOARCommonEventsFabric::wrapAndRegisterReloadListener);
    }

    public static void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        MOARCommonEvents.onPlayerLogin(handler.getPlayer());
    }

    public static void onLoadServerLevel(MinecraftServer server, ServerLevel level) {
        MOARCommonEvents.onLoadServersideLevel(level);
    }

    public static void onUnloadServerLevel(MinecraftServer server, ServerLevel level) {
        MOARCommonEvents.onUnloadServersideLevel(level);
    }

    public static void onServerStartTick(ServerLevel level) {
        MOARCommonEvents.onServersideLevelTickStart(level);
    }

    public static void onServerStopped(MinecraftServer server) {
        MOARCommonEvents.onServerStopped(server);
    }

    public static void wrapAndRegisterReloadListener(ResourceLocation location, PreparableReloadListener base) {
        IdentifiableResourceReloadListener listener = new IdentifiableResourceReloadListener() {
            @Override public ResourceLocation getFabricId() { return location; }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return base.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        };

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(listener);
    }

}
