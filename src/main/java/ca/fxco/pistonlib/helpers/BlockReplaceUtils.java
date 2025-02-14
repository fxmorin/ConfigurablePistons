package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Function;

@UtilityClass
public class BlockReplaceUtils {

    public static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{
            Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP
    };

    // [VanillaCopy] - net.minecraft.world.level.Level#setBlock()
    /**
     * Same as {@link net.minecraft.world.level.Level#setBlock setBlock} however when Block.UPDATE_KNOWN_SHAPE is used
     * than it will only give shape updates to blocks that match the condition
     */
    public static boolean setBlockWithConditionalShapeUpdates(Level level, BlockPos blockPos, BlockState blockState,
                                                              int i, Function<BlockState, Boolean> canUpdate) {
        if (level.isOutsideBuildHeight(blockPos)) {
            return false;
        }
        LevelChunk levelChunk = level.getChunkAt(blockPos);
        Block block = blockState.getBlock();
        BlockState blockState2 = levelChunk.setBlockState(blockPos, blockState, (i & 64) != 0);
        if (blockState2 == null) {
            return false;
        }
        BlockState blockState3 = level.getBlockState(blockPos);
        if ((i & 128) == 0 && blockState3 != blockState2 &&
                (blockState3.getLightBlock() != blockState2.getLightBlock() ||
                        blockState3.getLightEmission() != blockState2.getLightEmission() ||
                        blockState3.useShapeForLightOcclusion() || blockState2.useShapeForLightOcclusion())) {

            Profiler.get().push("queueCheckLight");
            level.getChunkSource().getLightEngine().checkBlock(blockPos);
            Profiler.get().pop();
        }

        if (blockState3 == blockState) {
            if (blockState2 != blockState3) {
                level.setBlocksDirty(blockPos, blockState2, blockState3);
            }

            if ((i & 2) != 0 && (!level.isClientSide || (i & 4) == 0) &&
                    (level.isClientSide || levelChunk.getFullStatus() != null &&
                            levelChunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
                level.sendBlockUpdated(blockPos, blockState2, blockState, i);
            }

            if ((i & 1) != 0) {
                level.blockUpdated(blockPos, blockState2.getBlock());
                if (!level.isClientSide && blockState.hasAnalogOutputSignal()) {
                    level.updateNeighbourForOutputSignal(blockPos, block);
                }
            }

            // PistonLib Start
            int j = 511; // 512 - 1
            int k = i & -34;
            if ((i & 16) == 0) { // Only if Block.UPDATE_KNOWN_SHAPE is not used
                blockState2.updateIndirectNeighbourShapes(level, blockPos, k, j);
                blockState.updateNeighbourShapes(level, blockPos, k, j);
                blockState.updateIndirectNeighbourShapes(level, blockPos, k, j);
            } else { // If Block.UPDATE_KNOWN_SHAPE is used
                conditionallyUpdateNeighbourShapes(blockState, level, blockPos, k, j, canUpdate);
            }
            // PistonLib End

            level.onBlockStateChange(blockPos, blockState2, blockState3);
        }
        return true;
    }

    // [VanillaCopy] - net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#updateNeighbourShapes()
    /**
     * Same as {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#updateNeighbourShapes updateNeighbourShapes}
     * however it only does neighborUpdates on blocks that match the condition
     */
    public void conditionallyUpdateNeighbourShapes(BlockState state, LevelAccessor level, BlockPos blockPos,
                                                   int i, int j, Function<BlockState, Boolean> canUpdate) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : UPDATE_SHAPE_ORDER) {
            mutableBlockPos.setWithOffset(blockPos, direction);
            if (canUpdate.apply(level.getBlockState(mutableBlockPos))) { // Do conditional check
                level.neighborShapeChanged(direction.getOpposite(), mutableBlockPos, blockPos, state, i, j);
            }
        }
    }

    /**
     * Same as {@link net.minecraft.world.level.block.Block#updateFromNeighbourShapes updateFromNeighbourShapes}
     * however it allows BasicMovingBlockEntity's that are about to be placed to use their internal state.
     * This allows you to move blocks that usually break from incorrect shape updates,
     * as long as the blocks are moved together.
     */
    public static BlockState updateFromNeighbourShapesWithMovingPistons(BlockState blockState,
                                                                        LevelReader levelReader,
                                                                        ScheduledTickAccess tickAccess,
                                                                        BlockPos blockPos, RandomSource random) {
        BlockState finalState = blockState;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : UPDATE_SHAPE_ORDER) {
            mutableBlockPos.setWithOffset(blockPos, direction);
            BlockState neighborState = levelReader.getBlockState(mutableBlockPos);
            if (neighborState.is(ModTags.MOVING_PISTONS)) {
                BlockEntity blockEntity = levelReader.getBlockEntity(mutableBlockPos);
                if (blockEntity instanceof PistonMovingBlockEntity bmbe && bmbe.progress >= 1.0F) {
                    neighborState = bmbe.getMovedState();
                }
            }
            finalState = finalState.updateShape(levelReader, tickAccess,
                    blockPos, direction, mutableBlockPos, neighborState, random);
        }

        return finalState;
    }
}
