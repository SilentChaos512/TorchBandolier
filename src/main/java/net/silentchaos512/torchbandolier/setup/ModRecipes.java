package net.silentchaos512.torchbandolier.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER, TorchBandolier.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractTorchesRecipe>> EXTRACT_TORCHES = register(
            "extract_torches",
            () -> new SimpleCraftingRecipeSerializer<>(ExtractTorchesRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SetTorchRecipe>> SET_TORCH = register(
            "set_torch",
            SetTorchRecipe.Serializer::new
    );

    private ModRecipes() {}

    static void register() {}

    private static <R extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> register(String name, Supplier<RecipeSerializer<R>> serializer) {
        return RECIPE_SERIALIZERS.register(name, serializer);
    }
}
