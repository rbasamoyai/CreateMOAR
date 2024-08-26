package rbasamoyai.createmoar.foundation.reactors.construction;

import javax.annotation.Nullable;

import rbasamoyai.createmoar.foundation.reactors.simulation.ReactorCore;

public interface ReactorPartBlockEntity {

	@Nullable ReactorCore getReactor();
	void setReactor(ReactorCore reactor);
	void removeReactor();

}
