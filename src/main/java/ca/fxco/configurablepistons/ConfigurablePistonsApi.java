package ca.fxco.configurablepistons;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.Nullable;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

import static ca.fxco.configurablepistons.pistonLogic.StickyGroup.STRICT_SAME;

/**
 * ConfigurablePistons API
 * <p>
 * This is the main class used to access the ConfigurablePistons API. Providing the easy-to-access methods to easily get started!
 * <p>
 * Here you can register your pistons families, and custom pistons
 */
public class ConfigurablePistonsApi {

    /**
     * Creates a family with a specific id
     */
    public static PistonFamily createFamily(String id) {
        return PistonFamilies.createFamily(id);
    }

    /**
     * When creativeGroup is null, no item will be created for the piston. CreativeGroup is only used for base piston blocks
     */
    public static <T extends Block> T registerPiston(PistonFamily family, T block, @Nullable CreativeModeTab creativeGroup) {
        return ModBlocks.registerPiston(family, block, creativeGroup);
    }

    /**
     * Creates a StickyGroup with a specific identifier, this should be used for custom sticky blocks
     */
    public static StickyGroup createStickyGroup(ResourceLocation id) {
        return createStickyGroup(id, STRICT_SAME, null);
    }

    /**
     * Creates a StickyGroup with a specific identifier, this should be used for custom sticky blocks
     */
    public static StickyGroup createStickyGroup(ResourceLocation id, BiPredicate<StickyGroup, StickyGroup> stickRule) {
        return createStickyGroup(id, stickRule, null);
    }

    /**
     * Creates a StickyGroup with a specific identifier, this should be used for custom sticky blocks
     */
    public static StickyGroup createStickyGroup(ResourceLocation id, BiPredicate<StickyGroup, StickyGroup> stickRule, @Nullable StickyGroup inherits) {
        return StickyGroup.create(id, stickRule, inherits);
    }
}
