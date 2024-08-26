package rbasamoyai.createmoar.datagen.forge;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.world.level.block.Block;
import rbasamoyai.createmoar.content.reactors.fuel.FuelBundleBlock;

public class MOARBuilderTransformersImpl {

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> graphiteBlock() { return b -> b; }
    public static <T extends FuelBundleBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> fuelBundle() { return b -> b; }

}
