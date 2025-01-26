package ca.fxco.api.pistonlib.recipes.pistonCrushing.builders;

import ca.fxco.api.pistonlib.recipes.pistonCrushing.PairCrushingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class PairCrushingRecipeBuilder implements RecipeBuilder {

    protected final Ingredient first;
    protected final Ingredient second;
    protected final ItemStack result;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    protected String group;

    public PairCrushingRecipeBuilder(Ingredient first, Ingredient second, ItemStack result) {
        this.first = first;
        this.second = second;
        this.result = result;
    }

    public static PairCrushingRecipeBuilder crushing(Ingredient first, Ingredient second, ItemStack result) {
        return new PairCrushingRecipeBuilder(first, second, result);
    }

    @Override
    public PairCrushingRecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    public PairCrushingRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> key) {
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        PairCrushingRecipe recipe = new PairCrushingRecipe(this.first, this.second, this.result);
        output.accept(key, recipe, advancement.build(key.location().withPrefix("recipes/")));
    }
}
