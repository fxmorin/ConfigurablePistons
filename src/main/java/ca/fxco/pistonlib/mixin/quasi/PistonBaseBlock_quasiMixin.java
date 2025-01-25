package ca.fxco.pistonlib.mixin.quasi;

import ca.fxco.pistonlib.helpers.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlock_quasiMixin {

    /**
     * @author FX
     * @reason The code was hideous and needs to be cleaned, plus I needed to add quasi
     */
    @Overwrite
    private boolean getNeighborSignal(SignalGetter signalGetter, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(signalGetter, pos, facing) ||
                signalGetter.pl$hasQuasiNeighborSignal(pos, 1);
    }
}
