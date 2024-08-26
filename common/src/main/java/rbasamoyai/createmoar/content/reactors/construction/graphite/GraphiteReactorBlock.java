package rbasamoyai.createmoar.content.reactors.construction.graphite;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.content.reactors.construction.ReactorRodVesselBlockEntity;
import rbasamoyai.createmoar.foundation.reactors.simulation.ReactorCore;
import rbasamoyai.createmoar.index.MOARBlockEntities;

public class GraphiteReactorBlock extends DirectionalBlock implements IBE<GraphiteReactorBlockBlockEntity> {

	public GraphiteReactorBlock(Properties properties) {
		super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP));
	}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override public Class<GraphiteReactorBlockBlockEntity> getBlockEntityClass() { return GraphiteReactorBlockBlockEntity.class; }
	@Override public BlockEntityType<? extends GraphiteReactorBlockBlockEntity> getBlockEntityType() { return MOARBlockEntities.GRAPHITE_REACTOR_BLOCK.get(); }

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		super.onPlace(state, level, pos, newState, moving);

		if (!level.isClientSide) {
			ReactorCore reactor = null;
			GraphiteReactorBlockBlockEntity rootGraphite = this.getBlockEntity(level, pos);
			if (rootGraphite != null) {
				for (Direction dir : Iterate.directions) {
					BlockPos pos1 = pos.relative(dir);
					if (!(level.getBlockEntity(pos1) instanceof GraphiteReactorBlockBlockEntity graphite))
                        continue;
					ReactorCore otherReactor = graphite.getReactor();
					if (reactor == null && otherReactor != null) {
						reactor = otherReactor;
					} else if (reactor != null && otherReactor == null) {
						reactor.queueAddReactorBlock(level, pos1);
					}
					if (otherReactor != reactor && otherReactor != null) {
						reactor.queueReactorMerge(otherReactor);
					}
				}
				if (reactor == null) {
					CreateMOAR.REACTOR_MANAGER.createNewReactor(level, pos);
				} else {
					reactor.queueAddReactorBlock(level, pos);
				}
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean b) {
		if (!level.isClientSide) {
			this.withBlockEntityDo(level, pos, graphite -> {
				ReactorCore reactor = graphite.getReactor();
				if (reactor != null)
                    reactor.queueRemoveReactorBlock(level, pos);
			});
		}
		super.onRemove(state, level, pos, newState, b);
	}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = ReactorRodVesselBlockEntity.onManualUse(state, level, pos, player, hand, hit);
        return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
    }

}
