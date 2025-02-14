package ca.fxco.pistonlib.api.pistonLogic.controller;

import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureResolver;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureRunner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * An interface used as the base of all piston related calls from blocks.
 * Having this logic split from the physical blocks allows any block to gain the ability to become a piston.
 *
 * @author FX
 * @since 1.0.4
 */
public interface PistonController {

    /**
     * Gets the piston family used by this controller.
     *
     * @return The piston family for this controller.
     * @since 1.2.0
     */
    PistonFamily getFamily();

    /**
     * Sets the piston family used by this controller.
     *
     * @param family The piston family for this controller.
     * @since 1.2.0
     */
    void setFamily(PistonFamily family);

    /**
     * Gets the piston type used by this controller.
     *
     * @return The piston type for this controller.
     * @since 1.2.0
     */
    PistonType getType();

    /**
     * Creates a new structure resolver
     *
     * @param level  The level to run the structure runner in
     * @param pos    The position of the piston calling the runner
     * @param facing The facing direction of the piston calling the runner
     * @param length The length that the piston calling the runner will attempt to reach
     * @param extend If this should be an extension or retraction
     * @param <S> The structure resolver type
     * @return The new structure resolver
     * @since 1.2.0
     */
    <S extends PistonStructureResolver & StructureResolver> S newStructureResolver(
            Level level, BlockPos pos, Direction facing, int length, boolean extend
    );

    /**
     * Creates a new structure runner
     *
     * @param level             The level to run the structure runner in
     * @param pos               The position of the piston calling the runner
     * @param facing            The facing direction of the piston calling the runner
     * @param length            The length that the piston calling the runner will attempt to reach
     * @param extend            If this should be an extension or retraction
     * @param structureProvider A structure resolver provider
     * @param <S> The structure resolver type
     * @return The new structure runner
     * @since 1.2.0
     */
    <S extends PistonStructureResolver & StructureResolver> StructureRunner newStructureRunner(
            Level level, BlockPos pos, Direction facing, int length, boolean extend,
            StructureResolver.Factory<S> structureProvider
    );

    /**
     * When extending, it gets the maximum length of the piston.
     * When retracting, it gets the minimum length of the piston.
     *
     * @return the length that the piston should have once it's done moving blocks
     * @since 1.2.0
     */
    int getLength(Level level, BlockPos pos, BlockState state);

    /**
     * Used to check if piston is powered
     *
     * @param level of the block state
     * @param pos block position of the block state
     * @param facing direction to not check for the signal, usually piston facing
     * @return {@code true} if block states neighbors has signal except from one direction
     * @since 1.2.0
     */
    boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing);

    /**
     * Checks if piston should extend and then adds block event based on that
     *
     * @param level of the block state
     * @param pos block position of the block state
     * @param state block state to check
     * @param onPlace was block just placed
     * @since 1.2.0
     */
    void checkIfExtend(Level level, BlockPos pos, BlockState state, boolean onPlace);

    /**
     * What should it do on retract, nothing, retract and pull blocks or just retract without pulling
     *
     * @param level of the block state
     * @param pos block position of the block state
     * @param facing the direction piston extends in
     * @param length of the piston
     * @return retract type of the block state
     * @since 1.2.0
     */
    int getRetractType(ServerLevel level, BlockPos pos, Direction facing, int length);

    /**
     * Vanilla method that we also implement
     *
     * @param state block state of the piston base
     * @param level of the block state
     * @param pos block position of the block state
     * @param type retract type {@link ca.fxco.pistonlib.api.pistonLogic.PistonEvents}
     * @param data 3D data of the direction
     * @return {@code false} if event failed, otherwise {@code true}
     * @since 1.2.0
     */
    boolean triggerEvent(BlockState state, Level level, BlockPos pos, int type, int data);

    /**
     * plays a {@link GameEvent} from a position.
     *
     * @param level of the block state
     * @param event an event as a holder
     * @param pos   block position to play the game event at
     * @since 1.2.0
     */
    void playEvents(Level level, Holder<GameEvent> event, BlockPos pos);

    /**
     * Returns whether this piston can move the given block,
     * taking into account world height/border as well as
     * vanilla and custom piston push reactions.
     *
     * @param state        The state we're attempting to move
     * @param level        The level in which we're attempting to move the block
     * @param pos          The position of the block we're attempting to move
     * @param moveDir      The direction we're attempting to move the block in
     * @param allowDestroy If we're allowed to destroy blocks
     * @param pistonFacing The direction that the piston moving this block is facing
     * @return {@code true} if the controller is able to move the block, otherwise {@code false}
     * @since 1.2.0
     */
    boolean canMoveBlock(BlockState state, Level level, BlockPos pos, Direction moveDir,
                         boolean allowDestroy, Direction pistonFacing);

    /**
     * Returns whether this piston controller can move the given block.
     * This method assumes the world height/border and push reaction
     * checks have all succeeded.
     *
     * @param state The block state that we're attempting to move
     * @return {@code true} if the controller is able to move the block, otherwise {@code false}
     * @since 1.2.0
     */
    boolean canMoveBlock(BlockState state);

    /**
     * The method used to move blocks from a position.
     * For normal pistons, you'd usually create your structure runner here.
     *
     * @param level  The level to move the block in
     * @param pos    The position of the first block to be moved
     * @param facing The direction to move the blocks in
     * @param length The length that the piston should have once it's done moving blocks
     * @param extend If this should be an extension or retraction
     * @return {@code true} if the controller was able to move the blocks, otherwise {@code false}
     * @since 1.2.0
     */
    boolean moveBlocks(Level level, BlockPos pos, Direction facing, int length, boolean extend);

    /**
     * The method used to get piston's head block state for piston
     *
     * @param pistonPos        block position of the piston
     * @param level            The level of the piston
     * @param pushingDir direction piston is pushing in
     * @return block state of the head
     * @since 1.2.0
     */
    BlockState getHeadState(BlockPos pistonPos, Level level, Direction pushingDir);
}
