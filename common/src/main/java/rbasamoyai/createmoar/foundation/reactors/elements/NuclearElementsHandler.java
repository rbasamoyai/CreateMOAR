package rbasamoyai.createmoar.foundation.reactors.elements;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import rbasamoyai.createmoar.CreateMOAR;

public class NuclearElementsHandler {

    private static final Map<ResourceLocation, NuclearElement> BUILT_IN_ELEMENTS = new Object2ObjectOpenHashMap<>();
	private static final Map<ResourceLocation, NuclearElement> NUCLEAR_ELEMENTS = new Object2ObjectOpenHashMap<>();

	// Resembles iron-56
	private static final NuclearElement DEFAULT = new NuclearElement(CreateMOAR.path("default"), 56, 1, false, false, null, null, null, null, null);

	public static class ReloadListener extends SimpleJsonResourceReloadListener {
		private static final Gson GSON = new Gson();
		public static final ReloadListener INSTANCE = new ReloadListener();

		public ReloadListener() { super(GSON, "nuclear_elements"); }

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
            for (Iterator<Map.Entry<ResourceLocation, NuclearElement>> iter = NUCLEAR_ELEMENTS.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<ResourceLocation, NuclearElement> entry = iter.next();
                if (map.containsKey(entry.getKey()))
                    continue;
                entry.getValue().invalidate();
                iter.remove();
            }
            for (ResourceLocation loc : map.keySet()) {
                if (!BUILT_IN_ELEMENTS.containsKey(loc) && !NUCLEAR_ELEMENTS.containsKey(loc))
                    NUCLEAR_ELEMENTS.put(loc, new NuclearElement(loc));
            }
			for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
				JsonElement el = entry.getValue();
				if (!el.isJsonObject())
                    continue;
				try {
                    ResourceLocation loc = entry.getKey();
                    NuclearElement element = BUILT_IN_ELEMENTS.containsKey(loc) ? BUILT_IN_ELEMENTS.get(loc) : NUCLEAR_ELEMENTS.get(loc);
                    element.readJson(el.getAsJsonObject(), loc.toString());
				} catch (Exception e) {

				}
			}
		}
	}

    /**
     * @param loc the id of the element
     * @return an empty nuclear element. This should be complimented with a data pack value.
     */
    public static NuclearElement registerDefaultElement(ResourceLocation loc) {
        if (BUILT_IN_ELEMENTS.containsKey(loc))
            throw new IllegalStateException("Default nuclear element " + loc + " already registered");
        NuclearElement element = new NuclearElement(loc);
        BUILT_IN_ELEMENTS.put(loc, element);
        return element;
    }

	public static NuclearElement get(ResourceLocation loc) {
        if (BUILT_IN_ELEMENTS.containsKey(loc))
            return BUILT_IN_ELEMENTS.get(loc);
		return NUCLEAR_ELEMENTS.computeIfAbsent(loc, l -> {
			CreateMOAR.LOGGER.warn("No properties for element '{}' found, setting to default", l);
			return DEFAULT;
		});
	}

}
