package ca.fxco.pistonlib.api.block.state;

import java.util.Map;

import ca.fxco.pistonlib.api.block.*;
import ca.fxco.pistonlib.api.pistonLogic.base.PLMergeBlockEntity;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import ca.fxco.pistonlib.api.pistonLogic.PistonMoveBehavior;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface collects all custom behavior that is injected into {@code BlockState}'s.
 * Similarly to {@linkplain net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase BlockStateBase},
 * <p>
 * PistonLib handles forwarding method calls to the block's implementations.
 *
 * @author Space Walker
 * @since 1.0.4
 */
@SuppressWarnings("deprecation")
public interface PLBlockStateBase extends BlockStatePistonBehavior,
        BlockStatePistonMerging, BlockStatePistonStickiness, BlockStateQuasiPower, BlockStateMoveBehavior {

    // BlockStateMoveBehavior

    @Override
    default boolean pl$canOverridePistonMoveBehavior() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void pl$setPistonMoveBehaviorOverride(PistonMoveBehavior override) {
        throw new UnsupportedOperationException();
    }

    @Override
    default PistonMoveBehavior pl$getPistonMoveBehaviorOverride() {
        throw new UnsupportedOperationException();
    }


    // BlockStatePistonBehavior

    @Override
    default int pl$getWeight() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$usesConfigurablePistonBehavior() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$isMovable(BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canPistonPush(BlockGetter level, BlockPos pos, Direction dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canPistonPull(BlockGetter level, BlockPos pos, Direction dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canBypassFused() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canDestroy(BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void pl$onPushEntityInto(BlockGetter level, BlockPos pos, Entity entity) {
        throw new UnsupportedOperationException();
    }


    // BlockStatePistonMerging

    @Override
    default boolean pl$usesConfigurablePistonMerging() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canMergeFromSide(BlockGetter level, BlockPos pos, Direction pushDir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default BlockState pl$doMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canMultiMerge() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canMultiMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir,
                                     Map<Direction, PLMergeBlockEntity.MergeData> currentlyMerging) {
        throw new UnsupportedOperationException();
    }

    @Override
    default BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction,BlockState> states,
                                       BlockState mergingIntoState) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$canUnMerge(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockGetter level, BlockPos pos,
                                                                Direction dir, BlockState pullingState){
        throw new UnsupportedOperationException();
    }

    @Override
    default BlockPistonMerging.MergeRule pl$getBlockEntityMergeRules() {
        throw new UnsupportedOperationException();
    }


    // BlockStatePistonStickiness

    @Override
    default @Nullable StickyGroup pl$getStickyGroup() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$hasStickyGroup() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$usesConfigurablePistonStickiness() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$isSticky() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Map<Direction, StickyType> pl$stickySides() {
        throw new UnsupportedOperationException();
    }

    @Override
    default StickyType pl$sideStickiness(Direction dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$matchesStickyConditions(BlockState neighborState, Direction dir) {
        throw new UnsupportedOperationException();
    }

    // BlockStateQuasiPower

    @Override
    default int pl$getQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist) {
        throw new UnsupportedOperationException();
    }

    @Override
    default int pl$getDirectQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean pl$isQuasiConductor(BlockGetter level, BlockPos pos) {
        throw new UnsupportedOperationException();
    }
}
