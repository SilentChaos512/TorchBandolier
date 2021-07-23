package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.init.ModRecipes;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

public final class SetTorchRecipe extends SpecialRecipe {
    public SetTorchRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SET_TORCH.get();
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        StackList list = StackList.from(inv);
        if (list.size() != 2) {
            return false;
        }
        int numEmptyBandolier = list.countOfMatches(s -> s.getItem() == ModItems.EMPTY_TORCH_BANDOLIER.get());
        int numTorch = list.countOfMatches(s -> ModItems.getTorchBandolier(s.getItem()) != null);
        return numEmptyBandolier == 1 && numTorch == 1;
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
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
