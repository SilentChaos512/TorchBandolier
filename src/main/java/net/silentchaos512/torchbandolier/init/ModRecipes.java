package net.silentchaos512.torchbandolier.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final RegistryObject<RecipeSerializer<?>> EXTRACT_TORCHES = register("extract_torches", () ->
            new SimpleRecipeSerializer<>(ExtractTorchesRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> SET_TORCH = register("set_torch", () ->
            new SimpleRecipeSerializer<>(SetTorchRecipe::new));

    private ModRecipes() {}

    static void register() {}

    private static RegistryObject<RecipeSerializer<?>> register(String name, Supplier<RecipeSerializer<?>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(name, serializer);
    }
}
