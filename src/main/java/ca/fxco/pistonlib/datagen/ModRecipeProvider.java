package ca.fxco.pistonlib.datagen;

import ca.fxco.api.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.api.pistonlib.recipes.pistonCrushing.builders.MultiCrushingRecipeBuilder;
import ca.fxco.api.pistonlib.recipes.pistonCrushing.builders.PairCrushingRecipeBuilder;
import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.base.ModPistonFamilies;
import ca.fxco.pistonlib.base.ModRegistries;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

	public static final Logger LOGGER = PistonLib.LOGGER;

	public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
		return new RecipeProvider(registryLookup, exporter) {
			@Override
			public void buildRecipes() {
				LOGGER.info("Generating recipes...");

				for (Map.Entry<ResourceKey<PistonFamily>, PistonFamily> entry : ModRegistries.PISTON_FAMILY.entrySet()) {
					ResourceKey<PistonFamily> key = entry.getKey();
					PistonFamily family = entry.getValue();

					if (family == ModPistonFamilies.VANILLA) {
						continue;
					}

					LOGGER.info("Generating recipes for piston family " + key.location() + "...");

					Block normalBase = family.getBase(PistonType.DEFAULT);
					Block stickyBase = family.getBase(PistonType.STICKY);

					if (normalBase != null && stickyBase != null && normalBase.asItem() != Items.AIR &&
							stickyBase.asItem() != Items.AIR) {
						offerStickyPistonRecipe(exporter, stickyBase, normalBase);
					}
				}

				LOGGER.info("Finished generating recipes for pistons, generating for other items...");

				offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
				offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_SLIME_BLOCK, Blocks.SLIME_BLOCK);
				offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_STONE_BLOCK, Blocks.STONE);

				generateRecipes(new BlockFamily.Builder(Blocks.OBSIDIAN).slab(ModBlocks.OBSIDIAN_SLAB_BLOCK).stairs(ModBlocks.OBSIDIAN_STAIR_BLOCK).getFamily(), FeatureFlags.VANILLA_SET);

				//SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.IRON_ORE), Items.RAW_IRON).save(exporter);
				//SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.COPPER_ORE), Items.RAW_COPPER).save(exporter);
				//SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.GOLD_ORE), Items.RAW_GOLD).save(exporter);

				//SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.STONE_BRICKS), Items.CRACKED_STONE_BRICKS).mustBeAgainst(Blocks.OBSIDIAN).save(exporter);
				//SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.STONE_BRICKS), Items.CRACKED_STONE_BRICKS)
				//		.hasConditional(SingleCrushingConditionalRecipe.Condition.HIGHER_RESISTANCE, 1199F).save(exporter);
				//offerCrushingCrackedRecipe(exporter, Blocks.STONE_BRICKS, Items.CRACKED_STONE_BRICKS);

				PairCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.OAK_PLANKS), Ingredient.of(Blocks.OAK_PLANKS), Items.STICK.getDefaultInstance()).save(exporter);
				PairCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.COAL_BLOCK), Ingredient.of(Items.COAL), Items.DIAMOND.getDefaultInstance()).save(exporter);
				PairCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.DIAMOND_BLOCK), Ingredient.of(Items.DIAMOND), Items.DIAMOND_CHESTPLATE.getDefaultInstance()).save(exporter);

				ItemStack result = Blocks.DIORITE.asItem().getDefaultInstance();
				result.setCount(3);
				MultiCrushingRecipeBuilder.crushing(List.of(Ingredient.of(Blocks.STONE), Ingredient.of(Items.QUARTZ), Ingredient.of(Blocks.ANDESITE)), result).save(exporter);

				LOGGER.info("Finished generating recipes!");
			}

			public void offerSlipperyBlockRecipe(RecipeOutput exporter, Block slipperyBlock, Block baseBlock) {
				ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, slipperyBlock, 1).requires(baseBlock).requires(Items.POTION).unlockedBy(getHasName(baseBlock), has(baseBlock)).save(exporter);
			}

			public void offerStickyPistonRecipe(RecipeOutput exporter, Block stickyPiston, Block regularPiston) {
				ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.REDSTONE, stickyPiston).define('P', regularPiston).define('S', Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has(Items.SLIME_BALL)).save(exporter);
			}
		};
	}

	@Override
	public String getName() {
		return "PistonLibRecipeProvider";
	}

	//public void offerCrushingCrackedRecipe(Consumer<FinishedRecipe> exporter, Block block, Item item) {
	//	SingleCrushingRecipeBuilder.crushing(Ingredient.of(block), item)
	//			.hasConditional(SingleCrushingConditionalRecipe.Condition.HIGHER_RESISTANCE, 1199F).save(exporter);
	//}
}
