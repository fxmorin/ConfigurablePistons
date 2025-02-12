package ca.fxco.pistonlib.api.block;

import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Allows getting block's piston behavior.
 * These methods are only used if {@link  #pl$usesConfigurablePistonBehavior} returns {@code true}.
 * This allows for more configurable and conditional piston behavior
 *
 * @author Space Walker
 * @since 1.0.4
 */
public interface BlockPistonBehavior {

    /**
     * Gets the weight of this block.
     *
     * @return the weight of the block
     * @since 1.0.4
     */
    default int pl$getWeight(BlockState state) {
        return 1;
    }

    /**
     * This must return true in order for the configurable piston behavior to be used!
     *
     * @return {@code true} if block uses configurable piston behavior, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$usesConfigurablePistonBehavior() {
        return false;
    }

    /** If the block state is currently movable. Allows for quick checks to boost performance by skipping more
     * intensive checks early. However, this isn't always checked first in some instances,
     * so make sure to account for that!
     *
     * @param level of the block
     * @param pos   block position of the block
     * @param state block state of the block
     * @return {@code true} if block state is movable, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$isMovable(BlockGetter level, BlockPos pos, BlockState state) {
        return true;
    }

    /**
     * Checks if a piston can push this block.
     *
     * @param level of the block state
     * @param pos   block position of the block state
     * @param state block state of the block
     * @param dir   direction to move in
     * @return {@code true} if piston can push block state, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canPistonPush(BlockGetter level, BlockPos pos, BlockState state, Direction dir) {
        return true;
    }

    /**
     * Checks if a piston can pull at a given location and direction.
     *
     * @param level of the block state
     * @param pos   block position of the block state
     * @param state block state of the block
     * @param dir   direction to move in
     * @return {@code true} if piston can pull block state, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canPistonPull(BlockGetter level, BlockPos pos, BlockState state, Direction dir) {
        return true;
    }

    /**
     * Checks if this state is able to bypass the {@link StickyType#FUSED} sticky type.
     *
     * @param state of the block
     * @return {@code true} if block state can bypass fused, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canBypassFused(BlockState state) {
        return false;
    }

    /**
     * Checks if this state can be destroyed at this position.
     *
     * @param level of the block state
     * @param pos   block position of the block state
     * @param state block state of the block
     * @return {@code true} if piston can destroy block state, otherwise {@code false}
     * @since 1.0.4
     */
    default boolean pl$canDestroy(BlockGetter level, BlockPos pos, BlockState state) {
        return false;
    }

    /**
     * This is called whenever an entity is pushed into a block by a piston.
     *
     * @param level  of the block state
     * @param pos    block position of the block state
     * @param state  block state of the block
     * @param entity pushed into the block state
     * @since 1.0.4
     */
    default void pl$onPushEntityInto(BlockGetter level, BlockPos pos, BlockState state, Entity entity) {}

}
