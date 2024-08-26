package rbasamoyai.createmoar.datagen.fabric;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.content.reactors.fuel.FuelBundleBlock;

public class MOARBuilderTransformersImpl {

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> graphiteBlock() {
        return b -> b.blockstate((c, p) -> p.directionalBlock(c.get(), p.models().getExistingFile(CreateMOAR.path(c.getName()))));
    }

    public static <T extends FuelBundleBlock, P> NonNullUnaryOperator<BlockBuilder<T, P>> fuelBundle() {
        return b -> b.blockstate((c, p) -> {
            ModelFile model = p.models().getExistingFile(CreateMOAR.path(c.getName()));
            p.axisBlock(c.get(), model, model);
        });
    }

}
