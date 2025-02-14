package ca.fxco.pistonlib.renderers;

import ca.fxco.pistonlib.blocks.pistons.movableBlockEntities.MBEMovingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.structure.StructureGroup;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.helpers.BlockAndTintWrapper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@Environment(EnvType.CLIENT)
public class BasicMovingBlockEntityRenderer<T extends BasicMovingBlockEntity> implements BlockEntityRenderer<T> {

    private static final boolean DEBUG_CONTROLLERS = false;
    private static final boolean DEBUG_AS_OVERLAY = true;

    protected final BlockEntityRendererProvider.Context context;

    public BasicMovingBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(T mbe, float partialTick, PoseStack stack, MultiBufferSource bufferSource,
                       int light, int overlay) {
        Level level = mbe.getLevel();
        if (level == null) {
            return;
        }

        BlockState state = mbe.getMovedState();

        if (state.isAir()) {
            return;
        }

        ModelBlockRenderer.enableCaching();
        stack.pushPose();
        stack.translate(mbe.getXOff(partialTick), mbe.getYOff(partialTick), mbe.getZOff(partialTick));

        Direction moveDir = mbe.getMovementDirection();
        BlockPos toPos = mbe.getBlockPos();
        BlockPos fromPos = toPos.relative(moveDir.getOpposite());

        if (DEBUG_CONTROLLERS) {
            if (mbe.hasControl()) {
                if (DEBUG_AS_OVERLAY) {
                    stack.pushPose();
                    stack.translate(-0.05F, -0.05F, -0.05F);
                    stack.scale(1.1F, 1.1F, 1.1F);
                    if (mbe.getStructureGroup() == null) { // This block is alone
                        renderDebugBlock(level, Blocks.ORANGE_STAINED_GLASS, fromPos, stack, bufferSource, overlay);
                    } else {
                        renderDebugBlock(level, Blocks.LIME_STAINED_GLASS, fromPos, stack, bufferSource, overlay);
                    }
                    stack.popPose();
                } else {
                    if (mbe.getStructureGroup() == null) { // This block is alone
                        renderDebugBlock(level, Blocks.ORANGE_CONCRETE, fromPos, stack, bufferSource, overlay);
                    } else {
                        renderDebugBlock(level, Blocks.LIME_CONCRETE, fromPos, stack, bufferSource, overlay);
                    }
                    stack.popPose();
                    ModelBlockRenderer.clearCache();
                    return;
                }
            }
        }

        if (mbe.isSourcePiston()) {
            this.renderMovingSource(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        } else {
            this.renderMovingBlock(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        }

        stack.popPose();
        stack.pushPose();

        if (mbe.isSourcePiston()) {
            this.renderStaticSource(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        } else {
            this.renderStaticBlock(mbe, level, fromPos, toPos, partialTick, stack, bufferSource, light, overlay);
        }

        stack.popPose();
        ModelBlockRenderer.clearCache();
    }

    protected void renderMovingSource(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick,
                                      PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = mbe.getMovedState();

        if (mbe.isExtending()) {
            if (state.getBlock() instanceof BasicPistonHeadBlock) {
                this.renderBlock(mbe, fromPos, state.setValue(BasicPistonHeadBlock.SHORT,
                                mbe.getProgress(partialTick) <= 0.5F), stack, bufferSource, level, false, overlay);
            }
        } else if (state.getBlock() instanceof BasicPistonBaseBlock base) {
            PistonFamily family = mbe.getFamily();
            Direction facing = state.getValue(BasicPistonBaseBlock.FACING);

            BlockState headState = family.getHead().defaultBlockState()
                .setValue(BasicPistonHeadBlock.TYPE, base.pl$getPistonController().getType())
                .setValue(BasicPistonHeadBlock.FACING, facing)
                .setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) >= 0.5F);

            this.renderBlock(mbe, fromPos, headState, stack, bufferSource, level, false, overlay);
        } else if (state.getBlock() instanceof BasicPistonHeadBlock) {
            BlockState headState = state
                    .setValue(BasicPistonHeadBlock.SHORT, mbe.getProgress(partialTick) >= 0.5F);

            this.renderBlock(mbe, fromPos, headState, stack, bufferSource, level, false, overlay);
        }
    }

    protected void renderStaticSource(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick,
                                      PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        if (!mbe.isExtending()) {
            BlockState state = mbe.getMovedState();
            if (state.getBlock() instanceof BasicPistonBaseBlock) {
                this.renderBlock(mbe, fromPos, state.setValue(BlockStateProperties.EXTENDED, true),
                        stack, bufferSource, level, false, overlay);
            } else if (state.getBlock() instanceof BasicPistonHeadBlock) {
                PistonFamily family = mbe.getFamily();
                Direction facing = state.getValue(BasicPistonHeadBlock.FACING);

                BlockState armState = family.getArm().defaultBlockState()
                        .setValue(BasicPistonHeadBlock.FACING, facing)
                        .setValue(BasicPistonHeadBlock.SHORT, false);

                this.renderBlock(mbe, fromPos, armState, stack, bufferSource, level, false, overlay);
            }
        }
    }

    protected void renderMovingBlock(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick,
                                     PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = mbe.getMovedState();

        if (state.getBlock() instanceof BasicPistonHeadBlock) {
            this.renderBlock(mbe, fromPos, state.setValue(BasicPistonHeadBlock.SHORT,
                            mbe.getProgress(partialTick) <= 0.5F), stack, bufferSource, level, false, overlay);
        } else {
            this.renderBlock(mbe, fromPos, state, stack, bufferSource, level, false, overlay);
        }

        // Movable block entities
        if (mbe instanceof MBEMovingBlockEntity mbembe) {
            BlockEntity blockEntity = mbembe.getMovedBlockEntity();

            if (blockEntity != null) {
                BlockEntityRenderDispatcher blockEntityRenderer = this.context.getBlockEntityRenderDispatcher();
                blockEntityRenderer.render(blockEntity, partialTick, stack, bufferSource);
            }
        }
    }

    protected void renderStaticBlock(T mbe, Level level, BlockPos fromPos, BlockPos toPos, float partialTick,
                                     PoseStack stack, MultiBufferSource bufferSource, int light, int overlay) {
    }

    protected void renderDebugBlock(Level level, Block block, BlockPos pos, PoseStack stack,
                                    MultiBufferSource bufferSource, int overlay) {
        BlockState state = block.defaultBlockState();
        RenderType type = ItemBlockRenderTypes.getMovingBlockRenderType(state);
        VertexConsumer consumer = bufferSource.getBuffer(type);
        BlockRenderDispatcher blockRenderer = this.context.getBlockRenderDispatcher();
        blockRenderer.getModelRenderer().tesselateBlock(level, blockRenderer.getBlockModel(state), state,
                pos, stack, consumer, false, RandomSource.create(), state.getSeed(pos), overlay);
    }

    protected void renderBlock(T mbe, BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource,
                               Level level, boolean cull, int overlay) {
        RenderType type = ItemBlockRenderTypes.getMovingBlockRenderType(state);
        VertexConsumer consumer = bufferSource.getBuffer(type);

        BlockAndTintGetter getter;
        StructureGroup structureGroup = mbe.getStructureGroup();
        if (structureGroup != null) {
            getter = new BlockAndTintWrapper(level) {
                @Override
                public BlockState getBlockState(BlockPos blockPos) {
                    return structureGroup.getState(blockPos);
                }
            };
            cull = true;
        } else {
            getter = level;
        }

        BlockRenderDispatcher blockRenderer = this.context.getBlockRenderDispatcher();
        blockRenderer.getModelRenderer().tesselateBlock(getter, blockRenderer.getBlockModel(state), state,
                pos, stack, consumer, cull, RandomSource.create(), state.getSeed(pos), overlay);
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}
