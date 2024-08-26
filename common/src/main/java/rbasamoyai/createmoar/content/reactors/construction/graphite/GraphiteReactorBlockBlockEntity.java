package rbasamoyai.createmoar.content.reactors.construction.graphite;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.content.reactors.ReactorRodBlock;
import rbasamoyai.createmoar.content.reactors.construction.PhysicalBlockStorageBehavior;
import rbasamoyai.createmoar.content.reactors.construction.ReactorRodVesselBlockEntity;
import rbasamoyai.createmoar.foundation.reactors.construction.ReactorPartBlockEntity;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;
import rbasamoyai.createmoar.foundation.reactors.simulation.ReactorCore;
import rbasamoyai.createmoar.index.MOARBlocks;

public class GraphiteReactorBlockBlockEntity extends SmartBlockEntity implements ReactorPartBlockEntity, ReactorRodVesselBlockEntity {

    protected PhysicalBlockStorageBehavior blockStorageBehavior;

	protected int syncCooldown;
	protected boolean queuedSync;

	private UUID reactorId;

	public GraphiteReactorBlockBlockEntity(BlockEntityType<? extends GraphiteReactorBlockBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(this.blockStorageBehavior = new PhysicalBlockStorageBehavior(this));
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		this.reactorId = tag.hasUUID("Reactor") ? tag.getUUID("Reactor") : null;
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);

		if (this.reactorId != null)
            tag.putUUID("Reactor", this.reactorId);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.syncCooldown > 0) {
			this.syncCooldown--;
			if (this.syncCooldown <= 0 && this.queuedSync)
				this.sendData();
		}
	}

	@Override
	@Nullable
	public ReactorCore getReactor() {
		return this.level == null || this.reactorId == null ? null : CreateMOAR.REACTOR_MANAGER.getReactor(this.level, this.reactorId);
	}

	@Override public void setReactor(ReactorCore reactor) { this.reactorId = reactor.getUUID(); }
	@Override public void removeReactor() { this.reactorId = null; }

    @Override
    public boolean canInsertBlock(StructureBlockInfo toInsert, boolean push) {
        Direction.Axis axis = this.getBlockState().getValue(BlockStateProperties.FACING).getAxis();
        BlockState insertedState = toInsert.state();
        if (AllBlocks.PISTON_EXTENSION_POLE.has(insertedState))
            return insertedState.getValue(BlockStateProperties.FACING).getAxis() == axis;
        // Control rod
        // Rod mechanism head
        if (MOARBlocks.FUEL_BUNDLE.has(insertedState))
            return insertedState.getValue(BlockStateProperties.AXIS) == axis;
        return false;
    }

    @Override
    public void insertBlock(StructureBlockInfo toInsert) {
        this.blockStorageBehavior.setBlock(toInsert);
        this.notifyUpdate();
    }

    @Override
    public void removeContainedBlock() {
        this.blockStorageBehavior.removeBlock();
        this.notifyUpdate();
    }

    @Override public StructureBlockInfo getBlock() { return this.blockStorageBehavior.getBlock(); }

    @Override
    public List<NuclearElementView> getContainedElements() {
        List<NuclearElementView> list = new ArrayList<>();
        StructureBlockInfo blockInfo = this.blockStorageBehavior.getBlock();
        if (blockInfo.state().getBlock() instanceof ReactorRodBlock rod)
            list.addAll(rod.getNuclearElements(blockInfo));
        return list;
    }

    @Override public Direction getDirection() { return this.getBlockState().getValue(BlockStateProperties.FACING); }

    // TODO handle fluids and materials

}
