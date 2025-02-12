package ca.fxco.pistonlib.mixin.blockEntity;

import ca.fxco.pistonlib.PistonLibConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntity_waterloggedMixin {

    @WrapOperation(
            method = "finalTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;" +
                            "updateFromNeighbourShapes(Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)" +
                            "Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState pl$removeWaterloggedBlockState(BlockState state, LevelAccessor level,
                                                      BlockPos pos, Operation<BlockState> original) {
        BlockState newState = original.call(state, level, pos);
        if (PistonLibConfig.pistonsPushWaterloggedBlocks == PistonLibConfig.WaterloggedState.NONE &&
                newState.hasProperty(BlockStateProperties.WATERLOGGED) &&
                newState.getValue(BlockStateProperties.WATERLOGGED)) {
            newState = newState.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        return newState;
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z"
            )
    )
    private static boolean pl$allowWaterloggedBlockState(BlockState instance, Property<?> property,
                                                         Operation<Boolean> original) {
        if (PistonLibConfig.pistonsPushWaterloggedBlocks == PistonLibConfig.WaterloggedState.ALL) {
            return false; // Prevents water from being removed
        }
        return original.call(instance, property);
    }
}
