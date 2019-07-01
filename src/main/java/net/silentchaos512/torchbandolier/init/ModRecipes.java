package net.silentchaos512.torchbandolier.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.crafting.recipe.ExtractTorchesRecipe;
import net.silentchaos512.torchbandolier.crafting.recipe.SetTorchRecipe;

public final class ModRecipes {
    private ModRecipes() {}

    public static void init() {
        register("extract_torches", ExtractTorchesRecipe.SERIALIZER);
        register("set_torch", SetTorchRecipe.SERIALIZER);
    }

    private static void register(String name, IRecipeSerializer<?> serializer) {
        IRecipeSerializer.register(TorchBandolier.MOD_ID + ":" + name, serializer);
    }
}
