package rbasamoyai.createmoar.foundation.reactors.construction.components;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronVector;

public class DefaultStructuralReactorComponent implements ReactorComponent {

    private final BlockPos pos;
    private final BlockState blockState;

    private NeutronVector normalizedNeutrons = new NeutronVector();
    private NeutronVector neutronScale = new NeutronVector();

    public DefaultStructuralReactorComponent(BlockPos pos, BlockState blockState) {
        this.pos = pos;
        this.blockState = blockState;
    }

    @Override public BlockPos blockPos() { return this.pos; }

    @Override
    public List<NuclearElementView> getElements() {
        return new ArrayList<>(); // TODO structural materials - use blockState
    }

    @Override
    public double getTotalVolume() {
        return 8000; // TODO config by blockState
    }

    @Override public NeutronVector getNormalizedNeutronAmount() { return this.normalizedNeutrons; }
    @Override public NeutronVector getNeutronScale() { return this.neutronScale; }

    @Override public void setNormalizedNeutronAmount(NeutronVector vec) { this.normalizedNeutrons = vec; }
    @Override public void setNeutronScale(NeutronVector vec) { this.neutronScale = vec; }

    @Override public void updateInternalState() {}

}
