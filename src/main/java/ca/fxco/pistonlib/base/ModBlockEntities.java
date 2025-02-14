package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModBlockEntities {

    // Other
    public static final BlockEntityType<MergeBlockEntity> MERGE_BLOCK_ENTITY;

    static {
        // Other
        MERGE_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("merge"),
                FabricBlockEntityTypeBuilder.create(MergeBlockEntity::new, ModBlocks.MERGE_BLOCK).build()
        );
    }

    private static <T extends BasicMovingBlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<T> factory
    ) {
        return register(id(name+"_moving_block"), factory);
    }

    /**
     * Builds a block entity type from the given factories and
     * registers it to the given namespaced id.
     * <br>
     * No blocks need to be passed here since they're added
     * to the block entity type after the corresponding piston
     * families are registered.
     *
     * @param <T>      the type of moving block entity
     * @param id       a namespaced id to uniquely identify the block
     *                 entity type
     * @param factory  the block entity factory for the block
     *                 entity registry
     * @return the block entity type that was registered
     */
    private static <T extends BasicMovingBlockEntity> BlockEntityType<T> register(
            ResourceLocation id,
            FabricBlockEntityTypeBuilder.Factory<T> factory
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.create(factory).build()
        );
    }

    public static void bootstrap() {}
}
