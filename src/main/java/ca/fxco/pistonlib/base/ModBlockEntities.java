package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.autoCraftingBlock.AutoCraftingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.configurablePiston.ConfigurableMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.fastPiston.FastMovingBlockEntity;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.speedPiston.SpeedMovingBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModBlockEntities {

    // Pistons
    public static final BlockEntityType<BasicMovingBlockEntity> BASIC_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<ConfigurableMovingBlockEntity> CONFIGURABLE_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<SpeedMovingBlockEntity> SPEED_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<FastMovingBlockEntity> FAST_MOVING_BLOCK_ENTITY;
    public static final BlockEntityType<MBEMovingBlockEntity> MBE_MOVING_BLOCK_ENTITY;

    // Other
    public static final BlockEntityType<MergeBlockEntity> MERGE_BLOCK_ENTITY;
    public static final BlockEntityType<AutoCraftingBlockEntity> AUTO_CRAFTING_BLOCK_ENTITY;

    static {
        // Pistons
        BASIC_MOVING_BLOCK_ENTITY = register(
                "basic",
                BasicMovingBlockEntity::new
        );
        CONFIGURABLE_MOVING_BLOCK_ENTITY = register(
                "configurable",
                ConfigurableMovingBlockEntity::new
        );
        SPEED_MOVING_BLOCK_ENTITY = register(
                "speed",
                SpeedMovingBlockEntity::new
        );
        FAST_MOVING_BLOCK_ENTITY = register(
                "fast",
                FastMovingBlockEntity::new
        );
        MBE_MOVING_BLOCK_ENTITY = register(
                "mbe",
                MBEMovingBlockEntity::new
        );

        // Other
        MERGE_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("merge"),
                FabricBlockEntityTypeBuilder.create(MergeBlockEntity::new, ModBlocks.MERGE_BLOCK).build(null)
        );
        AUTO_CRAFTING_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("auto_crafting_block"),
                FabricBlockEntityTypeBuilder.create(AutoCraftingBlockEntity::new, ModBlocks.AUTO_CRAFTING_BLOCK)
                        .build(null)
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
     * No blocks need to be passed here since they are added
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
