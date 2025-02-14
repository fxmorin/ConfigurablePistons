package ca.fxco.pistonlib.pistonLogic.structureResolvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.fxco.pistonlib.api.pistonLogic.controller.PistonController;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class MergingPistonStructureResolver extends BasicStructureResolver {

    @Getter
    public final List<BlockPos> toMerge = new ArrayList<>();
    @Getter
    public final Map<BlockPos, BlockState> toUnMerge = new HashMap<>(); // states to unmerge and block state which pulled them
    public final List<BlockPos> ignore = new ArrayList<>();

    public MergingPistonStructureResolver(PistonController controller, Level level, BlockPos pos, Direction facing,
                                          int length, boolean extend) {
        super(controller, level, pos, facing, length, extend);
    }

    @Override
    protected void resetResolver() {
        super.resetResolver();
        this.toMerge.clear();
        this.toUnMerge.clear();
        this.ignore.clear();
    }

    @Override
    protected boolean runStructureGeneration() {
        if (super.runStructureGeneration()) {
            for (BlockPos ignorePos : this.ignore) {
                this.toUnMerge.remove(ignorePos); // Remove ignored blocks from toUnMerge list
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean attemptMoveLine(BlockState state, BlockPos pos, Direction dir) {
        if (state.isAir() ||
                isPiston(pos) ||
                this.toPush.contains(pos) ||
                this.toMerge.contains(pos) ||
                this.ignore.contains(pos)) {
            return false;
        }
        if (!this.controller.canMoveBlock(state, this.level, pos, this.pushDirection, false, dir)) {
            return false;
        }
        int weight = state.pl$getWeight();
        if (weight + this.movingWeight > this.maxMovableWeight) {
            return true;
        }
        Direction pullDirection = this.pushDirection.getOpposite();
        boolean initialBlock = pos.relative(pullDirection).equals(this.pistonPos);

        // UnMerge checks on initial line blocks
        if (!initialBlock) {
            if (state.pl$usesConfigurablePistonMerging()) {
                BlockPos headPos = pos.relative(extending ? pullDirection : pushDirection, 2);
                BlockState neighborState;
                if (headPos.equals(this.pistonPos)) {
                    neighborState = this.controller.getHeadState(
                            headPos, level, extending ? pushDirection : pullDirection);
                } else {
                    neighborState = level.getBlockState(pos.relative(pullDirection));
                }
                if (state.pl$canUnMerge(level, pos, neighborState, this.pushDirection) &&
                    (!state.pl$getBlockEntityMergeRules().checkUnMerge() ||
                    level.getBlockEntity(pos).pl$canUnMerge(state, neighborState, this.pushDirection))) {
                    if (this.toUnMerge.containsKey(pos)) {
                        // If multiple sticky blocks are moving the same block, don't unmerge
                        this.ignore.add(pos);
                    } else {
                        this.toUnMerge.put(pos, neighborState);
                    }
                }
            }
        }

        // Do sticky checks on initial line blocks
        int distance = 1;
        BlockPos lastBlockPos = pos;
        BlockState currentState = state;
        BlockPos nextPos = pos.relative(pullDirection, distance);
        BlockState nextState = this.level.getBlockState(nextPos);
        while (isSticky(currentState, nextState, pullDirection)) {
            if (nextState.isAir() ||
                    isPiston(nextPos) ||
                    this.toMerge.contains(nextPos) ||
                    this.ignore.contains(nextPos) ||
                    !canMoveAdjacentBlock(pullDirection, currentState, nextState) ||
                    !this.controller.canMoveBlock(nextState, this.level, nextPos,
                            this.pushDirection, false, pullDirection)) {
                break;
            }
            weight += nextState.pl$getWeight();
            if (weight + this.movingWeight > this.maxMovableWeight) {
                return true;
            }
            ++distance;

            // UnMerge checks
            if (nextState.pl$usesConfigurablePistonMerging() &&
                    nextState.pl$canUnMerge(level, nextPos, currentState, this.pushDirection)
                    && !this.toPush.contains(lastBlockPos) &&
                    (!nextState.pl$getBlockEntityMergeRules().checkUnMerge() ||
                    level.getBlockEntity(nextPos).pl$canUnMerge(nextState, currentState, this.pushDirection))) {
                if (this.toUnMerge.containsKey(nextPos)) {
                    // If multiple sticky blocks are moving the same block, don't unmerge
                    this.ignore.add(nextPos);
                } else {
                    this.toUnMerge.put(nextPos, currentState);
                }
            }
            currentState = nextState;
            lastBlockPos = nextPos;
            nextPos = pos.relative(pullDirection, distance);
            nextState = this.level.getBlockState(nextPos);
        }
        this.movingWeight += weight;
        for(int k = distance - 1; k >= 0; --k) {
            this.toPush.add(pos.relative(pullDirection, k));
        }
        int nextIndex = 1;
        lastBlockPos = pos;
        BlockPos currentPos = pos.relative(this.pushDirection, nextIndex);
        while(true) {
            // Sticky Checks
            int lastIndex = this.toPush.indexOf(currentPos);
            if (lastIndex > -1) {
                this.setMovedBlocks(distance, lastIndex);
                for(int m = 0; m <= lastIndex + distance; ++m) {
                    BlockPos pos3 = this.toPush.get(m);
                    state = this.level.getBlockState(pos3);
                    if (attemptCreateBranchesAtBlock(state, pos3)) {
                        return true;
                    }
                }
                return false;
            }

            currentState = state;
            state = this.level.getBlockState(currentPos);

            // Merge checks
            if (state.getBlock() instanceof MergeBlock) { // MultiMerge
                if (currentState.pl$usesConfigurablePistonMerging() &&
                        currentState.pl$canMergeFromSide(level, lastBlockPos, pullDirection)) {
                    if (level.getBlockEntity(currentPos) instanceof MergeBlockEntity mergeBlockEntity &&
                            mergeBlockEntity.canMergeFromSide(this.pushDirection) &&
                            mergeBlockEntity.canMerge(state, this.pushDirection)) {
                        this.toMerge.add(lastBlockPos);
                        this.toPush.remove(lastBlockPos);
                        this.ignore.add(currentPos);
                        return false;
                    }
                }
            } else {
                if (state.pl$usesConfigurablePistonMerging()) {
                    if (state.pl$canMerge(level, currentPos, currentState, this.pushDirection)) {
                        if ((!currentState.pl$usesConfigurablePistonMerging() ||
                                currentState.pl$canMergeFromSide(level, lastBlockPos, pullDirection)) &&
                                (!state.pl$getBlockEntityMergeRules().checkMerge() ||
                                level.getBlockEntity(currentPos).pl$canMerge(state, currentState, this.pushDirection))
                        ) {
                            this.toMerge.add(lastBlockPos);
                            this.toPush.remove(lastBlockPos);
                            this.ignore.add(currentPos);
                            return false;
                        }
                    }
                    if (!this.toPush.contains(lastBlockPos) &&
                            state.pl$canUnMerge(level, currentPos, currentState, this.pushDirection) &&
                            (!state.pl$getBlockEntityMergeRules().checkUnMerge() ||
                            level.getBlockEntity(currentPos).pl$canUnMerge(state, currentState, this.pushDirection))) {
                        if (this.toUnMerge.containsKey(currentPos)) {
                            // If multiple sticky blocks are moving the same block, don't unmerge
                            this.ignore.add(currentPos);
                        } else {
                            this.toUnMerge.put(currentPos, currentState);
                        }
                    }
                }
            }

            // Movement Checks
            if (state.isAir()) {
                return false;
            } else if (currentPos.equals(this.pistonPos)) {
                return true;
            } else if (!controller.canMoveBlock(state, this.level, currentPos,
                    this.pushDirection, true, this.pushDirection)) {
                return true;
            }
            if (state.pl$usesConfigurablePistonBehavior()) {
                if (state.pl$canDestroy(this.level, currentPos)) {
                    this.toDestroy.add(currentPos);
                    return false;
                }
            } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(currentPos);
                return false;
            }
            weight = state.pl$getWeight();
            if (weight + this.movingWeight > this.maxMovableWeight) {
                return true;
            }
            this.movingWeight += weight;

            ++distance;
            ++nextIndex;

            lastBlockPos = currentPos;
            currentPos = pos.relative(this.pushDirection, nextIndex);

            // This check makes sure that if another block is going to push it from behind, it can't unmerge
            if (this.toUnMerge.containsKey(currentPos)) { // currentPos is actually nextPos until after toPush.add() runs
                this.ignore.add(currentPos);
            }

            this.toPush.add(lastBlockPos);
        }
    }

}
