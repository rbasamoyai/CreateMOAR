package rbasamoyai.createmoar.datagen;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createmoar.content.reactors.fuel.FuelBundleBlock;

public class MOARBuilderTransformers {

    @ExpectPlatform public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> graphiteBlock() { throw new AssertionError(); }
    @ExpectPlatform public static <T extends FuelBundleBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> fuelBundle() { throw new AssertionError(); }

}
