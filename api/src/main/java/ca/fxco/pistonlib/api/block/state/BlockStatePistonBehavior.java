package ca.fxco.pistonlib.api.block.state;

import ca.fxco.pistonlib.api.block.BlockPistonBehavior;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

/**
 * This interface is for internal use only.
 * Use {@link BlockPistonBehavior} for single block conditions
 *
 * @author FX
 * @since 1.0.4
 */
public interface BlockStatePistonBehavior {

    /**
     * Gets the weight of this block.
     *
     * @return the weight of the block
     * @since 1.0.4
     */
    int pl$getWeight();

    /**
     * This must return true in order for the configurable piston behavior to be used!
     *
     * @return {@code true} if block uses configurable piston behavior, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$usesConfigurablePistonBehavior();

    /** If the block state is currently movable. Allows for quick checks to boost performance by skipping more
     * intensive checks early. However, this isn't always checked first in some instances,
     * so make sure to account for that!
     *
     * @param level of the block
     * @param pos   block position of the block
     * @return {@code true} if block state is movable, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$isMovable(BlockGetter level, BlockPos pos);

    /**
     * Checks if a piston can push this state.
     *
     * @param level of the block state
     * @param pos   block position of the block state
     * @param dir   direction to move in
     * @return {@code true} if piston can push block state, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canPistonPush(BlockGetter level, BlockPos pos, Direction dir);

    /**
     * Checks if a piston can pull at a given location and direction.
     *
     * @param level of the block state
     * @param pos   block position of the block state
     * @param dir   direction to move in
     * @return {@code true} if piston can pull block state, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canPistonPull(BlockGetter level, BlockPos pos, Direction dir);

    /**
     * Checks if this state is able to bypass the {@link StickyType#FUSED} sticky type.
     *
     * @return {@code true} if block state can bypass fused, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canBypassFused();

    /**
     * Checks if this state can be destroyed at this position.
     *
     * @param level of the block state
     * @param pos   block position of the block state
     * @return {@code true} if piston can destroy block state, otherwise {@code false}
     * @since 1.0.4
     */
    boolean pl$canDestroy(BlockGetter level, BlockPos pos);

    /**
     * This is called whenever an entity is pushed into a block by a piston.
     *
     * @param level  of the block state
     * @param pos    block position of the block state
     * @param entity pushed into the block state
     * @since 1.0.4
     */
    void pl$onPushEntityInto(BlockGetter level, BlockPos pos, Entity entity);

}
