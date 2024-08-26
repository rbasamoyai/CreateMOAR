package rbasamoyai.createmoar.index;

import java.util.function.Supplier;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.createmoar.CreateMOAR;

public class ModGroup {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = makeKey("base");

    public static final Supplier<CreativeModeTab> GROUP = wrapGroup("base", () -> createBuilder()
        .title(Component.translatable("itemGroup." + CreateMOAR.MOD_ID))
        .icon(MOARBlocks.GRAPHITE_REACTOR_BLOCK::asStack)
        .displayItems((param, output) -> {
            output.accept(MOARBlocks.GRAPHITE_REACTOR_BLOCK.get());
            output.accept(MOARBlocks.FUEL_BUNDLE.get());
        }).build());

    @ExpectPlatform public static Supplier<CreativeModeTab> wrapGroup(String id, Supplier<CreativeModeTab> sup) { throw new AssertionError(); }
    @ExpectPlatform public static CreativeModeTab.Builder createBuilder() { throw new AssertionError(); }

    @ExpectPlatform public static void useModTab(ResourceKey<CreativeModeTab> key) { throw new AssertionError(); }

    public static ResourceKey<CreativeModeTab> makeKey(String id) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateMOAR.path(id));
    }

    public static void register() {
        CreateMOAR.REGISTRATE.addRawLang("itemGroup." + CreateMOAR.MOD_ID, "Create: Mother of All Reactors");
    }

}
