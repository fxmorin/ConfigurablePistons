package ca.fxco.pistonlib.mixin.blockEntity;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Post Load block entities once before they tick.
 * This allows the block entities to run code first while only running this logic once
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevel_blockEntityMixin extends Level {

    // constructor only needed to make the compiler happy
    private ServerLevel_blockEntityMixin(WritableLevelData data, ResourceKey<Level> key, RegistryAccess registryAccess,
                                         Holder<DimensionType> dimension,
                                         boolean isClientSide, boolean isDebug, long seed,
                                         int maxChainedNeighborUpdates) {
        super(data, key, registryAccess, dimension, isClientSide, isDebug, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private final Set<BlockEntity> blockEntitiesToPostLoad = new HashSet<>();

    @Override
    public void pl$addBlockEntityPostLoad(BlockEntity blockEntity) {
        this.blockEntitiesToPostLoad.add(blockEntity);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;tickTime()V"
            )
    )
    private void pl$beforeBlockEntityTicking(CallbackInfo ci) {
        for (Iterator<BlockEntity> it = this.blockEntitiesToPostLoad.iterator(); it.hasNext(); it.remove()) {
            it.next().pl$onPostLoad();
        }
    }
}
