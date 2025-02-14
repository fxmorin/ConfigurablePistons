package ca.fxco.pistonlib.mixin.blockEntity;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelChunk.class)
public class LevelChunk_blockEntityMixin {

    @Shadow
    public Level getLevel() { return null; }

    @WrapWithCondition(
            method = "addAndRegisterBlockEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;" +
                            "updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
            )
    )
    private boolean pl$conditionallyAddTickers$1(LevelChunk level, BlockEntity blockEntity) {
        if (blockEntity.pl$shouldPostLoad()) {
            this.getLevel().pl$addBlockEntityPostLoad(blockEntity);
        }
        if (blockEntity instanceof BasicMovingBlockEntity bmbe) {
            return bmbe.hasControl();
        }
        return true;
    }

    @WrapWithCondition(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;" +
                            "updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
            )
    )
    private boolean pl$conditionallyAddTickers$2(LevelChunk level, BlockEntity blockEntity) {
        if (blockEntity.pl$shouldPostLoad()) {
            this.getLevel().pl$addBlockEntityPostLoad(blockEntity);
        }
        if (blockEntity instanceof BasicMovingBlockEntity bmbe) {
            return bmbe.hasControl();
        }
        return true;
    }

    @WrapWithCondition(
            method = "method_32920(Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;" +
                            "updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
            )
    )
    private boolean pl$conditionallyAddTickers$3(LevelChunk level, BlockEntity blockEntity) {
        if (blockEntity.pl$shouldPostLoad()) {
            this.getLevel().pl$addBlockEntityPostLoad(blockEntity);
        }
        if (blockEntity instanceof BasicMovingBlockEntity bmbe) {
            return bmbe.hasControl();
        }
        return true;
    }
}
