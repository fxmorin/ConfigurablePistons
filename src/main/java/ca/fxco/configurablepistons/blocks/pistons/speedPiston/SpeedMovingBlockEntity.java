package ca.fxco.configurablepistons.blocks.pistons.speedPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedMovingBlockEntity extends BasicMovingBlockEntity {

    private float speed = 1.0F;

    public SpeedMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public SpeedMovingBlockEntity(float speed, BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                  boolean extending, boolean isSourcePiston, BasicMovingBlock movingBlock) {
        this(speed, pos, state, movedState, facing, extending, isSourcePiston, movingBlock,
                ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY);
    }

    public SpeedMovingBlockEntity(float speed, BlockPos pos, BlockState state, BlockState movedState, Direction dir,
                                  boolean extending, boolean isSourcePiston, BasicMovingBlock movingBlock,
                                  BlockEntityType<?> type) {
        super(pos, state, movedState, dir, extending, isSourcePiston, movingBlock, type);

        this.setSpeed(speed);
    }

    private void setSpeed(float speed) {
        if (speed <= 0.0F) {
            throw new IllegalArgumentException("piston speed must be positive!");
        }
        if (speed > 2.0F) {
            throw new IllegalArgumentException("piston speed must be at most 2!");
        }

        this.speed = speed;
    }

    @Override
    protected float speed() {
        return speed;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        if (nbt.contains("speed")) {
            setSpeed(nbt.getFloat("speed"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (this.speed != 1.0F) {
            nbt.putFloat("speed", this.speed);
        }
    }
}
