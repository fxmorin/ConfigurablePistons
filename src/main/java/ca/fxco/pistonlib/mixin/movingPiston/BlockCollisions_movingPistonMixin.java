package ca.fxco.pistonlib.mixin.movingPiston;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import ca.fxco.pistonlib.base.ModTags;

import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockCollisions.class)
public class BlockCollisions_movingPistonMixin {

    @WrapOperation(
        method = "computeNext",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
        )
    )
    private boolean pl$allMovingPistons(BlockState instance, Block block, Operation<Boolean> original) {
        return instance.is(ModTags.MOVING_PISTONS);
    }
}
