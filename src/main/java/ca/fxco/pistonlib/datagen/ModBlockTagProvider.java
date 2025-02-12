package ca.fxco.pistonlib.datagen;

import java.util.concurrent.CompletableFuture;

import ca.fxco.pistonlib.api.PistonLibRegistries;
import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public static final Logger LOGGER = PistonLib.LOGGER;

	public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		LOGGER.info("Generating block tags...");

		FabricTagBuilder pistonsTag = getOrCreateTagBuilder(ModTags.PISTONS);
		FabricTagBuilder movingPistonsTag = getOrCreateTagBuilder(ModTags.MOVING_PISTONS);

		PistonLibRegistries.PISTON_FAMILY.forEach(family -> {
			family.getBases().forEach((type, base) -> pistonsTag.add(base));
			movingPistonsTag.add(family.getMoving());
		});

		getOrCreateTagBuilder(ModTags.UNPUSHABLE).add(
				Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR, Blocks.BEACON,
				Blocks.ENDER_CHEST, Blocks.ENCHANTING_TABLE, Blocks.SPAWNER
		);

		getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE).addTag(ModTags.MOVING_PISTONS);
		getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE).addTag(ModTags.MOVING_PISTONS);

		LOGGER.info("Finished generating block tags!");
	}
}
