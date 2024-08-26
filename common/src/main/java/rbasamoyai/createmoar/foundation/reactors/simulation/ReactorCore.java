package rbasamoyai.createmoar.foundation.reactors.simulation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.UniqueLinkedList;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.foundation.reactors.construction.ReactorPartBlockEntity;
import rbasamoyai.createmoar.foundation.reactors.construction.components.ReactorComponent;
import rbasamoyai.createmoar.foundation.reactors.construction.components.ReactorComponentRegistry;
import rbasamoyai.createmoar.foundation.reactors.elements.ReactorSimulationCellInfo;
import rbasamoyai.createmoar.foundation.reactors.simulation.math.NeutronVector;
import rbasamoyai.createmoar.utils.MOARUtils;

public class ReactorCore {

	private final UUID uuid;

	private final Set<BlockPos> blocks = new ObjectLinkedOpenHashSet<>();
	private final Map<BlockPos, Boolean> changedBlocks = new Object2ObjectOpenHashMap<>();
	private final Set<ReactorCore> reactorsToMerge = new ObjectLinkedOpenHashSet<>();

    private final Map<BlockPos, ReactorComponent> reactorComponents = new Object2ObjectOpenHashMap<>();
    private final Map<Direction.Axis, Map<BlockPos, Pair<BlockPos, List<ReactorComponent>>>> reactorComponentRods = new EnumMap<>(Direction.Axis.class);

    private NeutronVector scalingVector = new NeutronVector();

	private AABB bounds;
    private boolean changedStructure;
	private boolean removed = false;

	public ResourceKey<Level> dimension;

	public ReactorCore(UUID uuid) {
		this.uuid = uuid;
	}

	public void tick(Level level) {
		this.handleBlockChanges(level);
		if (this.removed)
			return;
        if (this.changedStructure) {
            this.changedStructure = false;
            this.onStructuralChange(level);
        }
        if (!this.canTick(level))
            return;
        Map<BlockPos, ReactorSimulationCellInfo> reactionData = this.collectReactionData();
        ReactorSimulationContext context = this.getContextForTimeStep(reactionData);
        ReactorSimulation.simulate(context);
	}

    protected void onStructuralChange(Level level) {
        this.collectReactorComponents(level);
        this.calculateBounds(); // TODO: Eventually, bounds will be based off of reactor components, particularly rods
    }

    protected ReactorSimulationContext getContextForTimeStep(Map<BlockPos, ReactorSimulationCellInfo> reactionData) {
        return new ReactorSimulationContext(this.reactorComponents, this.reactorComponentRods, reactionData, this.scalingVector, 0);
    }

	protected void calculateBounds() {
		if (this.blocks.isEmpty())
            return;
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float minZ = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float maxZ = Float.NEGATIVE_INFINITY;

		for (BlockPos pos : this.blocks) {
			minX = Math.min(minX, pos.getX());
			minY = Math.min(minY, pos.getY());
			minZ = Math.min(minZ, pos.getZ());
			maxX = Math.max(maxX, pos.getX());
			maxY = Math.max(maxY, pos.getY());
			maxZ = Math.max(maxZ, pos.getZ());
		}
		this.bounds = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1).inflate(2);
	}

    public boolean canTick(Level level) {
        if (this.reactorComponents.isEmpty())
            return false;
        if (this.bounds == null)
            this.calculateBounds();
        int minX = SectionPos.posToSectionCoord(this.bounds.minX);
        int maxX = SectionPos.posToSectionCoord(this.bounds.maxX);
        int minZ = SectionPos.posToSectionCoord(this.bounds.minZ);
        int maxZ = SectionPos.posToSectionCoord(this.bounds.maxZ);
        //int minY = SectionPos.posToSectionCoord(this.bounds.minY);
        //int maxY = SectionPos.posToSectionCoord(this.bounds.maxY);

        for (int x = minX; x <= maxX; ++x) {
            //for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                //if (level.isOutsideBuildHeight(y << 4))
                //return false;
                if (!level.getChunkSource().hasChunk(x, z))
                    return false;
            }
            //}
        }
        return true;
    }


    protected void collectReactorComponents(Level level) {
        this.reactorComponents.entrySet().removeIf(entry -> !this.blocks.contains(entry.getKey()));
        this.reactorComponentRods.clear();
        for (Direction.Axis axis : Iterate.axisSet)
            this.reactorComponentRods.put(axis, new Object2ObjectOpenHashMap<>());
        if (this.blocks.isEmpty())
            return;

        // TODO rewrite block change handling to do this much nicer and more efficiently
        for (BlockPos pos : this.blocks) {
            BlockState blockState = level.getBlockState(pos);
            ReactorComponent component = ReactorComponentRegistry.getReactorComponent(level, pos, blockState);
            this.reactorComponents.put(pos, component);

            for (Direction.Axis axis : Iterate.axisSet) {
                BlockPos axisPos = MOARUtils.getAxisPosition(pos, axis);

                Map<BlockPos, Pair<BlockPos, List<ReactorComponent>>> rodsOnAxis = this.reactorComponentRods.get(axis);
                if (!rodsOnAxis.containsKey(axisPos))
                    rodsOnAxis.put(axisPos, Pair.of(pos, new ArrayList<>()));
                Pair<BlockPos, List<ReactorComponent>> rod = rodsOnAxis.get(axisPos);
                List<ReactorComponent> rodComponents = rod.getSecond();
                int difference = MOARUtils.getAxisDifference(rod.getFirst(), pos, axis);
                if (difference < 0) {
                    int padding = -difference - 1;
                    for (int i = 0; i < padding; ++i)
                        rodComponents.add(0, null);
                    rodComponents.add(0, component);
                    rodsOnAxis.put(axisPos, Pair.of(pos, rodComponents));
                } else if (difference >= rodComponents.size()) {
                    int padding = difference - rodComponents.size();
                    for (int i = 0; i < padding; ++i)
                        rodComponents.add(null);
                    rodComponents.add(component);
                } else {
                    rodComponents.set(difference, component);
                }
            }
        }

        for (Direction.Axis axis : Iterate.axisSet) {
            Map<BlockPos, Pair<BlockPos, List<ReactorComponent>>> rodsOnAxis = this.reactorComponentRods.get(axis);
            for (Pair<BlockPos, List<ReactorComponent>> rod : rodsOnAxis.values()) {
                BlockPos rootPos = rod.getFirst();
                List<ReactorComponent> rodComponents = rod.getSecond();
                for (ListIterator<ReactorComponent> lister = rodComponents.listIterator(); lister.hasNext(); ) {
                    int index = lister.nextIndex();
                    ReactorComponent component = lister.next();
                    if (component != null)
                        continue;
                    BlockPos pos = rootPos.relative(axis, index);
                    lister.set(ReactorComponentRegistry.getReactorComponent(level, pos, level.getBlockState(pos)));
                }
            }
        }
    }

    protected Map<BlockPos, ReactorSimulationCellInfo> collectReactionData() {
        Map<BlockPos, ReactorSimulationCellInfo> map = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<BlockPos, ReactorComponent> entry : this.reactorComponents.entrySet()) {
            ReactorSimulationCellInfo data = ReactorSimulationCellInfo.empty();
            entry.getValue().setReactionData(data);
            map.put(entry.getKey(), data);
        }
        return map;
    }

    protected boolean isValidReactorBlock(ReactorPartBlockEntity be) {
        return true;//be instanceof GraphiteReactorBlockBlockEntity; // allow for other types later.
    }

	public void queueAddReactorBlock(LevelAccessor level, BlockPos pos) {
		if (this.blocks.contains(pos) || this.blocks.size() >= maximumReactorSize())
            return;
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof ReactorPartBlockEntity reactorBE) || !this.isValidReactorBlock(reactorBE))
            return; // TODO: change reactor handling to account for structural blocks
		this.changedBlocks.put(pos, true);
		reactorBE.setReactor(this);
	}

	public void queueRemoveReactorBlock(LevelAccessor level, BlockPos pos) {
		if (this.blocks.contains(pos))
            this.changedBlocks.put(pos, false);
	}

	public void queueReactorMerge(ReactorCore merging) {
		this.reactorsToMerge.add(merging);
	}

	public void handleBlockChanges(Level level) {
		if (this.changedBlocks.isEmpty() && this.reactorsToMerge.isEmpty()) {
			if (this.blocks.isEmpty())
                this.removed = true;
			return;
		}
		this.mergeReactors();

		Set<BlockPos> blockPool = new HashSet<>();

		this.addChangedBlocks(blockPool);

		this.blocks.clear();
		if (blockPool.isEmpty()) {
			this.removed = true;
			return;
		}
		this.setNewReactorBlocks(level, blockPool);
		for (ReactorCore reactor : this.reactorsToMerge) {
			reactor.blocks.removeAll(this.blocks);
			reactor.changedStructure = true;
		}
		blockPool.removeAll(this.blocks);
		this.splitReactors(level, blockPool);

		this.changedBlocks.clear();
		this.reactorsToMerge.clear();

		this.changedStructure = true;
		if (this.blocks.isEmpty())
            this.removed = true;
	}

	protected void mergeReactors() {
		Set<ReactorCore> reactorsVisited = new HashSet<>();
		Queue<ReactorCore> reactorFrontier = new UniqueLinkedList<>();
		reactorFrontier.addAll(this.reactorsToMerge);
		int MAX_MERGES = maximumReactorsMergable();
		for (int p = 0; p < MAX_MERGES; ++p) {
			ReactorCore next = reactorFrontier.poll();
			if (next == null)
                break;
			reactorsVisited.add(next);
			for (BlockPos pos : next.blocks)
                this.changedBlocks.put(pos, true);
			this.changedBlocks.putAll(next.changedBlocks);
			for (ReactorCore toMerge : next.reactorsToMerge)
				if (!reactorsVisited.contains(toMerge)) reactorFrontier.add(toMerge);
		}
	}

	protected void addChangedBlocks(Set<BlockPos> blockPool) {
		for (Map.Entry<BlockPos, Boolean> entry : this.changedBlocks.entrySet()) {
			BlockPos pos = entry.getKey();
			if (entry.getValue()) {
                blockPool.add(pos);
            } else {
                this.blocks.remove(pos);
            }
		}
		blockPool.addAll(this.blocks);
	}

	protected void setNewReactorBlocks(Level level, Set<BlockPos> blockPool) {
		Queue<BlockPos> blockFrontier = new UniqueLinkedList<>();
		blockFrontier.add(blockPool.iterator().next());

		int MAX_BLOCKS = maximumReactorSize();
		int op = 0;
		for (int p = 0; p < MAX_BLOCKS; ++p, ++op) {
			BlockPos next = blockFrontier.poll();
			if (next == null)
                break;
			BlockEntity be = level.getBlockEntity(next);
			if (!(be instanceof ReactorPartBlockEntity reactorBE) || !this.isValidReactorBlock(reactorBE))
                continue; // TODO: change reactor handling to account for structural blocks
			reactorBE.setReactor(this);
			this.blocks.add(next);
			for (Direction dir : Iterate.directions) {
				BlockPos newPos = next.relative(dir);
				if (this.blocks.contains(newPos) || !blockPool.contains(newPos))
                    continue;
				blockFrontier.add(newPos);
			}
		}
	}

	protected void splitReactors(Level level, Set<BlockPos> blockPool) {
		int MAX_SPLITS = maximumReactorsSplittable();
		int MAX_SIZE = maximumReactorSize();

		for (int p = 0; p < MAX_SPLITS; ++p) {
			Iterator<BlockPos> oneTimeIterator = blockPool.iterator();
			if (!oneTimeIterator.hasNext())
                break;
			BlockPos startPos = oneTimeIterator.next();
			blockPool.remove(startPos);
			ReactorCore newReactor = CreateMOAR.REACTOR_MANAGER.queueNewReactor(level, startPos);

			Queue<BlockPos> frontier = new UniqueLinkedList<>();
			frontier.add(startPos);
			for (int m = 0; m < MAX_SIZE; ++m) {
				BlockPos next = frontier.poll();
				if (next == null)
                    continue;
				newReactor.queueAddReactorBlock(level, next);
				for (Direction dir : Iterate.directions) {
					BlockPos newPos = next.relative(dir);
					if (newReactor.changedBlocks.containsKey(newPos) || !blockPool.contains(newPos))
                        continue;
					frontier.add(newPos);
				}
			}
			blockPool.removeAll(newReactor.changedBlocks.keySet());
		}
	}

	public boolean removed() { return this.removed; }
	public UUID getUUID() { return this.uuid; }

	public CompoundTag writeNbt(CompoundTag tag) {
		ListTag blockList = new ListTag();
		for (BlockPos pos : this.blocks) {
			blockList.add(new IntArrayTag(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
		}
		tag.put("Blocks", blockList);

		if (this.bounds != null) {
			ListTag boundsList = new ListTag();
			boundsList.add(DoubleTag.valueOf(this.bounds.minX));
			boundsList.add(DoubleTag.valueOf(this.bounds.minY));
			boundsList.add(DoubleTag.valueOf(this.bounds.minZ));
			boundsList.add(DoubleTag.valueOf(this.bounds.maxX));
			boundsList.add(DoubleTag.valueOf(this.bounds.maxY));
			boundsList.add(DoubleTag.valueOf(this.bounds.maxZ));
			tag.put("Bounds", boundsList);
		}

		if (this.dimension != null) {
			tag.putString("Dimension", this.dimension.location().toString());
		}

		return tag;
	}

	public static ReactorCore fromNbt(CompoundTag tag, UUID uuid) {
		ReactorCore core = new ReactorCore(uuid);

		ListTag blockList = tag.getList("Blocks", Tag.TAG_INT_ARRAY);
		for (int i = 0; i < blockList.size(); ++i) {
			int[] pos = blockList.getIntArray(i);
			if (pos.length != 3)
                continue;
			core.blocks.add(new BlockPos(pos[0], pos[1], pos[2]));
		}

		if (tag.contains("Bounds", Tag.TAG_LIST)) {
			ListTag boundsList = tag.getList("Bounds", Tag.TAG_DOUBLE);
			if (boundsList.size() == 6) {
				core.bounds = new AABB(boundsList.getDouble(0), boundsList.getDouble(1),
					boundsList.getDouble(2), boundsList.getDouble(3),
					boundsList.getDouble(4), boundsList.getDouble(5));
			}
		}

		if (tag.contains("Dimension", Tag.TAG_STRING)) {
			core.dimension = ResourceKey.create(Registries.DIMENSION, MOARUtils.location(tag.getString("Dimension")));
		}

        core.changedStructure = true;
		return core;
	}

	// TODO: config
	public static int maximumReactorSize() {
		return 10000;
	}

	public static int maximumReactorsMergable() {
		return 1000;
	}

	public static int maximumReactorsSplittable() {
		return 1000;
	}

}
