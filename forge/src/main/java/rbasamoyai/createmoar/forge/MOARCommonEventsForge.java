package rbasamoyai.createmoar.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import rbasamoyai.createmoar.MOARCommonEvents;

public class MOARCommonEventsForge {

    public static void register(IEventBus forgeBus) {
        forgeBus.addListener(MOARCommonEventsForge::onPlayerLogin);
    }

    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent evt) {
        if (evt.getEntity() instanceof ServerPlayer player)
            MOARCommonEvents.onPlayerLogin(player);
    }

}
