package rbasamoyai.createmoar.foundation.reactors.saved_data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import rbasamoyai.createmoar.foundation.reactors.simulation.ReactorCore;

public class DimensionReactorManager {

    private Map<UUID, ReactorCore> waitingReactors = new HashMap<>();
    private Map<UUID, ReactorCore> reactors = new HashMap<>();
    private ReactorSavedData savedData;

    public void load(ServerLevel level) {
        this.savedData = ReactorSavedData.load(level);
        this.reactors = this.savedData.reactors();
        this.waitingReactors = this.savedData.waitingReactors();
    }

    public void tickLevelPre(Level level) {
        if (this.reactors.isEmpty())
            return;
        for (Iterator<ReactorCore> iter = this.reactors.values().iterator(); iter.hasNext(); ) {
            ReactorCore reactor = iter.next();
            reactor.tick(level);
            if (reactor.removed())
                iter.remove();
        }
        this.reactors.putAll(this.waitingReactors);
        this.waitingReactors.clear();
        this.savedData.setDirty();
    }

    public ReactorCore createNewReactor(Level level, BlockPos pos) {
        ReactorCore reactor = new ReactorCore(Mth.createInsecureUUID(level.random));
        reactor.dimension = level.dimension();
        reactor.queueAddReactorBlock(level, pos);
        this.reactors.put(reactor.getUUID(), reactor);
        return reactor;
    }

    public ReactorCore queueNewReactor(Level level, BlockPos pos) {
        ReactorCore reactor = new ReactorCore(Mth.createInsecureUUID(level.random));
        reactor.dimension = level.dimension();
        reactor.queueAddReactorBlock(level, pos);
        this.waitingReactors.put(reactor.getUUID(), reactor);
        return reactor;
    }

    @Nullable public ReactorCore getReactor(UUID uuid) { return this.reactors.get(uuid); }


}
