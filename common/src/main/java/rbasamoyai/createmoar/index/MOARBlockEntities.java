package rbasamoyai.createmoar.index;

import static rbasamoyai.createmoar.CreateMOAR.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import rbasamoyai.createmoar.content.reactors.construction.graphite.GraphiteReactorBlockBlockEntity;
import rbasamoyai.createmoar.content.reactors.fuel.FuelBundleBlockEntity;

public class MOARBlockEntities {

    //////// Reactor parts ////////

    public static final BlockEntityEntry<GraphiteReactorBlockBlockEntity> GRAPHITE_REACTOR_BLOCK = REGISTRATE
        .blockEntity("graphite_reactor_block", GraphiteReactorBlockBlockEntity::new)
        .validBlock(MOARBlocks.GRAPHITE_REACTOR_BLOCK)
        .register();

    public static final BlockEntityEntry<FuelBundleBlockEntity> FUEL_BUNDLE = REGISTRATE
        .blockEntity("fuel_assembly", FuelBundleBlockEntity::new)
        .validBlock(MOARBlocks.FUEL_BUNDLE)
        .register();

    public static void register() {}

}
