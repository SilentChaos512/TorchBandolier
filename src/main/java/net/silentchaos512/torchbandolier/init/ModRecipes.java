package net.silentchaos512.torchbandolier.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final RegistryObject<RecipeSerializer<?>> EXTRACT_TORCHES = register("extract_torches", () ->
            new SimpleCraftingRecipeSerializer<>(ExtractTorchesRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> SET_TORCH = register("set_torch", () ->
            new SimpleCraftingRecipeSerializer<>(SetTorchRecipe::new));

    private ModRecipes() {}

    static void register() {}

    private static RegistryObject<RecipeSerializer<?>> register(String name, Supplier<RecipeSerializer<?>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(name, serializer);
    }
}
