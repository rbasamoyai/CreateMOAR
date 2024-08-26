package rbasamoyai.createmoar.forge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import rbasamoyai.createmoar.MOARCommonEvents;

public class MOARCommonEventsForge {

    public static void register(IEventBus forgeBus) {
        forgeBus.addListener(MOARCommonEventsForge::onPlayerLogin);
        forgeBus.addListener(MOARCommonEventsForge::onLoadLevel);
        forgeBus.addListener(MOARCommonEventsForge::onUnloadLevel);
        forgeBus.addListener(MOARCommonEventsForge::onLevelTick);
        forgeBus.addListener(MOARCommonEventsForge::onServerStopped);
        forgeBus.addListener(MOARCommonEventsForge::onAddReloadListeners);
    }

    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent evt) {
        if (evt.getEntity() instanceof ServerPlayer player)
            MOARCommonEvents.onPlayerLogin(player);
    }

    public static void onLoadLevel(final LevelEvent.Load evt) {
        if (evt.getLevel() instanceof ServerLevel level)
            MOARCommonEvents.onLoadServersideLevel(level);
    }

    public static void onUnloadLevel(final LevelEvent.Unload evt) {
        if (evt.getLevel() instanceof ServerLevel level)
            MOARCommonEvents.onUnloadServersideLevel(level);
    }

    public static void onLevelTick(final TickEvent.LevelTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START)
            MOARCommonEvents.onServersideLevelTickStart(evt.level);
    }

    public static void onServerStopped(final ServerStoppedEvent evt) {
        MOARCommonEvents.onServerStopped(evt.getServer());
    }

    public static void onAddReloadListeners(final AddReloadListenerEvent evt) {
        MOARCommonEvents.addServerReloadListeners((loc, listener) -> evt.addListener(listener));
    }

}
