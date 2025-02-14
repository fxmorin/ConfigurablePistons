package ca.fxco.pistonlib.datagen;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.slf4j.Logger;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {

    public static final Logger LOGGER = PistonLib.LOGGER;

	public static final ModelTemplate TEMPLATE_PISTON_ARM = new ModelTemplate(Optional.of(PistonLib.id("block/template_piston_arm")), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate TEMPLATE_PISTON_ARM_SHORT = new ModelTemplate(Optional.of(PistonLib.id("block/template_piston_arm_short")), Optional.empty(), TextureSlot.TEXTURE);
	public static final ModelTemplate TEMPLATE_PARTICLE_ONLY = new ModelTemplate(Optional.of(PistonLib.id("block/template_empty")), Optional.empty(), TextureSlot.PARTICLE);
	public static final ModelTemplate PISTON_BASE = new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/piston_extended")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.INSIDE);

	public ModModelProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
		LOGGER.info("Generating blockstate definitions and models...");

		TextureMapping particleOnlyTextureMap = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
		createTrivialBlock(ca.fxco.pistonlib.base.ModBlocks.MERGE_BLOCK, particleOnlyTextureMap, TEMPLATE_PARTICLE_ONLY, generator);

		LOGGER.info("Finished generating blockstate definitions and models!");
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerators) {
	}

	// Don't remove, it's used by other mods
	public static void createTrivialBlock(Block block, TextureMapping textureMapping, ModelTemplate modelTemplate, BlockModelGenerators generators) {
		ResourceLocation resourceLocation = modelTemplate.create(block, textureMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, resourceLocation));
	}

	// Don't remove, it's used by other mods
	public static void registerPistonFamily(BlockModelGenerators generator, PistonFamily family) {
		boolean customTextures = family.hasCustomTextures();
		Block textureBaseBlock = customTextures ? family.getBase() : Blocks.PISTON;

		Block base = family.getBase();
		Block normalBase = family.getBase(PistonType.DEFAULT);
		Block stickyBase = family.getBase(PistonType.STICKY);
		Block arm = family.getArm();
		Block head = family.getHead();
		Block moving = family.getMoving();

		ResourceLocation sideTextureId = TextureMapping.getBlockTexture(textureBaseBlock, "_side");

		TextureMapping textureMap = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(textureBaseBlock, "_bottom")).put(TextureSlot.SIDE, sideTextureId);

		TextureMapping baseTextureMap = textureMap.copyAndUpdate(TextureSlot.INSIDE, TextureMapping.getBlockTexture(textureBaseBlock, "_inner"));

		ResourceLocation baseModelId = PISTON_BASE.createWithSuffix(base, "_base", baseTextureMap, generator.modelOutput);

		ResourceLocation topRegularTextureId = TextureMapping.getBlockTexture(textureBaseBlock, "_top");
		ResourceLocation topStickyTextureId = TextureMapping.getBlockTexture(textureBaseBlock, "_top_sticky");

		if (normalBase != null) {
			TextureMapping regularTextureMap = textureMap.copyAndUpdate(TextureSlot.PLATFORM, topRegularTextureId);
			generator.createPistonVariant(normalBase, baseModelId, regularTextureMap);
			ResourceLocation regularInventoryModelId = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(normalBase, "_inventory", textureMap.copyAndUpdate(TextureSlot.TOP, topRegularTextureId), generator.modelOutput);
			if (normalBase.asItem() != Items.AIR) generator.registerSimpleItemModel(normalBase, regularInventoryModelId);
		}

		if (stickyBase != null) {
			TextureMapping stickyTextureMap = textureMap.copyAndUpdate(TextureSlot.PLATFORM, topStickyTextureId);
			generator.createPistonVariant(stickyBase, baseModelId, stickyTextureMap);
			ResourceLocation stickyInventoryModelId = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(stickyBase, "_inventory", textureMap.copyAndUpdate(TextureSlot.TOP, topStickyTextureId), generator.modelOutput);
			if (stickyBase.asItem() != Items.AIR) generator.registerSimpleItemModel(stickyBase, stickyInventoryModelId);
		}

		if (head != null) {
			TextureMapping baseHeadTextureMap = new TextureMapping().put(TextureSlot.UNSTICKY, topRegularTextureId).put(TextureSlot.SIDE, sideTextureId);
			TextureMapping regularHeadTextureMap = baseHeadTextureMap.copyAndUpdate(TextureSlot.PLATFORM, topRegularTextureId);
			TextureMapping stickyHeadTextureMap = baseHeadTextureMap.copyAndUpdate(TextureSlot.PLATFORM, topStickyTextureId);

			generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(head).with(
					PropertyDispatch.properties(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE)
							.select(false, PistonType.DEFAULT, Variant.variant().with(
									VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(base, "_head", regularHeadTextureMap, generator.modelOutput)))
							.select(false, PistonType.STICKY, Variant.variant().with(
									VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(base, "_head_sticky", stickyHeadTextureMap, generator.modelOutput)))
							.select(true, PistonType.DEFAULT, Variant.variant().with(
									VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(base, "_head_short", regularHeadTextureMap, generator.modelOutput)))
							.select(true, PistonType.STICKY, Variant.variant().with(
									VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(base, "_head_short_sticky", stickyHeadTextureMap, generator.modelOutput)))
			).with(BlockModelGenerators.createFacingDispatch()));
		}

		if (arm != null) {
			TextureMapping armTextureMap = new TextureMapping().put(TextureSlot.TEXTURE, sideTextureId);

			generator.blockStateOutput.accept((MultiVariantGenerator.multiVariant(arm).with(
					PropertyDispatch.property(BlockStateProperties.SHORT)
							.select(false, Variant.variant().with(
									VariantProperties.MODEL, TEMPLATE_PISTON_ARM.createWithSuffix(base, "_arm", armTextureMap, generator.modelOutput)))
							.select(true, Variant.variant().with(
									VariantProperties.MODEL, TEMPLATE_PISTON_ARM_SHORT.createWithSuffix(base, "_arm_short", armTextureMap, generator.modelOutput)))
			).with(BlockModelGenerators.createFacingDispatch())));
		}

		if (moving != null) {
			TextureMapping movingPistonTextureMap = new TextureMapping().put(TextureSlot.PARTICLE, sideTextureId);

			createTrivialBlock(moving, movingPistonTextureMap, TEMPLATE_PARTICLE_ONLY, generator);
		}
	}
}
