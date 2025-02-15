package ca.fxco.pistonlib.blocks.pistons.configurablePiston;

import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.sticky.StickyType;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;

import static ca.fxco.pistonlib.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonHeadBlock extends BasicPistonHeadBlock {

    public ConfigurablePistonHeadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setFamily(PistonFamily family) {
        super.setFamily(family);

        // this should be fine since setFamily is only called during bootstrapping
        if (this.getFamily().isSlippery()) {
            this.registerDefaultState(this.defaultBlockState().setValue(SLIPPERY_DISTANCE, 0));
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (this.getFamily().isSlippery() && !oldState.is(state.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) == null) {
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
                                  BlockPos pos, Direction dir, BlockPos neighborPos, BlockState neighborState,
                                 RandomSource random) {
        if (this.getFamily().isSlippery() && !level.isClientSide())
            scheduledTickAccess.scheduleTick(pos, this, SLIPPERY_DELAY);
        return super.updateShape(state, level, scheduledTickAccess, pos, dir, neighborPos, neighborState, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.getFamily().isSlippery()) {
            int i = BaseSlipperyBlock.calculateDistance(level, pos);
            BlockState blockState = state.setValue(SLIPPERY_DISTANCE, i);
            if (blockState.getValue(SLIPPERY_DISTANCE) == MAX_DISTANCE && !super.canSurvive(state, level, pos)) {
                BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());
                if (this.isFittingBase(state, level.getBlockState(blockPos))) {
                    FallingBlockEntity.fall(level, pos, state.setValue(BlockStateProperties.EXTENDED,false));
                }
                level.removeBlock(pos, false);
            } else if (state != blockState) {
                level.setBlock(pos, blockState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!this.getFamily().isSlippery() || BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE) {
            if (this.getFamily().isVerySticky()) {
                BlockState blockState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
                return this.isFittingBase(state, blockState) || blockState.is(this.getFamily().getMoving());
            }
            return super.canSurvive(state, level, pos);
        }
        if (this.getFamily().isVerySticky()) {
            BlockState blockState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
            return this.isFittingBase(state, blockState) || blockState.is(this.getFamily().getMoving());
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT, SLIPPERY_DISTANCE);
    }

    @Override
    public boolean pl$usesConfigurablePistonBehavior() {
        return this.getFamily().isVerySticky(); // Makes the piston head movable by bypassing vanilla checks
    }

    @Override
    public boolean pl$usesConfigurablePistonStickiness() {
        return this.getFamily().isVerySticky();
    }

    // Returns a list of directions that are sticky, and the stickyType.
    public Map<Direction, StickyType> pl$stickySides(BlockState state) {
        return Map.of(state.getValue(FACING), StickyType.STICKY,
                state.getValue(FACING).getOpposite(), StickyType.STICKY);
    }

    public StickyType pl$sideStickiness(BlockState state, Direction direction) {
        return state.getValue(FACING).getAxis() == direction.getAxis() ? StickyType.STICKY : StickyType.DEFAULT;
    }

    @Override
    public boolean isFittingBase(BlockState headState, BlockState behindState) {
        return behindState.is(this.getFamily().getArm()) ?
                behindState.getValue(FACING) == headState.getValue(FACING) :
                super.isFittingBase(headState, behindState);
    }
}
