package ca.fxco.pistonlib.mixin.stuck;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import ca.fxco.pistonlib.base.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.minecraft.world.level.block.BedBlock.PART;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@Mixin(BedBlock.class)
public abstract class BedBlock_stuckMixin extends Block {

    @Shadow
    protected abstract @NotNull BlockState updateShape(BlockState blockState, LevelReader levelReader,
                                                       ScheduledTickAccess tickAccess,
                                                       BlockPos blockPos, Direction dir, BlockPos blockPos2,
                                                       BlockState blockState2, RandomSource random);

    public BedBlock_stuckMixin(Properties properties) {
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
        return Map.of(pl$getNeighbourDirection(state), StickyType.CONDITIONAL);
    }

    @Override
    public StickyType pl$sideStickiness(BlockState state, Direction dir) {
        return dir == pl$getNeighbourDirection(state) ? StickyType.CONDITIONAL : StickyType.DEFAULT;
    }

    @Override
    public boolean pl$matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        return state.is(neighborState.getBlock()) && pl$getNeighbourDirection(neighborState) == dir.getOpposite();
    }

    @Unique
    private static Direction pl$getNeighbourDirection(BlockState state) {
        BedPart bedPart = state.getValue(PART);
        Direction direction = state.getValue(FACING);
        return bedPart == BedPart.FOOT ? direction : direction.getOpposite();
    }
}
