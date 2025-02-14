package ca.fxco.pistonlib.blocks.pistons.movableBlockEntities;

import ca.fxco.pistonlib.api.block.MovingTickable;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureGroup;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Getter
public class MBEMovingBlockEntity extends BasicMovingBlockEntity {

    protected BlockEntity movedBlockEntity;

    public MBEMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public MBEMovingBlockEntity(PistonFamily family, StructureGroup group, BlockPos pos, BlockState state,
                                BlockState movedState, BlockEntity movedBlockEntity, Direction facing,
                                boolean extending, boolean isSourcePiston) {
        super(family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);

        this.movedBlockEntity = movedBlockEntity;
    }

    @Override
    protected boolean placeMovedBlock() {
        this.level.pl$prepareBlockEntityPlacement(this.worldPosition, this.movedState, this.movedBlockEntity);
        return super.placeMovedBlock();
    }

    @Override
    public void onMovingTick(Direction movingDirection, float speed) {
        super.onMovingTick(movingDirection, speed);
        if (this.movedBlockEntity instanceof MovingTickable tickable) {
            tickable.pl$movingTick(this.level, this.movedState, this.worldPosition,
                    movingDirection, this.progressO, speed, false);
        }
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider lookup) {
        super.loadAdditional(nbt, lookup);

        if (this.movedState.hasBlockEntity() && nbt.contains("blockEntity")) {
            EntityBlock movedBlock = (EntityBlock)this.movedState.getBlock();
            this.movedBlockEntity = movedBlock.newBlockEntity(this.worldPosition, this.movedState);

            this.movedBlockEntity.loadCustomOnly(nbt.getCompound("blockEntity"), lookup);
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider lookup) {
        super.saveAdditional(nbt, lookup);

        if (this.movedState.hasBlockEntity() && this.movedBlockEntity != null) {
            nbt.put("blockEntity", this.movedBlockEntity.saveWithoutMetadata(lookup));
        }
    }
}
