package rbasamoyai.createmoar.index;

import static rbasamoyai.createmoar.CreateMOAR.REGISTRATE;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import rbasamoyai.createmoar.content.reactors.construction.graphite.GraphiteReactorBlock;
import rbasamoyai.createmoar.content.reactors.construction.graphite.GraphiteReactorBlockComponent;
import rbasamoyai.createmoar.content.reactors.fuel.FuelBundleBlock;
import rbasamoyai.createmoar.datagen.MOARBuilderTransformers;
import rbasamoyai.createmoar.foundation.reactors.construction.components.ReactorComponentRegistry;

public class MOARBlocks {

    static {
        ModGroup.useModTab(ModGroup.MAIN_TAB_KEY);
    }

    //////// Reactor parts ////////

    public static final BlockEntry<GraphiteReactorBlock> GRAPHITE_REACTOR_BLOCK = REGISTRATE
        .block("graphite_reactor_block", GraphiteReactorBlock::new)
        .initialProperties(SharedProperties::stone)
        .transform(MOARBuilderTransformers.graphiteBlock())
        .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
        .onRegister(block -> ReactorComponentRegistry.registerProvider(block, GraphiteReactorBlockComponent::new))
        .simpleItem()
        .register();

    public static final BlockEntry<FuelBundleBlock> FUEL_BUNDLE = REGISTRATE
        .block("fuel_bundle", FuelBundleBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .transform(MOARBuilderTransformers.fuelBundle())
        .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
        .simpleItem()
        .register();

    public static void register() {}

}
