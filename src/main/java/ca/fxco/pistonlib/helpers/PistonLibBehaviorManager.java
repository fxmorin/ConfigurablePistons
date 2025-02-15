package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.api.pistonLogic.PistonMoveBehavior;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.lang3.SerializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PistonLibBehaviorManager {

    private static final Logger LOGGER = LogManager.getLogger("PistonLib Behavior Manager");

    private static boolean dirty;

    public static boolean canChangeOverride(BlockState state) {
        return state.pl$canOverridePistonMoveBehavior();
    }

    public static PistonMoveBehavior getOverride(BlockState state) {
        return state.pl$getPistonMoveBehaviorOverride();
    }

    public static void setOverride(BlockState state, PistonMoveBehavior override) {
        if (canChangeOverride(state)) {
            state.pl$setPistonMoveBehaviorOverride(override);
            dirty = true;
        }
    }

    public static void initOverrides(boolean resetAll) {
        if (resetAll) {
            for (Block block : BuiltInRegistries.BLOCK) {
                initOverrides(block, PistonMoveBehavior.DEFAULT);
            }
        }

        initOverrides(Blocks.MOVING_PISTON, PistonMoveBehavior.BLOCK);

        // Blocks that are immovable due to having block entities, but should
        // also be immovable for other reasons (e.g. containing obsidian).
        initOverrides(Blocks.ENCHANTING_TABLE, PistonMoveBehavior.BLOCK);
        initOverrides(Blocks.BEACON, PistonMoveBehavior.BLOCK);
        initOverrides(Blocks.ENDER_CHEST, PistonMoveBehavior.BLOCK);
        initOverrides(Blocks.SPAWNER, PistonMoveBehavior.BLOCK);
    }

    private static void initOverrides(Block block, PistonMoveBehavior override) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            setOverride(state, override);
        }
    }

    public static void load() {
        LOGGER.info("Loading PistonLib move behavior overrides...");

        initOverrides(false);
        Config.load();
        dirty = false;
    }

    public static void save(boolean quietly) {
        if (dirty) {
            if (!quietly) {
                LOGGER.info("Saving PistonLib move behavior overrides...");
            }

            Config.save();
            dirty = false;
        }
    }

    public static class Config {

        private static final TomlWriter WRITER = new TomlWriter();

        /**
         * @return Empty optional if we aren't able to save values at the moment
         */
        private static Optional<Path> getConfigFile() {
            return PistonLib.getServer().map(s ->
                    s.getWorldPath(LevelResource.ROOT).resolve("pistonlib_behavior_overrides.toml"));
        }

        @SuppressWarnings("unchecked")
        public static void load() {
            getConfigFile().ifPresent(configPath -> {
                if (!Files.exists(configPath)) {
                    return;
                }
                if (!Files.isRegularFile(configPath) || !Files.isReadable(configPath)) {
                    LOGGER.warn("PistonLib behavior overrides config is not readable!");
                    return;
                }

                try {
                    Map<String, Object> configData = new Toml().read(configPath.toFile()).toMap();
                    for (Map.Entry<String, Object> entry : configData.entrySet()) {
                        loadOverrides(entry.getKey(), (Map<String, String>) entry.getValue());
                    }
                } catch (IllegalStateException e) {
                    throw new SerializationException(e);
                }
            });
        }

        private static void loadOverrides(String blockString, Map<String, String> stateOverrides) {
            Block block = BlockUtils.blockFromString(blockString);

            if (block == null) {
                LOGGER.info("Ignoring PistonLib behavior overrides for unknown block: " + blockString);
                return;
            }

            for (Map.Entry<String, String> entry : stateOverrides.entrySet()) {
                loadOverride(block, entry.getKey(), entry.getValue());
            }
        }

        private static void loadOverride(Block block, String blockStateString, String behaviorString) {
            BlockState state = BlockUtils.blockStateFromString(block, blockStateString);

            if (state == null) {
                LOGGER.info("ignoring piston move behavior overrides for unknown block state {} of block {}",
                        blockStateString, block);
                return;
            }
            if (!canChangeOverride(state)) {
                LOGGER.info("ignoring piston move behavior override for block state " + blockStateString +
                        " of block " + block + ": not allowed to change overrides");
                return;
            }

            PistonMoveBehavior override = PistonMoveBehavior.fromName(behaviorString);

            if (override == null) {
                LOGGER.info("Unknown PistonLib behavior `" + behaviorString + "` given for block state `" +
                        blockStateString + "` of block `" + block + "`");
                return;
            }

            setOverride(state, override);
        }

        public static void save() {
            getConfigFile().ifPresent(configPath -> {
                if (Files.exists(configPath) && !Files.isWritable(configPath)) {
                    LOGGER.warn("unable to write piston move behavior overrides config!");
                    return;
                }

                Map<String, Map<String, String>> serializedValues = new HashMap<>();

                BuiltInRegistries.BLOCK.forEach(block -> {
                    saveOverrides(block, serializedValues);
                });

                if (serializedValues.isEmpty()) {
                    return; // nothing to save
                }

                try {
                    Files.createDirectories(configPath.getParent());
                    WRITER.write(serializedValues, configPath.toFile());
                } catch (IOException e) {
                    throw new SerializationException(e);
                }
            });
        }

        private static void saveOverrides(Block block, Map<String, Map<String, String>> serializedValues) {
            Map<String, String> overrides = new HashMap<>();

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                saveOverride(state, overrides);
            }

            if (!overrides.isEmpty()) {
                serializedValues.put(BlockUtils.blockAsString(block), overrides);
            }
        }

        private static void saveOverride(BlockState state, Map<String, String> overrides) {
            if (!canChangeOverride(state)) {
                return;
            }

            PistonMoveBehavior override = getOverride(state);

            if (!override.isPresent()) {
                return;
            }

            String blockStateString = BlockUtils.propertiesAsString(state);
            String overrideString = override.getName();

            overrides.put(blockStateString, overrideString);
        }
    }
}
