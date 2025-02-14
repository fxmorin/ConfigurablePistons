package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.api.block.BlockPistonMerging;
import ca.fxco.pistonlib.api.block.state.PLBlockStateBase;
import ca.fxco.pistonlib.api.pistonLogic.PistonMoveBehavior;
import ca.fxco.pistonlib.api.pistonLogic.base.PLMergeBlockEntity;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@SuppressWarnings("deprecation")
@Mixin(BlockStateBase.class)
public abstract class BlockStateBase_coreMixin implements PLBlockStateBase {

    @Unique
    private PistonMoveBehavior pl$pistonMoveBehaviorOverride = PistonMoveBehavior.DEFAULT;

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    //region PLBlockStateBase defaults

    // BlockStateMoveBehavior

    @Override
    public boolean pl$canOverridePistonMoveBehavior() {
        return this.getBlock().pl$canOverridePistonMoveBehavior();
    }

    // BlockStatePistonBehavior

    @Override
    public int pl$getWeight() {
        return this.getBlock().pl$getWeight(this.asState());
    }

    @Override
    public boolean pl$usesConfigurablePistonBehavior() {
        return this.getBlock().pl$usesConfigurablePistonBehavior();
    }

    @Override
    public boolean pl$isMovable(BlockGetter level, BlockPos pos) {
        return this.getBlock().pl$isMovable(level, pos, this.asState());
    }

    @Override
    public boolean pl$canPistonPush(BlockGetter level, BlockPos pos, Direction dir) {
        return this.getBlock().pl$canPistonPush(level, pos, this.asState(), dir);
    }

    @Override
    public boolean pl$canPistonPull(BlockGetter level, BlockPos pos, Direction dir) {
        return this.getBlock().pl$canPistonPull(level, pos, this.asState(), dir);
    }

    @Override
    public boolean pl$canBypassFused() {
        return this.getBlock().pl$canBypassFused(this.asState());
    }

    @Override
    public boolean pl$canDestroy(BlockGetter level, BlockPos pos) {
        return this.getBlock().pl$canDestroy(level, pos, this.asState());
    }

    @Override
    public void pl$onPushEntityInto(BlockGetter level, BlockPos pos, Entity entity) {
        this.getBlock().pl$onPushEntityInto(level, pos, this.asState(), entity);
    }


    // BlockStatePistonMerging

    @Override
    public boolean pl$usesConfigurablePistonMerging() {
        return this.getBlock().pl$usesConfigurablePistonMerging();
    }

    @Override
    public boolean pl$canMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        return this.getBlock().pl$canMerge(this.asState(), level, pos, mergingIntoState, dir);
    }

    @Override
    public boolean pl$canMergeFromSide(BlockGetter level, BlockPos pos, Direction pushDir) {
        return this.getBlock().pl$canMergeFromSide(this.asState(), level, pos, pushDir);
    }

    @Override
    public BlockState pl$doMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        return this.getBlock().pl$doMerge(this.asState(), level, pos, mergingIntoState, dir);
    }

    @Override
    public boolean pl$canMultiMerge() {
        return this.getBlock().pl$canMultiMerge();
    }

    @Override
    public boolean pl$canMultiMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir,
                                    Map<Direction, PLMergeBlockEntity.MergeData> currentlyMerging) {
        return this.getBlock().pl$canMultiMerge(
                this.asState(), level, pos, mergingIntoState, dir, currentlyMerging
        );
    }

    @Override
    public BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction,BlockState> states,
                                      BlockState mergingIntoState) {
        return this.getBlock().pl$doMultiMerge(level, pos, states, mergingIntoState);
    }

    @Override
    public boolean pl$canUnMerge(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        return this.getBlock().pl$canUnMerge(this.asState(), level, pos, neighborState, dir);
    }

    @Override
    public @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockGetter level, BlockPos pos,
                                                               Direction dir, BlockState pullingState) {
        return this.getBlock().pl$doUnMerge(this.asState(), level, pos, dir, pullingState);
    }

    @Override
    public BlockPistonMerging.MergeRule pl$getBlockEntityMergeRules() {
        return this.getBlock().pl$getBlockEntityMergeRules();
    }


    // BlockStatePistonStickiness

    @Override
    public @Nullable StickyGroup pl$getStickyGroup() {
        return this.getBlock().pl$getStickyGroup(this.asState());
    }

    @Override
    public boolean pl$hasStickyGroup() {
        return this.getBlock().pl$hasStickyGroup(this.asState());
    }

    @Override
    public boolean pl$usesConfigurablePistonStickiness() {
        return this.getBlock().pl$usesConfigurablePistonStickiness();
    }

    @Override
    public boolean pl$isSticky() {
        return this.getBlock().pl$isSticky(this.asState());
    }

    @Override
    public Map<Direction, StickyType> pl$stickySides() {
        return this.getBlock().pl$stickySides(this.asState());
    }

    @Override
    public StickyType pl$sideStickiness(Direction dir) {
        return this.getBlock().pl$sideStickiness(this.asState(), dir);
    }

    @Override
    public boolean pl$matchesStickyConditions(BlockState neighborState, Direction dir) {
        return this.getBlock().pl$matchesStickyConditions(this.asState(), neighborState, dir);
    }

    // BlockStateQuasiPower

    @Override
    public int pl$getQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return this.getBlock().pl$getQuasiSignal(this.asState(), level, pos, dir, dist);
    }

    @Override
    public int pl$getDirectQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return this.getBlock().pl$getDirectQuasiSignal(this.asState(), level, pos, dir, dist);
    }

    @Override
    public boolean pl$isQuasiConductor(BlockGetter level, BlockPos pos) {
        return this.getBlock().pl$isQuasiConductor(this.asState(), level, pos);
    }

    //endregion

    //region Override Behavior

    @Override
    public void pl$setPistonMoveBehaviorOverride(PistonMoveBehavior override) {
        this.pl$pistonMoveBehaviorOverride = override;
    }

    @Override
    public PistonMoveBehavior pl$getPistonMoveBehaviorOverride() {
        return this.pl$pistonMoveBehaviorOverride;
    }

    @Inject(
            method = "getPistonPushReaction",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pl$overridePushReaction(CallbackInfoReturnable<PushReaction> cir) {
        if (PistonLibConfig.behaviorOverrideApi) {
            if (pl$pistonMoveBehaviorOverride.isPresent()) {
                cir.setReturnValue(pl$pistonMoveBehaviorOverride.getPushReaction());
            }
        }
    }
    //endregion
}
