package ca.fxco.pistonlib.api.block;

import ca.fxco.pistonlib.api.pistonLogic.base.PLMergeBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Both blocks that are attempting to merge should have the same checks!
 * These methods are only used if {@link #pl$usesConfigurablePistonMerging}` returns {@code true}
 *
 * @author FX
 * @since 1.0.4
 */
public interface BlockPistonMerging {

    /**
     * This must return true in order for the configurable and
     * conditional merging/compression logic to be used!
     *
     * @return {@code true} if it should use configurable piston merging, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$usesConfigurablePistonMerging() {
        return false;
    }

    /**
     * Checks if this block can be merged.
     *
     * @param state            block state of the block
     * @param level            of the block state
     * @param pos              block position of the block state
     * @param mergingIntoState block state to merge into
     * @param dir              direction being pushed
     * @return {@code true} if it's able to merge both states together, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canMerge(BlockState state, BlockGetter level, BlockPos pos,
                                BlockState mergingIntoState, Direction dir) {
        return true;
    }

    /**
     * Checks if this block can be merged from a specific side.
     * Usually opposite of pushDirection.
     *
     * @param state   block state of the block
     * @param level   of the block state
     * @param pos     block position of the block state
     * @param pushDir direction it being pushed
     * @return {@code true} if the block can be merged from a given side or can be merged from that side,
     *  otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canMergeFromSide(BlockState state, BlockGetter level, BlockPos pos, Direction pushDir) {
        return true;
    }

    /**
     * Merges two states together.
     *
     * @param state            block state of the block
     * @param level            of the block state
     * @param pos              block position of the block state
     * @param mergingIntoState block state to merge into
     * @param dir              direction it being pushed
     * @return the merged state
     * @since 1.0.4
     */
    default BlockState pl$doMerge(BlockState state, BlockGetter level, BlockPos pos,
                                  BlockState mergingIntoState, Direction dir) {
        return mergingIntoState;
    }


    /**
     * This must return true if you want to be able to merge more than one
     * block at a time using {@link #pl$canMultiMerge} and {@link #pl$doMultiMerge}
     *
     * @return {@code true} if it can multi merge, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canMultiMerge() {
        return false;
    }

    /**
     * While merging with a block, is this block state able to merge with other block states from other directions?
     *
     * @param state            block state of the block
     * @param level            of the block state
     * @param pos              block position of the block
     * @param mergingIntoState block state to merge into
     * @param dir              direction it being pushed
     * @param currentlyMerging sides that are currently merging
     * @return {@code true} if it can multi merge with blocks from other directions, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canMultiMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState,
                                     Direction dir, Map<Direction, PLMergeBlockEntity.MergeData> currentlyMerging) {
        return false;
    }

    /**
     * Merge multiple states into one
     *
     * @param level            of the block
     * @param pos              block position of the block
     * @param states           states around the block
     * @param mergingIntoState block state to merge into
     * @return the merged state
     * @since 1.0.4
     */
    default BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction, BlockState> states,
                                       BlockState mergingIntoState) {
        return mergingIntoState;
    }

    /**
     * Checks if this block can un-merge.
     *
     * @param state         block state of the block
     * @param level         of the block state
     * @param pos           block position of the block state
     * @param neighborState block state of block state's neighbor
     * @param dir           direction it being pulled
     * @return {@code true} if it's able to unmerge into two different states, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canUnMerge(BlockState state, BlockGetter level, BlockPos pos,
                                  BlockState neighborState, Direction dir) {
        return false;
    }

    /**
     * Un-merge this block into two different states.
     * The first block state in the pair is the block state that is pulled out.
     *
     * @param state block state of the block
     * @param level of the block state
     * @param pos   block position of the block state
     * @param dir   direction it being pulled
     * @return the block states that it should unmerge into.
     * @since 1.0.4
     */
    default @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, BlockGetter level,
                                                                BlockPos pos, Direction dir, BlockState pullingState) {
        return null;
    }

    /**
     * This method determines when the block entity should be used:
     * -     NEVER = Block entity is skipped completely
     * -   MERGING = Block entity is used to check merging conditions
     * - UNMERGING = Block entity is used to check unmerging conditions
     * -    ALWAYS = Block entity is always checked
     * </br>
     * State checks always happen before block entity checks.
     * Skipping won't get the block entity at all, this is done for performance reasons.
     * It allows us to quickly know if the block entity should be loaded and checked against.
     *
     * @see BlockPistonMerging.MergeRule
     * @return merge rule this block entity uses
     * @since 1.0.4
     */
    default MergeRule pl$getBlockEntityMergeRules() {
        return MergeRule.NEVER;
    }

    enum MergeRule {

        /** Block entity is skipped completely */
        NEVER(false, false),

        /** Block entity is used to check merging conditions */
        MERGING(true, false),

        /** Block entity is used to check unmerging conditions */
        UNMERGING(false, true),

        /** Block entity is always checked */
        ALWAYS(true, true);

        private final boolean merging;
        private final boolean unmerging;

        MergeRule(boolean merging, boolean unmerging) {
            this.merging = merging;
            this.unmerging = unmerging;
        }

        public boolean checkMerge() {
            return this.merging;
        }

        public boolean checkUnMerge() {
            return this.unmerging;
        }
    }
}
