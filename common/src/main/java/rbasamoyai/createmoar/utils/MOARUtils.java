package rbasamoyai.createmoar.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElementsHandler;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.MutableElementView;
import rbasamoyai.createmoar.foundation.reactors.elements.modification.NuclearElementView;

public class MOARUtils {

    public static ResourceLocation location(String id) { return new ResourceLocation(id); }

    public static ResourceLocation location(String namespace, String path) { return new ResourceLocation(namespace, path); }

    public static BlockPos getAxisPosition(BlockPos pos, Direction.Axis axis) {
        return pos.relative(axis, -pos.get(axis));
    }

    public static int getAxisDifference(BlockPos from, BlockPos to, Direction.Axis axis) {
        return switch (axis) {
            case X:
                yield to.getX() - from.getX();
            case Y:
                yield to.getY() - from.getY();
            case Z:
                yield to.getZ() - from.getZ();
        };
    }

    public static void loadElementsFromNbt(CompoundTag tag, Map<NuclearElement, Double> map) {
        for (String key : tag.getAllKeys()) {
            NuclearElement element = NuclearElementsHandler.get(MOARUtils.location(key));
            if (element != null && tag.get(key) instanceof NumericTag numeric)
                map.put(element, numeric.getAsDouble());
        }
    }

    public static void writeElementsToNbt(CompoundTag tag, Map<NuclearElement, Double> map) {
        for (Map.Entry<NuclearElement, Double> entry : map.entrySet())
            tag.putDouble(entry.getKey().id().toString(), entry.getValue());
    }

    public static void mergeElementNbt(CompoundTag tag, NuclearElement element, double change) {
        String key = element.id().toString();
        tag.putDouble(key, tag.getDouble(key) + change);
    }

    public static List<NuclearElementView> getFuelElements(Map<NuclearElement, Double> byVolume,
                                                           Map<NuclearElement, Double> byAmount, double volume,
                                                           BiConsumer<NuclearElement, Double> cons) {
        List<NuclearElementView> list = new ArrayList<>();
        for (Iterator<Map.Entry<NuclearElement, Double>> iter = byVolume.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<NuclearElement, Double> entry = iter.next();
            if (entry.getKey().isInvalid()) {
                iter.remove();
                continue;
            }
            list.add(MutableElementView.fromVolume(entry.getKey(), entry.getValue(), cons));
        }
        for (Iterator<Map.Entry<NuclearElement, Double>> iter = byAmount.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<NuclearElement, Double> entry = iter.next();
            if (entry.getKey().isInvalid()) {
                iter.remove();
                continue;
            }
            list.add(new MutableElementView(entry.getKey(), entry.getValue(), volume, cons));
        }
        return list;
    }

    private MOARUtils() {}

}
