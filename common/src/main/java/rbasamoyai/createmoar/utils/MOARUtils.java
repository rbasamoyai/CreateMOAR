package rbasamoyai.createmoar.utils;

import net.minecraft.resources.ResourceLocation;

public class MOARUtils {

    public static ResourceLocation location(String id) { return new ResourceLocation(id); }

    public static ResourceLocation location(String namespace, String path) { return new ResourceLocation(namespace, path); }

    private MOARUtils() {}

}
