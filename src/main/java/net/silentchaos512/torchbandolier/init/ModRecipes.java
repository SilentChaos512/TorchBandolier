package net.silentchaos512.torchbandolier.init;

import net.minecraft.item.crafting.RecipeSerializers;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

public final class ModRecipes {
    private ModRecipes() {}

    public static void init() {
        RecipeSerializers.register(ExtractTorchesRecipe.SERIALIZER);
        RecipeSerializers.register(SetTorchRecipe.SERIALIZER);
    }
}
