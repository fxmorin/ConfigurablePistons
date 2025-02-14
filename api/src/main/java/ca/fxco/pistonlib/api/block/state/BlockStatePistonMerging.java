package ca.fxco.pistonlib.api.block.state;

import ca.fxco.pistonlib.api.block.BlockPistonMerging;
import ca.fxco.pistonlib.api.pistonLogic.base.PLMergeBlockEntity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This interface is for internal use only.
 * Use {@link BlockPistonMerging} for single block conditions
 *
 * @author FX
 * @since 1.0.4
 */
public interface BlockStatePistonMerging {

    /**
     * This must return true in order for the configurable and
     * conditional merging/compression logic to be used!
     *
     * @return {@code true} if it should use configurable piston merging, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$usesConfigurablePistonMerging();


    /**
     * Checks if this state can be merged.
     *
     * @param level            of the block state
     * @param pos              block position of the block state
     * @param mergingIntoState block state to merge into
     * @param dir              direction being pushed
     * @return {@code true} if it's able to merge both states together, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir);

    /**
     * Checks if this state can be merged from a specific side.
     * Usually opposite of pushDirection.
     *
     * @param level   of the block state
     * @param pos     block position of the block state
     * @param pushDir direction it being pushed
     * @return {@code true} if the block can be merged from a given side or can be merged from that side,
     *  otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canMergeFromSide(BlockGetter level, BlockPos pos, Direction pushDir);

    /**
     * Merges two states together.
     *
     * @param level            of the block state
     * @param pos              block position of the block state
     * @param mergingIntoState block state to merge into
     * @param dir              direction it being pushed
     * @return the merged state
     * @since 1.0.4
     */
    BlockState pl$doMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir);

    /**
     * This must return true if you want to be able to merge more than one
     * block at a time using {@link #pl$canMultiMerge} and {@link #pl$doMultiMerge}
     *
     * @return {@code true} if it can multi merge, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canMultiMerge();

    /**
     * While merging with a block, is this block state able to merge with other block states from other directions?
     *
     * @param level            of the block state
     * @param pos              block position of the block
     * @param mergingIntoState block state to merge into
     * @param dir              direction it being pushed
     * @param currentlyMerging is currently mering
     * @return {@code true} if it can multi merge with blocks from other directions, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canMultiMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir,
                             Map<Direction, PLMergeBlockEntity.MergeData> currentlyMerging);

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
    BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction,BlockState> states,
                               BlockState mergingIntoState);


    /**
     * Checks if this state can un-merge.
     *
     * @param level         of the block state
     * @param pos           block position of the block state
     * @param neighborState block state of block state's neighbor
     * @param dir           direction it being pulled
     * @return {@code true} if it's able to unmerge into two different states, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canUnMerge(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir);

    /**
     * Un-merge this state into two different states.
     * The first block state in the pair is the block state that is pulled out.
     *
     * @param level        of the block state
     * @param pos          block position of the block state
     * @param dir          direction it being pulled
     * @param pullingState block state which pulls this block
     * @return the block states that it should unmerge into.
     * @since 1.0.4
     */
    @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockGetter level, BlockPos pos,
                                                        Direction dir, BlockState pullingState);

    /**
     * This method determines when the block entity should be used:
     * -     NEVER = Block entity is skipped completely
     * -   MERGING = Block entity is used to check merging conditions
     * - UNMERGING = Block entity is used to check unmerging conditions
     * -    ALWAYS = Block entity is always checked
     * </b>
     * State checks always happen before block entity checks.
     * Skipping won't get the block entity at all, this is done for performance reasons.
     * It allows us to quickly know if the block entity should be loaded and checked against.
     *
     * @see BlockPistonMerging.MergeRule
     * @return merge rule this block entity uses
     * @since 1.0.4
     */
    BlockPistonMerging.MergeRule pl$getBlockEntityMergeRules();
}
