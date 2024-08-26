package rbasamoyai.createmoar.content.reactors.construction.graphite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createmoar.foundation.reactors.construction.components.ReactorComponent;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.ConstantElementView;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronVector;
import rbasamoyai.createmoar.index.MOARNuclearElements;

public class GraphiteReactorBlockComponent implements ReactorComponent {

    private final BlockPos pos;
    private final GraphiteReactorBlockBlockEntity be;
    private final Map<NuclearElement, Double> diffusedElements = new Reference2DoubleArrayMap<>();


    private NeutronVector normalizedNeutrons = new NeutronVector();
    private NeutronVector neutronScale = new NeutronVector();

    public GraphiteReactorBlockComponent(Level level, BlockPos pos, BlockState blockState) {
        this.pos = pos;
        this.be = level.getBlockEntity(pos) instanceof GraphiteReactorBlockBlockEntity be ? be : null;
    }

    @Override public BlockPos blockPos() { return this.pos; }

    @Override
    public List<NuclearElementView> getElements() {
        List<NuclearElementView> elements = new ArrayList<>();
        elements.add(ConstantElementView.fromVolume(MOARNuclearElements.GRAPHITE, 7000, this::addProduct));
        if (this.be != null)
            elements.addAll(this.be.getContainedElements());
        return elements;
    }

    @Override
    public double getTotalVolume() {
        return 8000; // TODO config?
    }

    protected void addProduct(NuclearElement element, double amount) {
        this.diffusedElements.merge(element, amount, Double::sum);
    }

    @Override public NeutronVector getNormalizedNeutronAmount() { return this.normalizedNeutrons; }
    @Override public NeutronVector getNeutronScale() { return this.neutronScale; }

    @Override public void setNormalizedNeutronAmount(NeutronVector vec) { this.normalizedNeutrons = vec; }
    @Override public void setNeutronScale(NeutronVector vec) { this.neutronScale = vec; }

    @Override
    public void updateInternalState() {

    }

}
