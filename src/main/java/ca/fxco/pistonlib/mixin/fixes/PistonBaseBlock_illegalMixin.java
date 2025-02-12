package ca.fxco.pistonlib.mixin.fixes;

import ca.fxco.pistonlib.PistonLibConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Prevents pistons from being able to break blocks with a hardness value of -1.0F
 * You need headless pistons to be able to break these blocks using pistons.
 *
 * @author FX
 */
@Mixin(value = PistonBaseBlock.class, priority = 1020)
public class PistonBaseBlock_illegalMixin {

    @WrapWithCondition(
            method = "triggerEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"
            )
    )
    private boolean pl$preventRemovingIllegalBlocks(Level level, BlockPos pos, boolean move) {
        if (PistonLibConfig.illegalBreakingFix && level.getBlockState(pos).getDestroySpeed(level, pos) == -1.0F) {
            return false;
        }
        return level.removeBlock(pos, move);
    }
}
