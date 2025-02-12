package ca.fxco.pistonlib;

import ca.fxco.pistonlib.api.config.Category;
import ca.fxco.pistonlib.api.config.ConfigValue;
import ca.fxco.pistonlib.api.config.TestValues;

public class PistonLibConfig {

    // ==============
    //    Features
    // ==============

    @ConfigValue(
            desc = "Double blocks such as Chests, Doors, and Beds will now move as one",
            keyword = {"sticky", "group", "double"},
            category = Category.FEATURE
    )
    public static boolean stuckDoubleBlocks = true;


    // ===============
    //    Mechanics
    // ===============

    @ConfigValue(
            desc = "Toggle the strong sticky type block dropping mechanic where all strong sticky types block drop together",
            conflict = "combinedBlockDropping",
            keyword = {"block", "dropping"},
            category = Category.MECHANIC
    )
    public static boolean strongBlockDropping = true;

    @ConfigValue(
            desc = "All blocks now block drop together, `strongBlockDropping` does nothing if this is true",
            requires = "pistonStructureGrouping",
            keyword = {"block", "dropping"},
            category = Category.MECHANIC
    )
    public static boolean combinedBlockDropping = true;

    @ConfigValue(
            desc = "Toggle the merging API, this will prevent all merging-based features from working!",
            keyword = {"merging", "api"},
            category = {Category.MECHANIC, Category.MERGING}
    )
    public static boolean mergingApi = true;

    @ConfigValue(
            desc = "Toggle the ticking API, this will prevent all ticking-based features from working!",
            keyword = {"ticking", "api"},
            category = Category.MECHANIC
    )
    public static boolean tickingApi = true;

    @ConfigValue(
            desc = "Toggle the indirect sticky API, this allows blocks to sticky to non-sticky blocks by being strongly sticky",
            keyword = {"extended", "indirect", "sticky", "api"},
            category = Category.MECHANIC
    )
    public static boolean indirectStickyApi = true;

    @ConfigValue(
            desc = "Toggle the behavior override API, this API allows you to change the move-ability of any block",
            keyword = {"behavior", "api"},
            category = Category.MECHANIC
    )
    public static boolean behaviorOverrideApi = true;

    @ConfigValue(
            desc = "Toggle the entity API, this API enabled entity-related piston mechanics such as Piston Crushing",
            keyword = {"entity", "api"},
            category = Category.MECHANIC
    )
    public static boolean entityApi = true;

    @ConfigValue(
            desc = "Group the piston structure into a single ticking block entity, allowing tons of optimizations",
            more = {"Disabling this mechanic also prevents multiple features from working. Such as being able to change the direction mid-way through pushing without block dropping", // I know, game changer xD
                    "This also allows blocks to cull against other blocks in piston structures!"},
            keyword = {"group", "structure"},
            category = {Category.MECHANIC, Category.OPTIMIZATION}
    )
    public static boolean pistonStructureGrouping = true;


    // ===================
    //    Major Changes
    // ===================

    @ConfigValue(
            desc = "Causes all sticky types to act like indirect sticky blocks",
            requires = "indirectStickyApi",
            keyword = {"sticky", "indirect"},
            category = {Category.EXTREME, Category.MECHANIC, Category.EXPERIMENTAL}
    )
    public static boolean allStickyTypesAreIndirect = false;


    // ===========
    //    Fixes
    // ===========

    @ConfigValue(
            desc = "Fixes pistons pushing entities 0.01 too far",
            more = "The value may not be 0.01 for pistons with different speeds",
            keyword = {"entity", "collision"},
            category = Category.FIX
    )
    public static boolean pistonsPushTooFarFix = true;

    @ConfigValue(
            desc = "Fixes piston progress not being saved correctly, cause some pistons to get out of sync",
            keyword = {"progress", "sync"},
            category = Category.FIX
    )
    public static boolean pistonProgressFix = true;

    @ConfigValue(
            desc = "Fixes tnt duping using pistons",
            more = {"This does also fix some other edge cases with modded blocks that behave the same when powered"},
            requires = "pistonPushingCacheFix",
            keyword = {"tnt", "duping"},
            category = Category.FIX
    )
    public static boolean tntDupingFix = false;

    @ConfigValue(
            desc = "Fixes tnt duping using pistons",
            more = "This does also fix some other edge cases with modded blocks that behave the same when powered",
            keyword = {"waterlog"},
            category = Category.FIX,
            testValues = @TestValues(stringValues = {"NONE", "VANILLA", "ALL"})
    )
    public static WaterloggedState pistonsPushWaterloggedBlocks = WaterloggedState.VANILLA;

    @ConfigValue(
            desc = "Fixes being able to make and use Headless Pistons",
            keyword = {"headless"},
            fixes = 27056,
            category = Category.FIX
    )
    public static boolean headlessPistonFix = true;

    @ConfigValue(
            desc = "Fixes Breaking blocks that should not be able to be broken using headless pistons",
            more = "Illegal blocks are any blocks that have a hardness value of -1.0F",
            keyword = {"headless", "illegal"},
            fixes = 188220,
            category = Category.FIX
    )
    public static boolean illegalBreakingFix = true;

    @ConfigValue(
            desc = "Fixes pistons pulling/pushing blocks using a hashmap causing order to be locational",
            keyword = {"locational", "update", "order"},
            fixes = 233420,
            category = Category.FIX
    )
    public static boolean locationalUpdateOrderFix = true;

    @ConfigValue(
            desc = "Fixes pistons being able to push blocks outside of the world border",
            keyword = {"world border"},
            fixes = 82010,
            category = Category.FIX
    )
    public static boolean pushThroughWorldBorderFix = true;

    @ConfigValue(
            desc = "Fixes mobs being able to spawn on moving pistons",
            more = "Only works on PistonLib pistons, not vanilla pistons",
            keyword = {"mob","spawning"},
            fixes = 163978,
            category = Category.FIX
    )
    public static boolean mobsSpawnOnMovingPistonsFix = true;

    @ConfigValue(
            desc = "Fixes the way piston pushing cache works",
            more = {"Prevents multiple duping methods based on update order and internal cache",
                    "Disable this rule in order to have the exact same vanilla duping behaviour",
                    "Only allows Coral based tnt duping to work!"},
            keyword = {"cache", "duping", "tnt"},
            category = Category.FIX
    )
    public static boolean pistonPushingCacheFix = true;

    // ============
    //    Tweaks
    // ============

    // TODO: Add some sort of validation, since this number should never be a negative
    @ConfigValue(
            desc = "Change the distance in blocks that piston block events are sent.",
            more = {"In vanilla, its set to 64 block by default.",
                    "Fixes pistons looking jittery when they are far away",
                    "Can also be used to make the distance shorter, to reduce the piston rendering lag"},
            keyword = {"block event", "distance"},
            suggestions = {"16", "32", "64", "128", "256"},
            category = {Category.TWEAK, Category.OPTIMIZATION, Category.FIX}
    )
    public static double pistonBlockEventDistance = 64D;

    // TODO: Add some sort of validation, since this number should never be a negative
    @ConfigValue(
            desc = "Change the default piston push limit",
            more = {"This will change the push limit of all pistons using the default push limit of 12"},
            keyword = {"block event", "distance"},
            suggestions = {"6", "12", "24", "128", "256"},
            category = Category.TWEAK
    )
    public static int defaultPistonPushLimit = 12;

    public enum WaterloggedState {
        NONE,
        VANILLA,
        ALL
    }
}
