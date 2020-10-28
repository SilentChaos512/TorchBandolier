package net.silentchaos512.torchbandolier.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final RegistryObject<IRecipeSerializer<?>> EXTRACT_TORCHES = register("extract_torches", () ->
            new SpecialRecipeSerializer<>(ExtractTorchesRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> SET_TORCH = register("set_torch", () ->
            new SpecialRecipeSerializer<>(SetTorchRecipe::new));

    private ModRecipes() {}

    static void register() {}

    private static RegistryObject<IRecipeSerializer<?>> register(String name, Supplier<IRecipeSerializer<?>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(name, serializer);
    }
}
