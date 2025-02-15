package ca.fxco.pistonlib.blocks.pistons.movableBlockEntities;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class MBEMovingBlock extends BasicMovingBlock {

    public MBEMovingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Level level = builder.getLevel();
        Vec3 origin = builder.getParameter(LootContextParams.ORIGIN);
        MBEMovingBlockEntity mbe = this.getMovingBlockEntity(level, BlockPos.containing(origin));

        if (mbe == null) {
            return Collections.emptyList();
        }

        BlockState movedState = mbe.getMovedState();
        BlockEntity movedBlockEntity = mbe.getMovedBlockEntity();

        if (mbe == builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)) {
            builder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, movedBlockEntity);
        }

        return movedState.getDrops(builder);
    }

    @Nullable
    private MBEMovingBlockEntity getMovingBlockEntity(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MBEMovingBlockEntity mbe ? mbe : null;
    }
}
