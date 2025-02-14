package ca.fxco.pistonlib.mixin.fixes;

import ca.fxco.pistonlib.PistonLibConfig;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;

/**
 * Fixes the piston update order being locational
 *
 * @author FX
 */
@Mixin(PistonBaseBlock.class)
public class PistonBaseBlock_pushOrderMixin {

    @WrapOperation(
            method = "moveBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;",
                    remap = false
            )
    )
    private HashMap<BlockPos, BlockState> pl$fixLocationalHashmap(Operation<HashMap<BlockPos, BlockState>> original) {
        return PistonLibConfig.locationalUpdateOrderFix ? Maps.newLinkedHashMap() : original.call();
    }
}
