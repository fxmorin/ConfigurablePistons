package ca.fxco.pistonlib.pistonLogic.structureResolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.api.pistonLogic.controller.PistonController;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickRules;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureResolver;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import static ca.fxco.pistonlib.PistonLib.DIRECTIONS;

public class BasicStructureResolver extends PistonStructureResolver implements StructureResolver {

    protected final PistonController controller;
    protected final int maxMovableWeight;
    protected final int length;
    protected int movingWeight = 0; // used instead of the toPush size so some blocks can be harder to push than others

    public BasicStructureResolver(PistonController controller, Level level, BlockPos pos,
                                  Direction facing, int length, boolean extend) {
        super(level, pos, facing, extend);

        this.controller = controller;
        this.length = length;
        this.maxMovableWeight = controller.getFamily().getPushLimit();

        this.startPos = this.pistonPos.relative(this.pistonDirection, this.length + 1);
    }

    protected void resetResolver() {
        this.toPush.clear();
        this.toDestroy.clear();
        this.movingWeight = 0;
    }

    @Override
    public boolean resolve() {
        resetResolver();

        if (this.controller.getFamily().hasCustomLength() && !resolveLongPiston()) {
            return false;
        }

        return runStructureGeneration();
    }

    protected boolean resolveLongPiston() {
        // make sure the piston doesn't try to extend while it's already extending
        // or retract while it's already retracting
        BlockPos headPos = this.pistonPos.relative(this.pistonDirection, this.length);
        BlockState headState = this.level.getBlockState(headPos);
        if (headState.is(this.controller.getFamily().getMoving())) {
            BlockEntity blockEntity = this.level.getBlockEntity(headPos);
            if (blockEntity == null) {
                return false;
            }
            if (blockEntity.getType() == this.controller.getFamily().getMovingBlockEntityType()) {
                BasicMovingBlockEntity mbe = (BasicMovingBlockEntity)blockEntity;
                if (mbe.isSourcePiston && mbe.extending == this.extending && mbe.direction == this.pistonDirection) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean runStructureGeneration() {
        // Structure Generation
        BlockState state = this.level.getBlockState(this.startPos);
        if (!this.controller.canMoveBlock(state, this.level, this.startPos,
                this.pushDirection, false, this.pistonDirection)) {
            // Block directly in front is immovable, can only be true if extending, and it can be destroyed
            if (this.extending) {
                if (state.pl$usesConfigurablePistonBehavior()) {
                    if (state.pl$canDestroy(this.level, this.startPos)) {
                        this.toDestroy.add(this.startPos);
                        return true;
                    }
                } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                    this.toDestroy.add(this.startPos);
                    return true;
                }
                return false;
            }
            return false;
        }
        Direction pushDir = !this.extending ? this.pushDirection.getOpposite() : this.pushDirection;
        // Make sure we don't pull a NO_STICK block
        if (!this.extending && state.pl$usesConfigurablePistonStickiness() &&
                state.pl$sideStickiness(pushDir) == StickyType.NO_STICK) {
            return false;
        }
        // Start block isn't immovable, we can check if it's possible to move this line
        if (this.attemptMoveLine(state, this.startPos, pushDir)) {
            return false;
        }

        // This loops through the blocks to push and creates the branches
        for (int i = 0; i < this.toPush.size(); i++) {
            BlockPos blockPos = this.toPush.get(i);
            state = this.level.getBlockState(blockPos);
            if (attemptCreateBranchesAtBlock(state, blockPos)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isPiston(BlockPos pos) {
        for (int i = 0; i <= this.length; i++) {
            if (this.pistonPos.relative(this.pistonDirection, i).equals(pos)) {
                return true;
            }
        }
    	return false;
    }

    protected static boolean canStick(BlockState state, BlockState adjState, Direction dir) {
        return state.pl$matchesStickyConditions(adjState, dir);
    }

    // Stickiness checks
    protected static boolean canAdjacentBlockStick(Direction dir, BlockState state, BlockState adjState) {
        return canAdjacentBlockStick(dir, state, adjState, true);
    }

    // Stickiness checks
    protected static boolean canAdjacentBlockStick(Direction dir, BlockState state,
                                                   BlockState adjState, boolean attemptIndirect) {
        if (adjState.pl$usesConfigurablePistonStickiness()) {
            if (!adjState.pl$isSticky()) {
                return attemptIndirect && canIndirectBlockStick(dir, state, adjState);
            }
            Direction oppositeDir = dir.getOpposite();
            StickyType type = adjState.pl$sideStickiness(oppositeDir);
            if (type == StickyType.CONDITIONAL) {
                if (canStick(state, adjState, dir)) {
                    return true;
                }
                return attemptIndirect && canIndirectBlockStick(dir, state, adjState);
            }

            if (type != StickyType.DEFAULT) {
                return type != StickyType.NO_STICK; // If NO_STICKY, even indirect sticky can't stick to this side!
            }
        }
        if (!state.pl$usesConfigurablePistonStickiness()) {
            StickyGroup stickyGroup1 = state.pl$getStickyGroup();
            if (stickyGroup1 != null) {
                return StickRules.test(stickyGroup1, adjState.pl$getStickyGroup());
            }
        }
        return attemptIndirect && canIndirectBlockStick(dir, state, adjState);
    }

    protected static boolean canIndirectBlockStick(Direction dir, BlockState state, BlockState adjState) {
        if (PistonLibConfig.indirectStickyApi) {
            if (PistonLibConfig.allStickyTypesAreIndirect) {
                return canAdjacentBlockStick(dir.getOpposite(), adjState, state, false);
            }
            if (state.pl$usesConfigurablePistonStickiness() && state.pl$isSticky()) {
                StickyType type = state.pl$sideStickiness(dir);
                return type.ordinal() >= StickyType.STRONG.ordinal();
            }
        }
        return false;
    }

    protected boolean attemptCreateBranchesAtBlock(BlockState state, BlockPos pos) {
        if (state.pl$usesConfigurablePistonStickiness()) {
            if (!PistonLibConfig.indirectStickyApi && !state.pl$isSticky()) {
                return false; // Can't do this early exit when `indirectStickyApi` is used.
            }
            Map<Direction, StickyType> sides = state.pl$stickySides();
            // We need to make sure all sides are attempted when using the `indirectStickyApi`
            if (PistonLibConfig.indirectStickyApi && sides.size() != DIRECTIONS.length) {
                for (Direction dir : DIRECTIONS) {
                    StickyType type = sides.getOrDefault(dir, StickyType.DEFAULT);
                    if (attemptCreateBranchForStickySide(state, pos, type, dir)) {
                        return true;
                    }
                }
            } else { // Probably won't check all sides, just the sides with custom sticky behavior
                for (Map.Entry<Direction, StickyType> sideData : sides.entrySet()) {
                    if (attemptCreateBranchForStickySide(state, pos, sideData.getValue(), sideData.getKey())) {
                        return true;
                    }
                }
            }
        } else {
            if (!PistonLibConfig.indirectStickyApi && !state.pl$hasStickyGroup()) {
                return false; // Can't do this early exit when `indirectStickyApi` is used.
            }
            for (Direction dir : DIRECTIONS) {
                if (dir.getAxis() != this.pushDirection.getAxis()) {
                    BlockPos adjPos = pos.relative(dir);
                    BlockState adjState = this.level.getBlockState(adjPos);
                    if (!state.isAir() && canMoveAdjacentBlock(dir, state, adjState) &&
                            attemptMoveLine(adjState, adjPos, dir)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean attemptCreateBranchForStickySide(BlockState state, BlockPos pos,
                                                       StickyType stickyType, Direction dir) {
        if (stickyType != StickyType.NO_STICK && dir.getAxis() != this.pushDirection.getAxis()) {
            BlockPos adjPos = pos.relative(dir);
            BlockState adjState = this.level.getBlockState(adjPos);
            if (stickyType == StickyType.CONDITIONAL && !canStick(state, adjState, dir)) {
                return false;
            }
            return canMoveAdjacentBlock(dir, state, adjState) && attemptMoveLine(adjState, adjPos, dir);
        }
        return false;
    }

    protected boolean canMoveAdjacentBlock(Direction dir, BlockState state, BlockState adjState) {
        if (state.pl$usesConfigurablePistonStickiness()) {
            StickyType stickyType = state.pl$sideStickiness(dir);
            if (stickyType == StickyType.NO_STICK) {
                return false;
            } else if (stickyType == StickyType.CONDITIONAL && canStick(state, adjState, dir)) {
                return true;
            }
        }
        return canAdjacentBlockStick(dir, state, adjState);
    }

    protected boolean isSticky(BlockState state, BlockState adjState, Direction dir) {
        if (PistonLibConfig.indirectStickyApi) {
            if (state.pl$usesConfigurablePistonStickiness() && state.pl$isSticky()) {
                StickyType type = state.pl$sideStickiness(dir);
                return type != StickyType.NO_STICK;
            }
            // All other blocks should check the blocks around them for indirect sticky blocks, except air
            return !state.isAir();
        }
        if (state.pl$usesConfigurablePistonStickiness()) {
            if (state.pl$isSticky()) {
                StickyType type = state.pl$sideStickiness(dir);
                if (type == StickyType.CONDITIONAL) {
                    return canStick(state, adjState, dir);
                }
                return type.ordinal() >= StickyType.STICKY.ordinal();
            }
            return false;
        }
        return state.pl$hasStickyGroup();
    }

    protected boolean attemptMoveLine(BlockState state, BlockPos pos, Direction dir) {
        if (state.isAir() || isPiston(pos) || this.toPush.contains(pos)) {
            return false;
        }
        if (!this.controller.canMoveBlock(state, this.level, pos, this.pushDirection, false, dir)) {
            return false;
        }
        int weight = state.pl$getWeight();
        if (weight + this.movingWeight > this.maxMovableWeight) {
            return true;
        }
        int distance = 1;
        Direction pullDirection = this.pushDirection.getOpposite();
        BlockState currentState = state;
        BlockPos nextPos = pos.relative(pullDirection, distance);
        BlockState nextState = this.level.getBlockState(nextPos);
        while (isSticky(currentState, nextState, pullDirection)) {
            if (nextState.isAir() ||
                    isPiston(nextPos) ||
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
            nextPos = pos.relative(pullDirection, distance);
            currentState = nextState;
            nextState = this.level.getBlockState(nextPos);
        }
        this.movingWeight += weight;
        int j = 0, k;
        for (k = distance - 1; k >= 0; --k) {
            this.toPush.add(pos.relative(pullDirection, k));
            ++j;
        }
        k = 1;
        while(true) {
            BlockPos pos2 = pos.relative(this.pushDirection, k);
            int l = this.toPush.indexOf(pos2);
            if (l > -1) {
                this.setMovedBlocks(j, l);
                for (int m = 0; m <= l + j; ++m) {
                    BlockPos pos3 = this.toPush.get(m);
                    state = this.level.getBlockState(pos3);
                    if (attemptCreateBranchesAtBlock(state, pos3)) {
                        return true;
                    }
                }
                return false;
            }
            state = this.level.getBlockState(pos2);
            if (state.isAir()) {
                return false;
            } else if (isPiston(pos2)) {
                return true;
            } else if (!controller.canMoveBlock(state, this.level, pos2,
                    this.pushDirection, true, this.pushDirection)) {
                return true;
            }
            if (state.pl$usesConfigurablePistonBehavior()) {
                if (state.pl$canDestroy(this.level, pos2)) {
                    this.toDestroy.add(pos2);
                    return false;
                }
            } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(pos2);
                return false;
            }
            weight = state.pl$getWeight();
            if (weight + this.movingWeight > this.maxMovableWeight) {
                return true;
            }
            this.movingWeight += weight;
            this.toPush.add(pos2);
            ++j;
            ++k;
        }
    }

    protected void setMovedBlocks(int from, int to) {
        List<BlockPos> list = new ArrayList<>(this.toPush.subList(0, to));
        List<BlockPos> list2 = new ArrayList<>(this.toPush.subList(this.toPush.size() - from, this.toPush.size()));
        List<BlockPos> list3 = new ArrayList<>(this.toPush.subList(to, this.toPush.size() - from));
        this.toPush.clear();
        this.toPush.addAll(list);
        this.toPush.addAll(list2);
        this.toPush.addAll(list3);
    }

    protected int getMoveLimit() {
        return this.maxMovableWeight;
    }
}
