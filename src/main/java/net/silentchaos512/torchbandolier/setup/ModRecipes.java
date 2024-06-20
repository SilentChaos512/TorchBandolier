package net.silentchaos512.torchbandolier.setup;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractTorchesRecipe>> EXTRACT_TORCHES = register(
            "extract_torches",
            () -> new SimpleCraftingRecipeSerializer<>(ExtractTorchesRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SetTorchRecipe>> SET_TORCH = register(
            "set_torch",
            () -> new SimpleCraftingRecipeSerializer<>(SetTorchRecipe::new));

    private ModRecipes() {}

    static void register() {}

    private static <R extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> register(String name, Supplier<RecipeSerializer<R>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(name, serializer);
    }
}
