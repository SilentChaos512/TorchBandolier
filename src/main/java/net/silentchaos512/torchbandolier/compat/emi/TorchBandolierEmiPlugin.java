package net.silentchaos512.torchbandolier.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;
import net.silentchaos512.torchbandolier.setup.ModDataComponents;
import net.silentchaos512.torchbandolier.setup.ModItems;
import net.silentchaos512.torchbandolier.setup.ModTags;

import java.util.List;

@EmiEntrypoint
public class TorchBandolierEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addRecipe(createExampleSetTorchRecipe(ModItems.TORCH_BANDOLIER.get()));
        emiRegistry.addRecipe(createExampleSetTorchRecipe(ModItems.SOUL_TORCH_BANDOLIER.get()));
        emiRegistry.addRecipe(createExampleSetTorchRecipe(ModItems.STONE_TORCH_BANDOLIER.get()));
    }

    private EmiCraftingRecipe createExampleSetTorchRecipe(TorchBandolierItem torchBandolier) {
        var bandolierWithOneTorch = new ItemStack(torchBandolier);
        bandolierWithOneTorch.set(ModDataComponents.TORCH, torchBandolier.getDefaultTorchBlock());
        bandolierWithOneTorch.set(ModDataComponents.TORCH_COUNT, 1);

        var itemName = BuiltInRegistries.ITEM.getKey(torchBandolier).getPath();

        return new EmiCraftingRecipe(
                List.of(
                        EmiIngredient.of(Ingredient.of(ModItems.EMPTY_TORCH_BANDOLIER.get())),
                        EmiIngredient.of(torchBandolier.getAcceptedTorches())
                ),
                EmiStack.of(bandolierWithOneTorch),
                TorchBandolier.getId(itemName + "_emi_example"),
                true
        );
    }
}
