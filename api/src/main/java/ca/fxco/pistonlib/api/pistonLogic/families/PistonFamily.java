package ca.fxco.pistonlib.api.pistonLogic.families;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

import ca.fxco.pistonlib.api.pistonLogic.structure.StructureGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

/**
 * A piston family is a collection of blocks and a block entity type
 * that form a set a pistons that act according to the piston behavior
 * as defined in the family.
 * <p>
 * A piston family holds references to piston base blocks for each
 * {@link net.minecraft.world.level.block.state.properties.PistonType
 * PistonType}, a piston head block, a moving block, a moving block
 * entity type, and a moving block entity factory. If the max length
 * of the pistons as defined in the piston behavior is more than 1,
 * the family must also hold a reference to a piston arm block.
 * 
 * @author FX
 * @since 1.2.0
 */
public interface PistonFamily {

    /**
     * Gets the arm block used by the family
     *
     * @return The arm block, or {@code null} if this family doesn't use an arm
     * @since 1.2.0
     */
    @Nullable Block getArm();

    /**
     * Gets the head block used by the family
     *
     * @return The head block
     * @since 1.2.0
     */
    Block getHead();

    /**
     * Gets the moving block used by the family
     *
     * @return The moving block
     * @since 1.2.0
     */
    Block getMoving();

    /**
     * Gets the moving block entity type
     *
     * @return The moving block entity type
     * @since 1.2.0
     */
    BlockEntityType<? extends PistonMovingBlockEntity> getMovingBlockEntityType();

    /**
     * Gets the factory used to create moving block entities
     *
     * @return The moving block entity factory
     * @since 1.2.0
     */
    Factory<? extends PistonMovingBlockEntity> getMovingBlockEntityFactory();

    /**
     * Gets an arbitrary base block used by this family
     *
     * @return an arbitrary base block
     * @since 1.2.0
     */
    Block getBase();

    /**
     * Gets a base block from this family, for a specific {@link PistonType}
     *
     * @param type The piston type that we want the base for
     * @return The base block for the piston type, or {@code null} if no base block exists for that piston type
     * @since 1.2.0
     */
    @Nullable Block getBase(PistonType type);

    /**
     * The base blocks used by this family
     *
     * @return A map containing the base blocks.
     * @since 1.2.0
     */
    Map<PistonType, Block> getBases();

    /**
     * If this piston has a custom texture
     *
     * @return {@code true} if this piston has a custom texture, otherwise {@code false}
     * @since 1.2.0
     */
    boolean hasCustomTextures();

    /**
     * If this piston uses a custom length.
     * A custom length over 1, makes it use the piston arm.
     *
     * @return {@code true} if this piston is using a custom length, otherwise {@code false}
     * @since 1.2.0
     */
    default boolean hasCustomLength() {
        return getMinLength() != 0 || getMaxLength() != 1;
    }

    /**
     * Creates a new moving block entity.
     *
     * @param pos              The position to create the block entity at
     * @param state            The state of the moving block to make the block entity for
     * @param movedState       The state of the block being moved within the moving block entity
     * @param movedBlockEntity The block entity of the block being moved by the moving block entity
     * @param facing           The direction that the moving block entity is moving towards
     * @param extending        If the moving block entity is extending or retracting
     * @param isSourcePiston   If this is the source piston block entity. Which means it's a piston head
     * @return The new moving block entity
     * @since 1.2.0
     */
    PistonMovingBlockEntity newMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                                 BlockEntity movedBlockEntity, Direction facing, boolean extending,
                                                 boolean isSourcePiston);

    /**
     * Creates a new moving block entity, using a structure group.
     *
     * @param structureGroup   The structure group that the block entity is a part of
     * @param pos              The position to create the block entity at
     * @param state            The state of the moving block to make the block entity for
     * @param movedState       The state of the block being moved within the moving block entity
     * @param movedBlockEntity The block entity of the block being moved by the moving block entity
     * @param facing           The direction that the moving block entity is moving towards
     * @param extending        If the moving block entity is extending or retracting
     * @param isSourcePiston   If this is the source piston block entity. Which means it's a piston head
     * @return The new moving block entity
     * @since 1.2.0
     */
    PistonMovingBlockEntity newMovingBlockEntity(StructureGroup structureGroup, BlockPos pos, BlockState state,
                                                 BlockState movedState, BlockEntity movedBlockEntity, Direction facing,
                                                 boolean extending, boolean isSourcePiston);

    //region PistonBehavior Delegate

    /**
     * Check if this is a very sticky piston.
     * This makes the front of the sticky piston stick like a slime block.
     *
     * @return {@code true} if the piston is very sticky, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isVerySticky();

    /**
     * Check if this piston can be powered from the front.
     *
     * @return {@code true} if the piston can be powered from the front, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isFrontPowered();

    /**
     * If this piston is quasi.
     * If it's affected by quasi-connectivity
     *
     * @return {@code true} if the piston is affected by quasi-connectivity, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isQuasi();

    /**
     * Gets the piston push limit
     *
     * @return The push limit
     * @since 1.2.0
     */
    int getPushLimit();

    /**
     * Gets the extending speed
     *
     * @return The extending speed
     * @since 1.2.0
     */
    float getExtendingSpeed();

    /**
     * Gets the retracting speed
     *
     * @return The retracting speed
     * @since 1.2.0
     */
    float getRetractingSpeed();

    /**
     * If this piston is allowed to retract while its extending
     *
     * @return {@code true} if its able to retract while extending, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isRetractOnExtending();

    /**
     * If this piston is allowed to extend while its retracting
     *
     * @return {@code true} if its able to extend while retracting, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isExtendOnRetracting();

    /**
     * Gets the minimum length (in blocks) that the piston can be.
     * Vanilla is 0
     *
     * @return The minimum length
     * @since 1.2.0
     */
    int getMinLength();

    /**
     * Gets the maximum length (in blocks) that the piston can be.
     * Vanilla is 1
     *
     * @return The maximum length
     * @since 1.2.0
     */
    int getMaxLength();

    //endregion

    /**
     * The PistonFamily builder
     *
     * @since 1.2.0
     */
    interface Builder {

        /**
         * Sets the head block used by the family
         *
         * @param head The head block
         * @return The builder
         * @since 1.2.0
         */
        Builder head(Block head);

        /**
         * Sets the arm block used by the family
         *
         * @param arm The arm block
         * @return The builder
         * @since 1.2.0
         */
        Builder arm(@Nullable Block arm);

        /**
         * Sets the moving block used by the family
         *
         * @param movingBlock The moving block
         * @return The builder
         * @since 1.2.0
         */
        Builder moving(Block movingBlock);

        /**
         * Sets the base block used by the family
         *
         * @param type The piston type to set the base block for
         * @param base The base block for this piston type
         * @return The builder
         * @since 1.2.0
         */
        Builder base(PistonType type, Block base);

        /**
         * Sets the {@link PistonBehavior} used by this family
         *
         * @param behavior The piston behavior
         * @return The builder
         * @since 1.2.0
         */
        Builder behavior(PistonBehavior behavior);

        /**
         * Sets the {@link PistonBehavior} used by this family
         *
         * @param builder The piston behavior builder
         * @return The builder
         * @since 1.2.0
         */
        Builder behavior(PistonBehavior.Builder builder);

        /**
         * Sets the moving block entity type and factory for the family
         *
         * @param type    The block entity type
         * @param factory The moving block entity factory
         * @return The builder
         * @since 1.2.0
         */
        <T extends PistonMovingBlockEntity> Builder movingBlockEntity(BlockEntityType<T> type, Factory<T> factory);

        /**
         * Sets if this piston family uses custom textures
         *
         * @param customTextures If it uses custom textures
         * @return The builder
         * @since 1.2.0
         */
        Builder customTextures(boolean customTextures);

        /**
         * Builds the {@link PistonFamily}
         *
         * @return The new piston family
         * @since 1.2.0
         */
        PistonFamily build();
    }

    /**
     * Wraps a fabric factory.
     * This should only be used for vanilla piston block entities!
     *
     * @param fabricFactory The fabric/vanilla moving block entity factory
     * @return A new factory, which wraps the fabric factory
     * @since 1.2.0
     */
    static <T extends PistonMovingBlockEntity> Factory<T> wrapFabricFactory(
            BiFunction<BlockPos, BlockState, T> fabricFactory
    ) {
        return (family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston) ->
                fabricFactory.apply(pos, state);
    }

    /**
     * Wraps a fabric factory.
     * This should only be used for vanilla piston block entities!
     *
     * @return A new factory, which wraps the fabric factory
     * @since 1.2.0
     */
    static Factory<PistonMovingBlockEntity> createVanillaFactory() {
        return (family, group, pos, state, movedState,
                movedBlockEntity, facing, extending, isSourcePiston) ->
                new PistonMovingBlockEntity(pos, state, movedState, facing, extending, isSourcePiston);
    }

    /**
     * Conditionally use two different Fabric Factories.
     * These conditions are done within the factory function, so that they can be changed dynamically.
     * Fabric factories are used by pistonlib block entities to initialize the block entity type.
     *
     * @param condition    The boolean supplier which is checked to determine which factory to use
     * @param trueFactory  The fabric factory used if the condition returns {@code true}
     * @param falseFactory The fabric factory used if the condition returns {@code false}
     * @return A new fabric factory, which can conditionally change between two different fabric factories
     * @since 1.2.0
     */
    static <T extends PistonMovingBlockEntity> FabricBlockEntityTypeBuilder.Factory<T> conditionalFabricFactory(
            BooleanSupplier condition,
            FabricBlockEntityTypeBuilder.Factory<T> trueFactory,
            FabricBlockEntityTypeBuilder.Factory<T> falseFactory
    ) {
        return (pos, state) -> (condition.getAsBoolean() ? trueFactory : falseFactory).create(pos, state);
    }

    /**
     * Conditionally use two different Factories.
     * These conditions are done within the factory function, so that they can be changed dynamically.
     *
     * @param condition    The boolean supplier which is checked to determine which factory to use
     * @param trueFactory  The factory used if the condition returns {@code true}
     * @param falseFactory The factory used if the condition returns {@code false}
     * @return A new factory, which can conditionally change between two different factories
     * @since 1.2.0
     */
    static <T extends PistonMovingBlockEntity> Factory<T> conditionalFactory(BooleanSupplier condition,
                                                                             Factory<T> trueFactory,
                                                                             Factory<T> falseFactory) {
        return (family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSource) ->
                (condition.getAsBoolean() ? trueFactory : falseFactory)
                        .create(family, group, pos, state, movedState, movedBlockEntity, facing, extending, isSource);
    }

    /**
     * Factory used to create PistonMovingBlockEntities
     *
     * @param <T> The type of the piston moving block entity
     * @since 1.2.0
     */
    @FunctionalInterface
    interface Factory<T extends PistonMovingBlockEntity> {

        /**
         * Creates a new moving block entity, using a structure group.
         *
         * @param family           The piston family used to create the moving block entity
         * @param structureGroup   The structure group that the block entity is a part of
         * @param pos              The position to create the block entity at
         * @param state            The state of the moving block to make the block entity for
         * @param movedState       The state of the block being moved within the moving block entity
         * @param movedBlockEntity The block entity of the block being moved by the moving block entity
         * @param facing           The direction that the moving block entity is moving towards
         * @param extending        If the moving block entity is extending or retracting
         * @param isSourcePiston   If this is the source piston block entity. Which means it's a piston head
         * @return The new moving block entity
         * @since 1.2.0
         */
        T create(PistonFamily family, StructureGroup structureGroup, BlockPos pos, BlockState state,
                 BlockState movedState, BlockEntity movedBlockEntity, Direction facing, boolean extending,
                 boolean isSourcePiston);
    }
}
