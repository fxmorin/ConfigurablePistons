package ca.fxco.pistonlib.mixin.stuck;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import ca.fxco.pistonlib.base.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.minecraft.world.level.block.DoorBlock.HALF;

@Mixin(DoorBlock.class)
public abstract class DoorBlock_stuckMixin extends Block {

    public DoorBlock_stuckMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "updateShape",
            at = @At("RETURN"),
            cancellable = true
    )
    private void pl$lookWithinMovingPistons(BlockState blockState, LevelReader levelReader,
                                            ScheduledTickAccess tickAccess, BlockPos blockPos, Direction dir,
                                            BlockPos blockPos2, BlockState blockState2, RandomSource random,
                                            CallbackInfoReturnable<BlockState> cir) {
        if (cir.getReturnValue().isAir() && blockState2.is(ModTags.MOVING_PISTONS)) {
            BlockEntity entity = levelReader.getBlockEntity(blockPos2);
            if (entity instanceof PistonMovingBlockEntity mpbe && mpbe.progress >= 1.0F) {
                cir.setReturnValue(updateShape(blockState, levelReader,
                        tickAccess, blockPos, dir, blockPos2, mpbe.movedState, random));
            }
        }
    }

    @Override
    public boolean pl$usesConfigurablePistonBehavior() {
        return PistonLibConfig.stuckDoubleBlocks;
    }

    @Override
    public boolean pl$usesConfigurablePistonStickiness() {
        return PistonLibConfig.stuckDoubleBlocks;
    }

    @Override
    public Map<Direction, StickyType> pl$stickySides(BlockState state) {
        return Map.of(pl$getStickyDirection(state), StickyType.CONDITIONAL);
    }

    @Override
    public StickyType pl$sideStickiness(BlockState state, Direction dir) {
        return dir == pl$getStickyDirection(state) ? StickyType.CONDITIONAL : StickyType.DEFAULT;
    }

    @Override
    public boolean pl$matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        return state.is(neighborState.getBlock()) && pl$getStickyDirection(neighborState) == dir.getOpposite();
    }

    @Unique
    private static Direction pl$getStickyDirection(BlockState state) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        return doubleBlockHalf == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN;
    }
}
