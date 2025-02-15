package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.recipes.pistonCrushing.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {

    public static final RecipeSerializer<SingleCrushingRecipe> SINGLE_PISTON_CRUSHING = register("piston_crushing_single", new SingleCrushingRecipe.Serializer());
    public static final RecipeSerializer<SingleCrushingAgainstRecipe> SINGLE_AGAINST_PISTON_CRUSHING = register("piston_crushing_single_against", new SingleCrushingAgainstRecipe.Serializer());
    public static final RecipeSerializer<SingleCrushingConditionalRecipe> SINGLE_CONDITIONAL_PISTON_CRUSHING = register("piston_crushing_single_conditional", new SingleCrushingConditionalRecipe.Serializer());
    public static final RecipeSerializer<PairCrushingRecipe> PAIR_PISTON_CRUSHING = register("piston_crushing_pair", new PairCrushingRecipe.Serializer());
    public static final RecipeSerializer<MultiCrushingRecipe> MULTI_PISTON_CRUSHING = register("piston_crushing_multi", new MultiCrushingRecipe.Serializer());

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String string, S recipeSerializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PistonLib.id(string), recipeSerializer);
    }

    public static void boostrap() { }
}
