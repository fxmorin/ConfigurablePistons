package ca.fxco.pistonlib.mixin.fixes;

import ca.fxco.pistonlib.PistonLibConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * Prevents Headless pistons from existing, since headless pistons are able to break any block in the game.
 * This fix should prevent being able to break most blocks such as bedrock!
 *
 * @author FX
 */
@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlock_headlessMixin {

    @Shadow
    @Final
    public static BooleanProperty EXTENDED;

    @Shadow
    @Final
    private boolean isSticky;

    @Shadow
    protected abstract boolean getNeighborSignal(SignalGetter signalGetter, BlockPos blockPos, Direction direction);

    @Inject(
            method = "checkIfExtend",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;getNeighborSignal(" +
                            "Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/core/Direction;)Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void pl$stopHeadlessPiston(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (PistonLibConfig.headlessPistonFix && state.getValue(EXTENDED)) {
            Direction direction = state.getValue(FACING);
            BlockState blockState = level.getBlockState(pos.relative(direction));
            if (this.getNeighborSignal(level, pos, direction) &&
                    !blockState.is(Blocks.MOVING_PISTON) &&
                    !blockState.is(Blocks.PISTON_HEAD)) {
                level.removeBlock(pos, false);
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX(), pos.getY(), pos.getZ(),
                        new ItemStack(this.isSticky ? Items.STICKY_PISTON : Items.PISTON)
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
                ci.cancel();
            }
        }
    }
}
