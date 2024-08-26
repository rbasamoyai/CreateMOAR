package rbasamoyai.createmoar.foundation.reactors.simulation;

import java.util.List;
import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import rbasamoyai.createmoar.foundation.reactors.construction.components.ReactorComponent;
import rbasamoyai.createmoar.foundation.reactors.elements.ReactorSimulationCellInfo;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronVector;

public record ReactorSimulationContext(Map<BlockPos, ReactorComponent> reactorCells,
                                       Map<Direction.Axis, Map<BlockPos, Pair<BlockPos, List<ReactorComponent>>>> reactorRods,
                                       Map<BlockPos, ReactorSimulationCellInfo> reactionData,
                                       NeutronVector amountScaling, double heatScaling) {

}
