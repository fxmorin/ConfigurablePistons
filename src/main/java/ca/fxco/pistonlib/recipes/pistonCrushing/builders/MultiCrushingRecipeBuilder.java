package ca.fxco.pistonlib.recipes.pistonCrushing.builders;

import ca.fxco.pistonlib.recipes.pistonCrushing.MultiCrushingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiCrushingRecipeBuilder implements RecipeBuilder {

    protected final List<Ingredient> ingredients;
    protected final ItemStack result;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group;

    public MultiCrushingRecipeBuilder(List<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public static MultiCrushingRecipeBuilder crushingItems(List<ItemLike> items, Item result) {
        return crushingItems(items, result, 1);
    }

    public static MultiCrushingRecipeBuilder crushingItems(List<ItemLike> items, Item result, int count) {
        ItemStack stack = result.getDefaultInstance();
        stack.setCount(count);
        return crushingItems(items, stack);
    }

    public static MultiCrushingRecipeBuilder crushingItems(List<ItemLike> items, ItemStack result) {
        return crushing(items.stream().map(Ingredient::of).toList(), result);
    }

    public static MultiCrushingRecipeBuilder crushing(List<Ingredient> ingredients, ItemStack result) {
        return new MultiCrushingRecipeBuilder(ingredients, result);
    }

    @Override
    public MultiCrushingRecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    public MultiCrushingRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> key) {
        MultiCrushingRecipe recipe = new MultiCrushingRecipe(this.ingredients, this.result);
        output.accept(key, recipe, null);
    }
}
