package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import java.util.function.Function;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModBlocks {

    public static final MergeBlock MERGE_BLOCK = register("merge_block", MergeBlock::new, Blocks.MOVING_PISTON);

    private static <T extends Block> T register(String name, Function<Properties, T> block, Block propertySource) {
        return register(name, block, Properties.ofFullCopy(propertySource));
    }

    private static <T extends Block> T registerPiston(String name, Function<Properties, T> block) {
        return register(name, block, Properties.ofFullCopy(Blocks.PISTON));
    }

    private static <T extends Block> T registerPistonHead(String name, Function<Properties, T> block) {
        return register(name, block, Properties.ofFullCopy(Blocks.PISTON_HEAD));
    }

    private static <T extends Block> T registerMovingBlock(String name, Function<Properties, T> block) {
        return register(name, block, BasicMovingBlock.createDefaultSettings());
    }

    private static <T extends Block> T register(String name, Function<Properties, T> function, Properties properties) {
        ResourceKey<Block> resourceKey = ResourceKey.create(Registries.BLOCK, id(name));
        T block = function.apply(properties.setId(resourceKey));
        return Registry.register(BuiltInRegistries.BLOCK, resourceKey, block);
    }

    public static void bootstrap() { }
}
