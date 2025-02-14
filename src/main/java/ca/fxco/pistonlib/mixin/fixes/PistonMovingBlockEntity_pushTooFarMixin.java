package ca.fxco.pistonlib.mixin.fixes;

import ca.fxco.pistonlib.PistonLibConfig;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes pistons pushing entities 0.01 too far instead of an exact block
 *
 * @author FX
 */
@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntity_pushTooFarMixin {

    @Inject(
            method = "moveCollidedEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/shapes/VoxelShape;toAabbs()Ljava/util/List;"
            )
    )
    private static void pl$isFinalTick(Level level, BlockPos pos, float f, PistonMovingBlockEntity be,
                                       CallbackInfo ci, @Share("finalTick") LocalBooleanRef finalTickRef) {
        finalTickRef.set(PistonLibConfig.pistonsPushTooFarFix && f >= 1.0);
    }


    @ModifyConstant(
            method = "moveCollidedEntities",
            constant = @Constant(doubleValue = 0.01)
    )
    private static double pl$dontPushOffsetLastTick(double constant,
                                                    @Share("finalTick") LocalBooleanRef finalTickRef) {
        return finalTickRef.get() ? 0.0 : constant;
    }
}
