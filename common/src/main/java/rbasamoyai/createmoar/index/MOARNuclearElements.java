package rbasamoyai.createmoar.index;

import rbasamoyai.createmoar.CreateMOAR;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;
import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElementsHandler;

public class MOARNuclearElements {

    public static final NuclearElement
    //////// Structural elements ////////
        GRAPHITE = register("graphite"),
        IRON_56 = register("iron_56"),

    //////// Fuel elements ////////
        URANIUM_235 = register("uranium_235"),
        URANIUM_238 = register("uranium_238"),

    //////// Decay products ////////
        IODINE_135 = register("iodine_135"),
        XENON_135 = register("xenon_135");

    private static NuclearElement register(String id) {
        return NuclearElementsHandler.registerDefaultElement(CreateMOAR.path(id));
    }

    public static void register() {}

}
