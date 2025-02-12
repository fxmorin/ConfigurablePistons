package ca.fxco.pistonlib.mixin.quasi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public class DispenserBlock_quasiMixin {

    @WrapOperation(
            method = "neighborChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z",
                    ordinal = 1
            )
    )
    private boolean pl$useQuasiSignalCheck(Level instance, BlockPos pos, Operation<Boolean> original) {
        return instance.pl$hasQuasiNeighborSignal(pos.below(), 1);
    }
}
