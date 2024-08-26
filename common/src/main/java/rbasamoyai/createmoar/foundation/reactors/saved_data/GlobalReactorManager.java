package rbasamoyai.createmoar.foundation.reactors.saved_data;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import rbasamoyai.createmoar.foundation.reactors.simulation.ReactorCore;

public class GlobalReactorManager {

    private final Map<ResourceKey<Level>, DimensionReactorManager> reactorManagers = new Object2ObjectOpenHashMap<>();

    private DimensionReactorManager getOrCreateManager(Level level) {
        return this.reactorManagers.computeIfAbsent(level.dimension(), l -> new DimensionReactorManager());
    }

	public void levelLoaded(ServerLevel level) {
        DimensionReactorManager manager = this.getOrCreateManager(level);
        manager.load(level);
	}

    public void levelUnloaded(Level level) {
        MinecraftServer server = level.getServer();
        if (server == null)
            return;
        this.reactorManagers.remove(level.dimension());
    }

	public void cleanUp() {
		this.reactorManagers.clear();
	}

	public void tickLevelPre(Level level) {
        DimensionReactorManager manager = this.reactorManagers.get(level.dimension());
		if (manager == null)
            return;
        manager.tickLevelPre(level);
	}

	public ReactorCore createNewReactor(Level level, BlockPos pos) {
        DimensionReactorManager manager = this.getOrCreateManager(level);
		return manager.createNewReactor(level, pos);
	}

	public ReactorCore queueNewReactor(Level level, BlockPos pos) {
        DimensionReactorManager manager = this.getOrCreateManager(level);
		return manager.queueNewReactor(level, pos);
	}

	@Nullable
    public ReactorCore getReactor(Level level, UUID uuid) {
        if (!this.reactorManagers.containsKey(level.dimension()))
            return null;
        return this.reactorManagers.get(level.dimension()).getReactor(uuid);
    }

}
