package rbasamoyai.createmoar.foundation.reactors.elements;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronMatrix;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronVector;

public record ReactorSimulationCellInfo(NeutronMatrix neutronTransitions, NeutronVector neutronSource, NeutronVector diffusion,
                                        List<Pair<NuclearElementView, NeutronVector>> elementChangeFromReactions,
                                        List<Pair<NuclearElementView, Double>> elementChangeFromSource) {

    public static ReactorSimulationCellInfo empty() {
        return new ReactorSimulationCellInfo(new NeutronMatrix(), new NeutronVector(), new NeutronVector(1, 1), new ArrayList<>(), new ArrayList<>());
    }

}
