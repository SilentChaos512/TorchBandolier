package net.silentchaos512.torchbandolier.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.Config;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;
import net.silentchaos512.torchbandolier.setup.ModItems;

import javax.annotation.Nonnull;
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
        registration.addRecipes(RecipeTypes.CRAFTING,
                ModItems.getTorchBandoliers()
                        .filter(item1 -> item1.getDefaultTorchBlock() != null)
                        .map(item1 -> {
                            var defaultTorchBlock = item1.getDefaultTorchBlock();
                            var torchItem = defaultTorchBlock.asItem();
                            var id1 = TorchBandolier.getId("dummy_set_" + NameUtils.fromItem(item1).getPath());
                            CraftingRecipe recipe1 = new ShapelessRecipe(
                                    "",
                                    CraftingBookCategory.MISC,
                                    TorchBandolierItem.createStack(item1, defaultTorchBlock, 1),
                                    NonNullList.of(
                                            Ingredient.EMPTY,
                                            Ingredient.of(ModItems.EMPTY_TORCH_BANDOLIER),
                                            Ingredient.of(torchItem)
                                    )
                            );
                            return new RecipeHolder<>(id1, recipe1);
                        })
                        .collect(Collectors.toList())
        );
        // Extract torches recipes
        registration.addRecipes(RecipeTypes.CRAFTING,
                ModItems.getTorchBandoliers()
                        .filter(item -> item.getDefaultTorchBlock() != null && !item.getDefaultTorchBlock().defaultBlockState().isAir())
                        .map(item -> {
                            var defaultTorchBlock = item.getDefaultTorchBlock();
                            var torchItem = defaultTorchBlock.asItem();
                            var id = TorchBandolier.getId("dummy_extract_" + NameUtils.fromItem(item).getPath());
                            CraftingRecipe recipe = new ShapelessRecipe(
                                    "",
                                    CraftingBookCategory.MISC,
                                    new ItemStack(torchItem, 64),
                                    NonNullList.of(
                                            Ingredient.EMPTY,
                                            Ingredient.of(TorchBandolierItem.createStack(item, defaultTorchBlock, Config.maxTorchCount))
                                    )
                            );
                            return new RecipeHolder<>(id, recipe);
                        })
                        .collect(Collectors.toList())
        );
    }
}
