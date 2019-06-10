package net.silentchaos512.torchbandolier.compat.jei;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.torchbandolier.TorchBandolier;

//@JeiPlugin
public class TorchBandolierJeiPlugin /*implements IModPlugin*/ {
    private static final ResourceLocation PLUGIN_UID = TorchBandolier.getId("plugin");

    /*
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Set torch recipes
        registration.addRecipes(
                ModItems.getTorchBandolierPairs().map(entry -> {
                    Item torch = entry.getKey();
                    TorchBandolierItem item = entry.getValue();
                    return new ShapelessRecipe(
                            TorchBandolier.getId("dummy_set_" + Objects.requireNonNull(item.getRegistryName()).getPath()),
                            "",
                            TorchBandolierItem.createStack(item, 1),
                            NonNullList.from(
                                    Ingredient.EMPTY,
                                    Ingredient.fromItems(ModItems.emptyTorchBandolier),
                                    Ingredient.fromItems(torch)
                            )
                    );
                }).collect(Collectors.toList()),
                VanillaRecipeCategoryUid.CRAFTING
        );
        // Extract torches recipes
        registration.addRecipes(
                ModItems.getTorchBandolierPairs().map(entry -> {
                    Item torch = entry.getKey();
                    TorchBandolierItem item = entry.getValue();
                    return new ShapelessRecipe(
                            TorchBandolier.getId("dummy_extract_" + Objects.requireNonNull(item.getRegistryName()).getPath()),
                            "",
                            new ItemStack(torch, 64),
                            NonNullList.from(
                                    Ingredient.EMPTY,
                                    Ingredient.fromStacks(TorchBandolierItem.createStack(item, Config.GENERAL.maxTorchCount.get()))
                            )
                    );
                }).collect(Collectors.toList()),
                VanillaRecipeCategoryUid.CRAFTING
        );
    }
    */
}
