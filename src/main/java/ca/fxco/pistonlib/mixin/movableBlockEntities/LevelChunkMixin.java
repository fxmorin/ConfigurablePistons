package ca.fxco.pistonlib.mixin.movableBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Shadow
    @Final
    Level level;

    @Inject(
        method = "setBlockState",
        at = @At(
            value = "FIELD",
            ordinal = 1,
            target = "Lnet/minecraft/world/level/Level;isClientSide:Z"
        )
    )
    private void pl$placeMovedBlockEntity(BlockPos pos, BlockState state, boolean movedByPiston,
                                          CallbackInfoReturnable<BlockState> cir) {
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = this.level.pl$getBlockEntityForPlacement(pos, state);

            if (blockEntity != null) {
                ((LevelChunk)(Object)this).addAndRegisterBlockEntity(blockEntity);
            }
        }
    }
}
