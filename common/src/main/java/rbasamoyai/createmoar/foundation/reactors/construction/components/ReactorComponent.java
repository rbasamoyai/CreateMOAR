package rbasamoyai.createmoar.foundation.reactors.construction.components;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;
import rbasamoyai.createmoar.foundation.reactors.elements.ReactorSimulationCellInfo;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronMatrix;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronVector;

public interface ReactorComponent {

    BlockPos blockPos();

    /**
     * Get all the nuclear elements in this reactor component.
     * @return the nuclear elements present
     * @implNote This should not return any element views with {@link NuclearElementView#getElement()}
     * such that {@link NuclearElement#isInvalid()} returns true.
     */
    List<NuclearElementView> getElements();
    double getTotalVolume();

    NeutronVector getNormalizedNeutronAmount();
    NeutronVector getNeutronScale();

    default NeutronVector getNeutronAmount() {
        return this.getNormalizedNeutronAmount().mult(this.getNeutronScale());
    }

    void setNormalizedNeutronAmount(NeutronVector vec);
    void setNeutronScale(NeutronVector vec);

    // TODO: heat functions

    void updateInternalState();

    default void setReactionData(ReactorSimulationCellInfo data) {
        NeutronMatrix neutronTransformations = data.neutronTransitions();
        NeutronVector neutronSource = data.neutronSource();
        NeutronVector diffusion = data.diffusion();
        List<Pair<NuclearElementView, NeutronVector>> elementChangeFromReactions = data.elementChangeFromReactions();
        List<Pair<NuclearElementView, Double>> elementChangeFromSource = data.elementChangeFromSource();

        List<NuclearElementView> elements = this.getElements();
        if (elements.isEmpty())
            return;

        double totalVolume = this.getTotalVolume();
        if (totalVolume < 1e-2d)
            return;

        diffusion.set(0, 0); // Initial average transport cross section

        for (NuclearElementView view : elements) {
            NuclearElement element = view.getElement();
            double amount = view.getAmount();
            double volume = view.getVolume();

            // E. E. Lewis, Fundamentals of Nuclear Reactor Physics (2008), p. 95, eqs. (4.2) and (4.3)
            double volumeScalar = volume / totalVolume;

            // M. Ragheb, Neutron Diffusion Theory (2017), eq. 2
            double avgScattering = element.atomicMass() == 0 ? 0 : 2d / 3d / element.atomicMass();

            double temperatureScalar = getDopplerBroadening(view.getTemperature());
            double thermalFission = 0;
            double fastFission = 0;
            double thermalAbsorption = 0;
            double fastAbsorption = 0;

            NeutronVector elementReactionChange = new NeutronVector();
            double elementSourceChange = 0;

            NuclearElement.AreaProperties thermalProperties = element.thermalAreaProperties();
            if (thermalProperties != null) {
                double thermalScattering = amount * thermalProperties.scattering() * volumeScalar;
                thermalAbsorption = amount * thermalProperties.absorption() * temperatureScalar * volumeScalar;
                thermalFission = amount * thermalProperties.fission() * temperatureScalar * volumeScalar;
                double moderation = thermalScattering * element.moderationScalar() * volumeScalar;

                neutronTransformations.thermalToThermal += -thermalAbsorption - thermalFission;
                neutronTransformations.fastToThermal += moderation;
                neutronTransformations.fastToFast -= moderation;

                // Lewis, p. 142, eq. (6.13)
                double thermalTotal = thermalAbsorption + thermalFission + thermalScattering;
                diffusion.thermal = thermalTotal - thermalScattering * avgScattering;
            }
            NuclearElement.AreaProperties fastProperties = element.fastAreaProperties();
            if (fastProperties != null) {
                fastAbsorption = amount * fastProperties.absorption() * volumeScalar;
                neutronTransformations.fastToFast -= fastAbsorption;
                fastFission = amount * fastProperties.fission() * volumeScalar;

                double fastScattering = amount * fastProperties.scattering() * volumeScalar;
                // Lewis, p. 142, eq. (6.13)
                double fastTotal = fastAbsorption + fastFission + fastScattering;
                diffusion.fast = fastTotal - fastScattering * avgScattering;
            }
            NuclearElement.FissionProperties fissionProperties = element.fissionProperties();
            if (fissionProperties != null) {
                double neutronMultiplier = fissionProperties.neutronMultiplier();
                neutronSource.fast += amount * Math.exp(fissionProperties.spontaneousFissionRate() * 0.05) * neutronMultiplier;
                neutronTransformations.thermalToFast += thermalFission * neutronMultiplier;
                neutronTransformations.fastToFast += fastFission * (neutronMultiplier - 1);
                elementReactionChange.sub(thermalFission, fastFission);

                for (Map.Entry<NuclearElement, Double> productEntry : fissionProperties.products().entrySet())
                    elementChangeFromReactions.add(Pair.of(view.createProductView(productEntry.getKey()), new NeutronVector(0, productEntry.getValue())));
            }
            NuclearElement.DecayProperties decayProperties = element.decayProperties();
            if (decayProperties != null) {
                double decayAmount = amount * Math.exp(decayProperties.decayConstant() * 0.05);
                neutronSource.fast += decayAmount * decayProperties.neutronsProduced();
                elementSourceChange -= decayAmount;
            }

            if (element.isDestroyedByNeutronCapture())
                elementReactionChange.sub(thermalAbsorption, fastAbsorption);
            NuclearElement capture = element.neutronCaptureProduct();
            if (capture != null)
                elementChangeFromReactions.add(Pair.of(view.createProductView(capture), new NeutronVector(thermalAbsorption, fastAbsorption)));

            if (view.isMutable()) {
                elementChangeFromReactions.add(Pair.of(view, elementReactionChange));
                elementChangeFromSource.add(Pair.of(view, elementSourceChange));
            }
        }

        if (diffusion.thermal <= 1e-6d)
            diffusion.thermal = 1;
        if (diffusion.fast <= 1e-6d)
            diffusion.fast = 1;
        // Lewis, p. 142, eq. (6.13)
        diffusion.set(1d / 3d / diffusion.thermal, 1d / 3d / diffusion.fast);
    }

    /**
     * @param temperature in degrees Kelvin
     * @return broadening scalar
     */
    static double getDopplerBroadening(double temperature) { return Math.sqrt((273 + 20) / temperature); }

    @FunctionalInterface
    interface Provider {
        @Nonnull ReactorComponent createReactorComponent(Level level, BlockPos pos, BlockState blockState);
        Provider DEFAULT = (level, pos, blockState) -> new DefaultStructuralReactorComponent(pos, blockState);
    }

}
