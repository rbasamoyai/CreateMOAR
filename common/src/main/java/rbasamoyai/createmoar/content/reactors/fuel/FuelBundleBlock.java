package rbasamoyai.createmoar.content.reactors.fuel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;

import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.createmoar.content.reactors.ReactorRodBlock;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.ConstantElementView;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.index.MOARBlockEntities;
import rbasamoyai.createmoar.index.MOARBlocks;
import rbasamoyai.createmoar.index.MOARNuclearElements;
import rbasamoyai.createmoar.utils.MOARUtils;

public class FuelBundleBlock extends RotatedPillarBlock implements IBE<FuelBundleBlockEntity>, ReactorRodBlock {

	public FuelBundleBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AllShapes.FOUR_VOXEL_POLE.get(state.getValue(AXIS));
	}

	@Override public Class<FuelBundleBlockEntity> getBlockEntityClass() { return FuelBundleBlockEntity.class; }
	@Override public BlockEntityType<? extends FuelBundleBlockEntity> getBlockEntityType() { return MOARBlockEntities.FUEL_BUNDLE.get(); }

	@Override
	public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
		return type == this.getBlockEntityType() ? (l1, p1, s1, be) -> ((FuelBundleBlockEntity) be).tick() : null;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		ItemStack stack = player.getItemInHand(hand);

		// TODO: temporary, support more fuels later
		if (AllItems.CRUSHED_URANIUM.isIn(stack)) {
			return this.onBlockEntityUse(level, pos, rod -> rod.onFillBlock(stack));
		}

		return super.use(state, level, pos, player, hand, result);
	}

    @Override public Direction.Axis getRodAxis(BlockState blockState) { return blockState.getValue(AXIS); }

    @Override
    public List<NuclearElementView> getNuclearElements(Level level, BlockPos pos, BlockState blockState) {
        List<NuclearElementView> list = new ArrayList<>();
        if (level.getBlockEntity(pos) instanceof FuelBundleBlockEntity bundle)
            list.addAll(bundle.getElementViews());
        return list;
    }

    @Override
    public List<NuclearElementView> getNuclearElements(ItemStack itemStack) {
        CompoundTag beTag = itemStack.getOrCreateTag().getCompound("BlockEntityTag");
        BiConsumer<NuclearElement, Double> cons = (el, v) -> MOARUtils.mergeElementNbt(beTag.getCompound("Products"), el, v);
        List<NuclearElementView> list = loadElementViews(beTag, this.getRodVolume(this.defaultBlockState()), cons);
        list.add(ConstantElementView.fromVolume(MOARNuclearElements.IRON_56, 100, cons));
        return list;
    }

    @Override
    public List<NuclearElementView> getNuclearElements(StructureBlockInfo blockInfo) {
        List<NuclearElementView> list = new ArrayList<>();
        CompoundTag beTag = blockInfo.nbt();
        BiConsumer<NuclearElement, Double> cons = beTag == null ? (el, v) -> {} :
            (el, v) -> MOARUtils.mergeElementNbt(beTag.getCompound("Products"), el, v);
        if (beTag != null)
            list.addAll(loadElementViews(beTag, this.getRodVolume(blockInfo.state()), cons));
        list.add(ConstantElementView.fromVolume(MOARNuclearElements.IRON_56, 100, cons));
        return list;
    }

    public static List<NuclearElementView> loadElementViews(CompoundTag tag, double volume, BiConsumer<NuclearElement, Double> cons) {
        Map<NuclearElement, Double> byVolume = new Reference2DoubleOpenHashMap<>();
        MOARUtils.loadElementsFromNbt(tag.getCompound("Contents"), byVolume);

        Map<NuclearElement, Double> byAmount = new Reference2DoubleOpenHashMap<>();
        MOARUtils.loadElementsFromNbt(tag.getCompound("Products"), byAmount);
        return MOARUtils.getFuelElements(byVolume, byAmount, volume, cons);
    }

    @Override
    public double getRodVolume(BlockState blockState) {
        return 1000; // TODO config?
    }

    @Override
    public StructureBlockInfo getInsertedBlockInfo(ItemStack itemStack, Direction direction) {
        BlockState blockState = MOARBlocks.FUEL_BUNDLE.getDefaultState().setValue(AXIS, direction.getAxis());
        return new StructureBlockInfo(BlockPos.ZERO, blockState, itemStack.getOrCreateTag().getCompound("BlockEntityTag"));
    }

}
