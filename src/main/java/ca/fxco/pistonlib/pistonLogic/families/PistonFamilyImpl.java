package ca.fxco.pistonlib.pistonLogic.families;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonBehavior;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamilies;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@AllArgsConstructor
public class PistonFamilyImpl implements PistonFamily {

    @Delegate(types = PistonBehavior.class, excludes = PistonBehavior.Builder.class)
    private final PistonBehavior behavior;

    private final Map<PistonType, Block> bases;
    @Getter
    private final @Nullable Block arm;
    @Getter
    private final Block head;
    @Getter
    private final Block moving;
    @Getter
    private final BlockEntityType<? extends PistonMovingBlockEntity> movingBlockEntityType;
    @Getter
    private final PistonFamily.Factory<? extends PistonMovingBlockEntity> movingBlockEntityFactory;

    private final boolean customTextures;

    @Override
    public Map<PistonType, Block> getBases() {
        return Collections.unmodifiableMap(this.bases);
    }

    @Override
    public @Nullable Block getBase(PistonType type) {
        return this.bases.get(type);
    }
    
    @Override
    public Block getBase() {
        return this.bases.values().iterator().next();
    }

    @Override
    public boolean hasCustomTextures() {
        return customTextures && PistonLib.DATAGEN_ACTIVE;
    }

    @Override
    public PistonMovingBlockEntity newMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                                        BlockEntity movedBlockEntity, Direction facing,
                                                        boolean extending, boolean isSourcePiston) {
        return this.movingBlockEntityFactory.create(
                this, null,
                pos, state, movedState,
                movedBlockEntity,
                facing, extending, isSourcePiston
        );
    }

    @Override
    public PistonMovingBlockEntity newMovingBlockEntity(StructureGroup structureGroup, BlockPos pos, BlockState state,
                                                        BlockState movedState, BlockEntity movedBlockEntity,
                                                        Direction facing, boolean extending, boolean isSourcePiston) {
        return this.movingBlockEntityFactory.create(
                this, structureGroup,
                pos, state, movedState,
                movedBlockEntity,
                facing, extending, isSourcePiston
        );
    }

    @Override
    public boolean hasCustomLength() {
        return this.behavior.getMinLength() != 0 || this.behavior.getMaxLength() != 1;
    }

    @Override
    public String toString() {
        return "PistonFamily{" + PistonFamilies.getId(this) + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements PistonFamily.Builder {

        protected final Map<PistonType, Block> bases = new EnumMap<>(PistonType.class);
        protected Block head;
        protected @Nullable Block arm;
        protected Block moving;
        protected PistonBehavior behavior;
        protected BlockEntityType<? extends PistonMovingBlockEntity> movingBlockEntityType;
        protected PistonFamily.Factory<? extends PistonMovingBlockEntity> movingBlockEntityFactory;
        protected boolean customTextures;

        @Override
        public Builder base(PistonType type, Block base) {
            this.bases.put(type, base);
            return this;
        }

        @Override
        public Builder head(Block head) {
            this.head = head;
            return this;
        }

        @Override
        public Builder arm(@Nullable Block arm) {
            this.arm = arm;
            return this;
        }

        @Override
        public Builder moving(Block movingBlock) {
            this.moving = movingBlock;
            return this;
        }

        @Override
        public Builder behavior(PistonBehavior behavior) {
            this.behavior = behavior;
            return this;
        }

        @Override
        public Builder behavior(PistonBehavior.Builder builder) {
            this.behavior = builder.build();
            return this;
        }

        @Override
        public <T extends PistonMovingBlockEntity> Builder movingBlockEntity(
                BlockEntityType<T> type, Factory<T> factory
        ) {
            this.movingBlockEntityType = type;
            this.movingBlockEntityFactory = factory;
            return this;
        }

        @Override
        public Builder customTextures(boolean customTextures) {
            this.customTextures = customTextures;
            return this;
        }

        @Override
        public PistonFamily build() {
            return new PistonFamilyImpl(behavior, bases, arm, head, moving,
                    movingBlockEntityType, movingBlockEntityFactory, customTextures);
        }
    }
}
