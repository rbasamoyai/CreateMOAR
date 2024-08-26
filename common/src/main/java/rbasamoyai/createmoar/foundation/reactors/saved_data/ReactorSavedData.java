package rbasamoyai.createmoar.foundation.reactors.saved_data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import rbasamoyai.createmoar.foundation.reactors.simulation.ReactorCore;

public class ReactorSavedData extends SavedData {

	private final Map<UUID, ReactorCore> reactors = new HashMap<>();
	private final Map<UUID, ReactorCore> waitingReactors = new HashMap<>();

	@Override
	public CompoundTag save(CompoundTag tag) {
		CompoundTag reactorTag = new CompoundTag();
		for (Map.Entry<UUID, ReactorCore> entry : this.reactors.entrySet()) {
			reactorTag.put(entry.getKey().toString(), entry.getValue().writeNbt(new CompoundTag()));
		}
		for (Map.Entry<UUID, ReactorCore> entry : this.waitingReactors.entrySet()) {
			reactorTag.put(entry.getKey().toString(), entry.getValue().writeNbt(new CompoundTag()));
		}
		tag.put("Reactors", reactorTag);
		return tag;
	}

	private static ReactorSavedData load(CompoundTag tag) {
		ReactorSavedData data = new ReactorSavedData();

		CompoundTag reactorTag = tag.getCompound("Reactors");
		for (String key : reactorTag.getAllKeys()) {
			UUID uuid = UUID.fromString(key);
			ReactorCore reactor = ReactorCore.fromNbt(reactorTag.getCompound(key), uuid);
			data.reactors.put(uuid, reactor);
		}

		return data;
	}

	public Map<UUID, ReactorCore> reactors() { return this.reactors; }
	public Map<UUID, ReactorCore> waitingReactors() { return this.waitingReactors; }

	public static ReactorSavedData load(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(ReactorSavedData::load, ReactorSavedData::new, "createmoar_reactor_data");
	}

}
