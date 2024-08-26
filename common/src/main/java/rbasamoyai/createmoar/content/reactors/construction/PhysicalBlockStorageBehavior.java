package rbasamoyai.createmoar.content.reactors.construction;

import javax.annotation.Nonnull;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class PhysicalBlockStorageBehavior extends BlockEntityBehaviour {

	private static final StructureBlockInfo EMPTY = new StructureBlockInfo(BlockPos.ZERO, Blocks.AIR.defaultBlockState(), new CompoundTag());

	public static final BehaviourType<PhysicalBlockStorageBehavior> TYPE = new BehaviourType<>();

	public PhysicalBlockStorageBehavior(SmartBlockEntity be) {
		super(be);
	}

	@Override public BehaviourType<?> getType() { return TYPE; }

	private StructureBlockInfo blockInfo = EMPTY;

	public StructureBlockInfo getBlock() { return this.blockInfo == null ? EMPTY : this.blockInfo; }

	public void setBlock(@Nonnull StructureBlockInfo blockInfo) { this.blockInfo = blockInfo; }
	public void removeBlock() { this.setBlock(EMPTY); }

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
		BlockState state = NbtUtils.readBlockState(this.blockEntity.blockHolderGetter(), nbt.getCompound("State"));
		CompoundTag tag = nbt.contains("Data") ? nbt.getCompound("Data") : new CompoundTag();
		this.blockInfo = new StructureBlockInfo(pos, state, tag);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		if (!this.blockInfo.state().isAir()) {
			nbt.putLong("Pos", this.blockInfo.pos().asLong());
			nbt.put("State", NbtUtils.writeBlockState(this.blockInfo.state()));
			if (this.blockInfo.nbt() != null)
                nbt.put("Data", this.blockInfo.nbt());
		}
	}

}
