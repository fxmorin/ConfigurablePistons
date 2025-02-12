package ca.fxco.pistonlib.datagen;

import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PistonLibDatagen implements DataGeneratorEntrypoint {

    public static final Logger LOGGER = PistonLib.LOGGER;

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		LOGGER.info("Starting PistonLib datagen...");

		FabricDataGenerator.Pack pack = dataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
	}
}
