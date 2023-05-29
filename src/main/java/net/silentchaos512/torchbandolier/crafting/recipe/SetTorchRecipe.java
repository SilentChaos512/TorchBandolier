package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.init.ModRecipes;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

public final class SetTorchRecipe extends CustomRecipe {
    public SetTorchRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SET_TORCH.get();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        StackList list = StackList.from(inv);
        if (list.size() != 2) {
            return false;
        }
        int numEmptyBandolier = list.countOfMatches(s -> s.getItem() == ModItems.EMPTY_TORCH_BANDOLIER.get());
        int numTorch = list.countOfMatches(s -> ModItems.getTorchBandolier(s.getItem()) != null);
        return numEmptyBandolier == 1 && numTorch == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack torch = StackList.from(inv).uniqueMatch(s -> !(s.getItem() instanceof TorchBandolierItem));
        TorchBandolierItem item = ModItems.getTorchBandolier(torch.getItem());
        if (torch.isEmpty() || item == null) {
            return ItemStack.EMPTY;
        }
        return TorchBandolierItem.createStack(item, 1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }
}
