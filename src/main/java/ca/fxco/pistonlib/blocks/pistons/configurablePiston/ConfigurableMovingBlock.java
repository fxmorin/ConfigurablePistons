package ca.fxco.pistonlib.blocks.pistons.configurablePiston;

import ca.fxco.pistonlib.api.pistonLogic.controller.PistonController;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ca.fxco.pistonlib.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurableMovingBlock extends BasicMovingBlock {

    public ConfigurableMovingBlock() {
        this(BasicMovingBlock.createDefaultSettings());
    }

    public ConfigurableMovingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (this.getFamily().isExtendOnRetracting() && level.getBlockEntity(pos) instanceof BasicMovingBlockEntity movingBlockEntity) { //TODO: Merge into single statement
            if (movingBlockEntity.isSourcePiston && movingBlockEntity.movedState.getBlock() instanceof ConfigurablePistonBaseBlock cpbb) {
                if (!movingBlockEntity.isExtending()) {
                    Direction facing = movingBlockEntity.movedState.getValue(FACING);
                    PistonController pistonController = cpbb.pl$getPistonController();
                    if (pistonController.hasNeighborSignal(level, pos, facing)) {
                        float progress = movingBlockEntity.progress;
                        movingBlockEntity.finalTick(false, false);
                        Set<BlockPos> positions = new HashSet<>();
                        BlockPos frontPos = pos.relative(facing);
                        if (level.getBlockEntity(frontPos) instanceof ConfigurableMovingBlockEntity bmbe && !bmbe.extending && bmbe.progress == progress) {
                            if (bmbe.movedState.pl$usesConfigurablePistonStickiness() && bmbe.movedState.pl$isSticky()) {
                                stuckNeighbors(level, frontPos, bmbe.movedState.pl$stickySides(), bmbe, positions);
                            }
                            bmbe.finalTick();
                        }
                        //stuckNeighbors(level, pos.relative(facing), );
                        pistonController.checkIfExtend(level, pos, movingBlockEntity.movedState, false);
                        int progressInt = Float.floatToIntBits(1 - progress);
                        level.blockEvent(frontPos, this, 99, progressInt);
                        for (BlockPos pos9 : positions) {
                            level.blockEvent(pos9.relative(facing), this, 99, progressInt);
                        }
                    }
                }
            }
        }
    }

    private void stuckNeighbors(Level level, BlockPos pos, Map<Direction, StickyType> stickyTypes,
                                ConfigurableMovingBlockEntity thisMbe, Set<BlockPos> set) {
        for (Map.Entry<Direction, StickyType> entry : stickyTypes.entrySet()) {
            StickyType stickyType = entry.getValue();

            if (stickyType.ordinal() < StickyType.STRONG.ordinal()) { // only strong or fused
                continue;
            }

            Direction dir = entry.getKey();
            BlockPos neighborPos = pos.relative(dir);
            if (set.contains(neighborPos)) {
                continue;
            }
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.is(this.getFamily().getMoving())) {
                BlockEntity blockEntity = level.getBlockEntity(neighborPos);

                if (blockEntity instanceof ConfigurableMovingBlockEntity mbe) {
                    if (!mbe.isExtending() && thisMbe.progress == mbe.progress) {
                        set.add(neighborPos);
                        if (mbe.movedState.pl$usesConfigurablePistonStickiness() && mbe.movedState.pl$isSticky()) {
                            stuckNeighbors(level, neighborPos, mbe.movedState.pl$stickySides(), mbe, set);
                        }
                        mbe.finalTick(true, true);
                    }
                }
            }
        }
    }

    public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int type, int data) {
        if (type == 99 && level.getBlockEntity(blockPos) instanceof BasicMovingBlockEntity bmbe) {
            bmbe.progress = bmbe.progressO = Float.intBitsToFloat(data);
        }
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (this.getFamily().isSlippery() && !oldState.is(state.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) == null) {
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
                                  BlockPos pos, Direction dir, BlockPos neighborPos,
                                  BlockState neighborState, RandomSource random) {
        if (this.getFamily().isSlippery() && !level.isClientSide()) {
            scheduledTickAccess.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.getFamily().isSlippery()) {
            int i = BaseSlipperyBlock.calculateDistance(level, pos);
            BlockState blockState = state.setValue(SLIPPERY_DISTANCE, i);
            if (blockState.getValue(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
                FallingBlockEntity.fall(level, pos, blockState);
            } else if (state != blockState) {
                level.setBlock(pos, blockState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !this.getFamily().isSlippery() || BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SLIPPERY_DISTANCE);
    }
}
