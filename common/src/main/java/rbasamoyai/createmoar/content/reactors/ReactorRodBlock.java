package rbasamoyai.createmoar.content.reactors;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;

public interface ReactorRodBlock {

    Direction.Axis getRodAxis(BlockState blockState);

    List<NuclearElementView> getNuclearElements(Level level, BlockPos pos, BlockState blockState);
    List<NuclearElementView> getNuclearElements(ItemStack itemStack);
    List<NuclearElementView> getNuclearElements(StructureBlockInfo blockInfo);
    double getRodVolume(BlockState blockState);

    StructureBlockInfo getInsertedBlockInfo(ItemStack itemStack, Direction direction);

}
