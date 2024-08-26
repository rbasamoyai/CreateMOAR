package rbasamoyai.createmoar.content.reactors.construction;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import rbasamoyai.createmoar.content.reactors.ReactorRodBlock;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;

public interface ReactorRodVesselBlockEntity {

	boolean canInsertBlock(StructureBlockInfo toInsert, boolean push);
    void insertBlock(StructureBlockInfo toInsert);
    void removeContainedBlock();
    StructureBlockInfo getBlock();

    List<NuclearElementView> getContainedElements();
    Direction getDirection();

    static InteractionResult onManualUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // TODO better system for hand insertion. this is just temporary
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof ReactorRodVesselBlockEntity vessel
            && hit.getDirection().getAxis() == vessel.getDirection().getAxis()) {
            if (itemStack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ReactorRodBlock rodBlock) {
                StructureBlockInfo blockInfo = rodBlock.getInsertedBlockInfo(itemStack, vessel.getDirection());
                boolean consumed = false;
                if (vessel.canInsertBlock(blockInfo, false)) {
                    consumed = true;
                    if (!level.isClientSide)
                        vessel.insertBlock(blockInfo);
                } else if (vessel.canInsertBlock(blockInfo, true)) {
                    // TODO pushing? e.g. Pu breeder reactor
                }
                if (consumed) {
                    level.playSound(player, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
                    if (!level.isClientSide)
                        itemStack.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            } else if (itemStack.isEmpty()) {
                StructureBlockInfo blockInfo = vessel.getBlock();
                ItemStack result = new ItemStack(blockInfo.state().getBlock().asItem());
                if (!result.isEmpty()) {
                    if (!level.isClientSide) {
                        if (blockInfo.nbt() != null)
                            result.getOrCreateTag().put("BlockEntityTag", blockInfo.nbt());
                        player.setItemInHand(hand, result);
                        vessel.removeContainedBlock();
                    }
                    level.playSound(player, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1, 1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

}
