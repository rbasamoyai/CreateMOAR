package rbasamoyai.createmoar.foundation.reactors.construction.components;

import java.util.Objects;

import javax.annotation.Nonnull;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorComponentRegistry {

    private static final Reference2ReferenceMap<Block, ReactorComponent.Provider> PROVIDERS = new Reference2ReferenceOpenHashMap<>();

    public static void registerProvider(Block block, ReactorComponent.Provider provider) {
        Objects.requireNonNull(block);
        Objects.requireNonNull(provider);
        if (PROVIDERS.put(block, provider) != null)
            throw new IllegalStateException("Reactor component provider already registered for block " + BuiltInRegistries.BLOCK.getId(block));
    }

    @Nonnull
    public static ReactorComponent getReactorComponent(Level level, BlockPos pos, BlockState blockState) {
        return PROVIDERS.getOrDefault(blockState.getBlock(), ReactorComponent.Provider.DEFAULT).createReactorComponent(level, pos, blockState);
    }

}
