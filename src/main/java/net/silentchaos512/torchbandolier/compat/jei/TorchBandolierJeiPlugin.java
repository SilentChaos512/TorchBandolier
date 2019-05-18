package net.silentchaos512.torchbandolier.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.config.Config;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.item.ItemTorchBandolier;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class TorchBandolierJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = TorchBandolier.getId("plugin");

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
                    ItemTorchBandolier item = entry.getValue();
                    return new ShapelessRecipe(
                            TorchBandolier.getId("dummy_set_" + Objects.requireNonNull(item.getRegistryName()).getPath()),
                            "",
                            ItemTorchBandolier.createStack(item, 1),
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
                    ItemTorchBandolier item = entry.getValue();
                    return new ShapelessRecipe(
                            TorchBandolier.getId("dummy_extract_" + Objects.requireNonNull(item.getRegistryName()).getPath()),
                            "",
                            new ItemStack(torch, 64),
                            NonNullList.from(
                                    Ingredient.EMPTY,
                                    Ingredient.fromStacks(ItemTorchBandolier.createStack(item, Config.GENERAL.maxTorchCount.get()))
                            )
                    );
                }).collect(Collectors.toList()),
                VanillaRecipeCategoryUid.CRAFTING
        );
    }
}