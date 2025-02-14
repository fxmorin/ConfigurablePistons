package ca.fxco.pistonlib.mixin.fixes;

import ca.fxco.pistonlib.PistonLibConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Fixes being able to push blocks through the world border
 *
 * @author FX
 */
@Mixin(PistonBaseBlock.class)
public class PistonBlock_worldBorderMixin {

    @WrapOperation(
            method = "isPushable",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/border/WorldBorder;" +
                            "isWithinBounds(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private static boolean pl$customWorldBorderCheck(WorldBorder instance, BlockPos pos, Operation<Boolean> original,
                                                     BlockState state, Level level, BlockPos pos2, Direction dir) {
        if (PistonLibConfig.pushThroughWorldBorderFix) {
            return instance.isWithinBounds(pos.relative(dir));
        }
        return original.call(instance, pos);
    }
}
