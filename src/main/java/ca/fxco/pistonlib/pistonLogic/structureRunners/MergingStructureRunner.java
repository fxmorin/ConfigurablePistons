package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.api.PistonLibApi;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureGroup;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureResolver;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
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

import static net.minecraft.world.level.block.Block.*;
import static net.minecraft.world.level.block.Block.UPDATE_INVISIBLE;

public class MergingStructureRunner extends BasicStructureRunner {

    private Map<BlockPos, BlockState> toKeep;
    private BlockState[] unMergingStates;
    private int unMergingIndex = 0;

    public <S extends PistonStructureResolver & StructureResolver> MergingStructureRunner(
            Level level, BlockPos pos, Direction facing, int length, PistonFamily family, PistonType type,
            boolean extend, StructureResolver.Factory<S> structureProvider
    ) {
        super(level, pos, facing, length, family, type, extend, structureProvider);
    }

    @Override
    public boolean taskRunStructureResolver() {
        if (!super.taskRunStructureResolver()) {
            return false;
        }
        this.toKeep = new LinkedHashMap<>();
        return true;
    }

    @Override
    public void taskSetPositionsToMove() {
        for (BlockPos posToMove : toMove) {
            BlockState stateToMove = level.getBlockState(posToMove);
            BlockEntity blockEntityToMove = level.getBlockEntity(posToMove);

            if (blockEntityToMove != null) {
                if (blockEntityToMove.pl$shouldUnMergeBlockEntity(stateToMove, moveDir)) {
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
    public void taskMoveBlocks() {
        int moveSize = toMove.size();
        if (moveSize > 0) {
            if (structure instanceof MergingPistonStructureResolver mergingStructure) {
                Map<BlockPos, BlockState> toUnMerge = mergingStructure.getToUnMerge();
                unMergingStates = new BlockState[toUnMerge.size()];
                StructureGroup structureGroup = null;
                if (moveSize > 1) { // Only use Structure group if there are more than 1 block entities in the group
                    structureGroup = PistonLibApi.getSupplier().createStructureGroup(level.isClientSide);
                }
                for (int i = moveSize - 1; i >= 0; i--) {
                    BlockPos posToMove = toMove.get(i);
                    BlockPos dstPos = posToMove.relative(moveDir);
                    BlockState stateToMove = statesToMove.get(i);
                    BlockEntity blockEntityToMove = blockEntitiesToMove.get(i);

                    boolean move = true;

                    // UnMerge blocks
                    if (toUnMerge.containsKey(posToMove)) {
                        Pair<BlockState, BlockState> unmergedStates = null;
                        if (stateToMove.pl$getBlockEntityMergeRules().checkUnMerge()) {
                            unmergedStates = blockEntityToMove.pl$doUnMerge(stateToMove, moveDir);
                            if (!blockEntityToMove.pl$shouldUnMergeBlockEntity(stateToMove, moveDir)) {
                                blockEntityToMove = null;
                            }
                        }
                        if (unmergedStates == null) {
                            unmergedStates = stateToMove.pl$doUnMerge(level,
                                    posToMove, moveDir, toUnMerge.get(posToMove));
                        }
                        if (unmergedStates != null) {
                            unMergingStates[unMergingIndex++] = stateToMove;
                            stateToMove = unmergedStates.getFirst();
                            BlockState stateToKeep = unmergedStates.getSecond();
                            toKeep.put(posToMove, stateToKeep);
                            affectedStates[affectedIndex++] = stateToKeep;
                            toRemove.remove(posToMove);
                            move = false;
                        }
                    }

                    toRemove.remove(dstPos);

                    BlockState movingBlock = this.family.getMoving().defaultBlockState()
                            .setValue(BasicMovingBlock.FACING, facing);
                    BlockEntity movingBlockEntity = this.family.newMovingBlockEntity(
                            structureGroup, dstPos, movingBlock, stateToMove, blockEntityToMove,
                            facing, extend, false);

                    level.setBlock(dstPos, movingBlock, UPDATE_MOVE_BY_PISTON |
                            (blockEntityToMove != null ? UPDATE_CLIENTS : UPDATE_INVISIBLE));
                    level.setBlockEntity(movingBlockEntity);

                    if (move) {
                        affectedStates[affectedIndex++] = stateToMove;
                    }
                }
            }
        }
    }

    @Override
    public void taskMergeBlocks() {
        if (!(structure instanceof MergingPistonStructureResolver mergingStructure)) {
            return;
        }
        List<BlockPos> toMerge = mergingStructure.getToMerge();
        float speed = extend ? family.getExtendingSpeed() : family.getRetractingSpeed();

        // Merge Blocks
        for (int i = toMerge.size() - 1; i >= 0; i--) {
            BlockPos posToMerge = toMerge.get(i);
            BlockState stateToMerge = level.getBlockState(posToMerge);

            BlockPos mergeIntoPos = posToMerge.relative(moveDir);
            BlockState mergeIntoState = level.getBlockState(mergeIntoPos);

            if (mergeIntoState.getBlock() instanceof MergeBlock) { // MultiMerge
                if (level.getBlockEntity(mergeIntoPos) instanceof MergeBlockEntity mergeBlockEntity) {
                    if (stateToMerge.pl$getBlockEntityMergeRules().checkMerge() &&
                            mergeBlockEntity.getInitialBlockEntity() != null) {
                        BlockEntity blockEntityToMerge = level.getBlockEntity(posToMerge);
                        if (blockEntityToMerge != null && blockEntityToMerge.pl$shouldStoreSelf(mergeBlockEntity)) {
                            blockEntityToMerge.pl$onMerge(mergeBlockEntity, moveDir);
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
                if (mergeIntoBlockEntity != null && mergeIntoBlockEntity.pl$doInitialMerging()) {
                    mergeBlockEntity = new MergeBlockEntity(
                            mergeIntoPos, mergeBlockState, mergeIntoState, mergeIntoBlockEntity
                    );
                    mergeIntoBlockEntity.pl$onMerge(mergeBlockEntity, moveDir); // Call onMerge for the base block entity

                    if (stateToMerge.pl$getBlockEntityMergeRules().checkMerge()) {
                        BlockEntity blockEntityToMerge = level.getBlockEntity(posToMerge);
                        if (blockEntityToMerge != null && blockEntityToMerge.pl$shouldStoreSelf(mergeBlockEntity)) {
                            blockEntityToMerge.pl$onMerge(mergeBlockEntity, moveDir);
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
    public void taskDoUnMergeUpdates() {
        int unMergingIndex = 0;

        // Keep these blocks as they unmerged, just change there state to the new one
        for (Map.Entry<BlockPos, BlockState> entry : toKeep.entrySet()) {
            level.setBlock(
                    entry.getKey(),
                    entry.getValue(),
                    UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS
            );
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
