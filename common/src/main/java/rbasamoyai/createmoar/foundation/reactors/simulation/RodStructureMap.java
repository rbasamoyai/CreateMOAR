package rbasamoyai.createmoar.foundation.reactors.simulation;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.simibubi.create.foundation.utility.Iterate;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RodStructureMap<T> {

    private final Map<BlockPos, Map<BlockPos, List<T>>> xRods = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, Map<BlockPos, List<T>>> yRods = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, Map<BlockPos, List<T>>> zRods = new Object2ObjectOpenHashMap<>();

    // Back-references yippee
    private final Map<BlockPos, ObjectObjectMutablePair<BlockPos, List<T>>> xRodsByPos = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, ObjectObjectMutablePair<BlockPos, List<T>>> yRodsByPos = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, ObjectObjectMutablePair<BlockPos, List<T>>> zRodsByPos = new Object2ObjectOpenHashMap<>();

    public RodStructureMap() {}

    public Map<BlockPos, Map<BlockPos, List<T>>> getAllRodsAlongAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> this.xRods;
            case Y -> this.yRods;
            case Z -> this.zRods;
        };
    }

    /**
     * Retrieves the rods aligned on a given axis that passes through the given coordinate,
     * if at least one ord exists. The rods do not have to contain the coordinate, only pass
     * through it as if they infinitely spanned their axis.
     *
     * @param axis the axis of the structure to search
     * @param pos the position in the structure
     * @return the map representing the collection of rods, or null if the position is not stored
     */
    @Nullable
    public Map<BlockPos, List<T>> getAllRodsAlongAxisAndCoordinate(Direction.Axis axis, BlockPos pos) {
        return switch(axis) {
            case X -> this.xRods.get(new BlockPos(0, pos.getY(), pos.getZ()));
            case Y -> this.yRods.get(new BlockPos(pos.getX(), 0, pos.getZ()));
            case Z -> this.zRods.get(new BlockPos(pos.getX(), pos.getY(), 0));
        };
    }

    /**
     * Retrieves the rod aligned on a given axis containing a given position, if it exists.
     * The returned list should not be modified!
     *
     * @param axis the axis of the structure to search
     * @param pos the position in the structure
     * @return the list representing the rod, or null if the position is not stored, and the
     * associated root position.
     */
    @Nullable
    public Pair<BlockPos, List<T>> getRodContainingCoordinate(Direction.Axis axis, BlockPos pos) {
        return switch (axis) {
            case X -> this.xRodsByPos.get(pos);
            case Y -> this.yRodsByPos.get(pos);
            case Z -> this.zRodsByPos.get(pos);
        };
    }

    /**
     * Retrieves the rod aligned on a given axis containing a given position, if it exists.
     * Internal version of {@link #getRodContainingCoordinate(Direction.Axis, BlockPos)}.
     *
     * @param axis the axis of the structure to search
     * @param pos the position in the structure
     * @return the list representing the rod, or null if the position is not stored, and the
     * associated root position.
     */
    @Nullable
    private ObjectObjectMutablePair<BlockPos, List<T>> getMutableRodContainingCoordinate(Direction.Axis axis, BlockPos pos) {
        return switch (axis) {
            case X -> this.xRodsByPos.get(pos);
            case Y -> this.yRodsByPos.get(pos);
            case Z -> this.zRodsByPos.get(pos);
        };
    }

    /**
     * Create a new rod at the location and axis.
     *
     * @param axis the target axis
     * @param pos the target position
     * @param rod the rod to add
     */
    private void addRod(Direction.Axis axis, BlockPos pos, List<T> rod) {
        Map<BlockPos, List<T>> map = new Object2ObjectOpenHashMap<>();
        map.put(pos, rod);
        switch (axis) {
            case X -> this.xRods.put(new BlockPos(0, pos.getY(), pos.getZ()), map);
            case Y -> this.yRods.put(new BlockPos(pos.getX(), 0, pos.getZ()), map);
            case Z -> this.zRods.put(new BlockPos(pos.getX(), pos.getY(), 0), map);
        }
    }

    private void setBackReference(Direction.Axis axis, BlockPos pos, ObjectObjectMutablePair<BlockPos, List<T>> rod) {
        switch (axis) {
            case X -> this.xRodsByPos.put(pos, rod);
            case Y -> this.yRodsByPos.put(pos, rod);
            case Z -> this.zRodsByPos.put(pos, rod);
        }
    }

    /**
     * Does not remove back-references.
     * @param axis the target axis
     * @param pos the root pos to remove
     */
    private void removeRodInternal(Direction.Axis axis, BlockPos pos) {
        switch (axis) {
            case X -> this.xRods.remove(pos);
            case Y -> this.yRods.remove(pos);
            case Z -> this.zRods.remove(pos);
        }
    }

    /**
     * Sets an object at the specified position. If not yet connected to an
     * adjacent space, tries to merge it with adjacent rods, creating new rods
     * if unable to.
     *
     * @param pos the position
     * @param t the object to insert
     * @return true if already merged or able to connect with already existing
     * rods on at least one axis, false otherwise.
     */
    public boolean set(BlockPos pos, T t) {
        boolean merged = false;
        pos = pos.immutable();
        BlockPos.MutableBlockPos selector = new BlockPos.MutableBlockPos();

        for (Direction.Axis axis : Iterate.axes) {
            Pair<BlockPos, List<T>> currentRod = this.getMutableRodContainingCoordinate(axis, pos);
            if (currentRod != null) {
                currentRod.right().set(getIndexAlongAxis(axis, currentRod.left(), pos), t);
                merged = true;
                continue;
            }

            Direction positive = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction negative = positive.getOpposite();

            // Really dependent on the structure being set up correctly!
            ObjectObjectMutablePair<BlockPos, List<T>> negativeRod = this.getMutableRodContainingCoordinate(axis, selector.setWithOffset(pos, negative));
            ObjectObjectMutablePair<BlockPos, List<T>> positiveRod = this.getMutableRodContainingCoordinate(axis, selector.setWithOffset(pos, positive));

            if (negativeRod != null && positiveRod != null) {
                negativeRod.right().add(t);
                negativeRod.right().addAll(positiveRod.right());

                // Update back-references on positive rod elements
                Direction dir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
                int sz = positiveRod.right().size();
                for (int i = 0; i < sz; ++i)
                    this.setBackReference(axis, pos.relative(dir, i), negativeRod);
                this.removeRodInternal(axis, positiveRod.left());
                merged = true;
            } else if (negativeRod != null) {
                negativeRod.right().add(t);
                merged = true;
            } else if (positiveRod != null) {
                // Unlike the negative-positive merger, the same rod pair reference should be shared.
                // Therefore, it is critical for the structure to be set up correctly!
                positiveRod.left(pos);
                positiveRod.right().add(0, t);
                merged = true;
            } else {
                this.addRod(axis, pos, Lists.newArrayList(t));
            }
        }
        return merged;
    }

    private static int getIndexAlongAxis(Direction.Axis axis, BlockPos origin, BlockPos pos) {
        return switch (axis) {
            case X -> pos.getX() - origin.getX();
            case Y -> pos.getY() - origin.getY();
            case Z -> pos.getZ() - origin.getZ();
        };
    }

}
