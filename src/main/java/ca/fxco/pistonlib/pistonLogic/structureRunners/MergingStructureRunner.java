package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.impl.BlockEntityMerging;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.internal.BlockStateBaseMerging;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.MergingPistonStructureResolver;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.level.block.Block.*;
import static net.minecraft.world.level.block.Block.UPDATE_INVISIBLE;

public class MergingStructureRunner extends BasicStructureRunner {

    private final Map<BlockPos, BlockState> toKeep = new LinkedHashMap<>();
    private BlockState[] unMergingStates;
    private int unMergingIndex = 0;

    public MergingStructureRunner(PistonFamily family, PistonType type) {
        super(family, type);
    }

    @Override
    protected void taskSetPositionsToMove(Level level, List<BlockPos> toMove, Direction moveDir) {
        for (BlockPos posToMove : toMove) {
            BlockState stateToMove = level.getBlockState(posToMove);
            BlockEntity blockEntityToMove = level.getBlockEntity(posToMove);

            if (blockEntityToMove != null) {
                if (!(blockEntityToMove instanceof BlockEntityMerging bem) || bem.shouldUnMergeBlockEntity(stateToMove, moveDir)) {
                    level.removeBlockEntity(posToMove);
                    blockEntityToMove.setChanged();
                }
            }

            statesToMove.add(stateToMove);
            blockEntitiesToMove.add(blockEntityToMove);
            toRemove.put(posToMove, stateToMove);
        }
    }

    @Override
    protected void taskMoveBlocks(Level level, BlockPos pos, PistonStructureResolver structure, Direction facing,
                                  boolean extend, List<BlockPos> toMove, BlockState[] affectedStates,
                                  AtomicInteger affectedIndex, Direction moveDir) {
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos posToMove = toMove.get(i);
            BlockPos dstPos = posToMove.relative(moveDir);
            BlockState stateToMove = statesToMove.get(i);
            BlockEntity blockEntityToMove = blockEntitiesToMove.get(i);

            boolean move = true;

            // UnMerge blocks
            if (structure instanceof MergingPistonStructureResolver mergingStructure) {
                List<BlockPos> toUnMerge = mergingStructure.getToUnMerge();
                unMergingStates = new BlockState[toUnMerge.size()];

                if (toUnMerge.contains(posToMove) && stateToMove instanceof BlockStateBaseMerging bsbm) {
                    Pair<BlockState, BlockState> unmergedStates = null;
                    if (bsbm.getBlockEntityMergeRules().checkUnMerge() &&
                            blockEntityToMove instanceof BlockEntityMerging bem) {
                        unmergedStates = bem.doUnMerge(stateToMove, moveDir);
                        if (!bem.shouldUnMergeBlockEntity(stateToMove, moveDir)) {
                            blockEntityToMove = null;
                        }
                    }
                    if (unmergedStates == null) {
                        unmergedStates = bsbm.doUnMerge(level, posToMove, moveDir);
                    }
                    if (unmergedStates != null) {
                        unMergingStates[unMergingIndex++] = stateToMove;
                        stateToMove = unmergedStates.getFirst();
                        BlockState stateToKeep = unmergedStates.getSecond();
                        toKeep.put(posToMove, stateToKeep);
                        affectedStates[affectedIndex.getAndIncrement()] = stateToKeep;
                        toRemove.remove(posToMove);
                        move = false;
                    }
                }
            }

            toRemove.remove(dstPos);

            BlockState movingBlock = this.family.getMoving().defaultBlockState()
                    .setValue(BasicMovingBlock.FACING, facing);
            BlockEntity movingBlockEntity = this.family
                    .newMovingBlockEntity(dstPos, movingBlock, stateToMove, blockEntityToMove, facing, extend, false);

            level.setBlock(dstPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBlockEntity);

            if (move) {
                affectedStates[affectedIndex.getAndIncrement()] = stateToMove;
            }
        }
    }

    @Override
    protected void taskMergeBlocks(Level level, BlockPos pos, Direction facing, boolean extend,
                                   MergingPistonStructureResolver structure, Direction moveDir) {
        List<BlockPos> toMerge = structure.getToMerge();
        float speed = extend ? family.getExtendingSpeed() : family.getRetractingSpeed();

        // Merge Blocks
        for (int i = toMerge.size() - 1; i >= 0; i--) {
            BlockPos posToMerge = toMerge.get(i);
            BlockState stateToMerge = level.getBlockState(posToMerge);

            BlockPos mergeIntoPos = posToMerge.relative(moveDir);
            BlockState mergeIntoState = level.getBlockState(mergeIntoPos);

            if (mergeIntoState.getBlock() instanceof MergeBlock) { // MultiMerge
                if (level.getBlockEntity(mergeIntoPos) instanceof MergeBlockEntity mergeBlockEntity) {
                    if (((BlockStateBaseMerging)stateToMerge).getBlockEntityMergeRules().checkMerge() &&
                            mergeBlockEntity.getInitialBlockEntity() != null) {
                        BlockEntity blockEntityToMerge = level.getBlockEntity(posToMerge);
                        if (blockEntityToMerge instanceof BlockEntityMerging bem2 &&
                                bem2.shouldStoreSelf(mergeBlockEntity)) {
                            bem2.onMerge(mergeBlockEntity, moveDir);
                            mergeBlockEntity.doMerge(stateToMerge, blockEntityToMerge, moveDir, speed);
                        } else {
                            mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);
                        }
                    } else {
                        mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);
                    }
                }
            } else {
                BlockState mergeBlockState = ModBlocks.MERGE_BLOCK.defaultBlockState();
                MergeBlockEntity mergeBlockEntity;
                BlockEntity mergeIntoBlockEntity = level.getBlockEntity(mergeIntoPos);
                if (mergeIntoBlockEntity instanceof BlockEntityMerging bem && bem.doInitialMerging()) {
                    mergeBlockEntity = new MergeBlockEntity(mergeIntoPos, mergeBlockState, mergeIntoState, mergeIntoBlockEntity);
                    bem.onMerge(mergeBlockEntity, moveDir); // Call onMerge for the base block entity

                    if (((BlockStateBaseMerging)stateToMerge).getBlockEntityMergeRules().checkMerge()) {
                        BlockEntity blockEntityToMerge = level.getBlockEntity(posToMerge);
                        if (blockEntityToMerge instanceof BlockEntityMerging bem2 &&
                                bem2.shouldStoreSelf(mergeBlockEntity)) {
                            bem2.onMerge(mergeBlockEntity, moveDir);
                            mergeBlockEntity.doMerge(stateToMerge, blockEntityToMerge, moveDir, speed);
                        } else {
                            mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);
                        }
                    } else {
                        mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);
                    }
                } else {
                    mergeBlockEntity = new MergeBlockEntity(mergeIntoPos, mergeBlockState, mergeIntoState);
                    mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);
                }

                level.setBlock(posToMerge, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);

                level.setBlock(mergeIntoPos, mergeBlockState, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
                level.setBlockEntity(mergeBlockEntity);
            }
        }
    }

    @Override
    protected void taskDoUnMergeUpdates(Level level) {
        int unMergingIndex = 0;

        // Keep these blocks as they unmerged, just change there state to the new one
        for (Map.Entry<BlockPos, BlockState> entry : toKeep.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
        }

        // Do neighbor updates at the unmerged positions once all the blocks have been changed
        for (Map.Entry<BlockPos, BlockState> entry : toKeep.entrySet()) {
            BlockPos keepPos = entry.getKey();
            BlockState keepState = entry.getValue();
            BlockState lastState = unMergingStates[unMergingIndex++];

            lastState.updateIndirectNeighbourShapes(level, keepPos, UPDATE_CLIENTS);
            keepState.updateNeighbourShapes(level, keepPos, UPDATE_CLIENTS);
            keepState.updateIndirectNeighbourShapes(level, keepPos, UPDATE_CLIENTS);
        }
    }
}
