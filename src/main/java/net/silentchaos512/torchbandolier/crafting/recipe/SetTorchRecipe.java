package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.item.ItemTorchBandolier;

public final class SetTorchRecipe implements IRecipe {
    public static final IRecipeSerializer<SetTorchRecipe> SERIALIZER = new RecipeSerializers.SimpleSerializer<>(
            "torchbandolier:set_torch",
            SetTorchRecipe::new
    );

    private final ResourceLocation recipeId;

    private SetTorchRecipe(ResourceLocation id) {
        this.recipeId = id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        StackList list = StackList.from(inv);
        if (list.size() != 2) {
            return false;
        }
        int numEmptyBandolier = list.countOfMatches(s -> s.getItem() == ModItems.emptyTorchBandolier);
        int numTorch = list.countOfMatches(s -> ModItems.getTorchBandolier(s.getItem()) != null);
        return numEmptyBandolier == 1 && numTorch == 1;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack torch = StackList.from(inv).uniqueMatch(s -> !(s.getItem() instanceof ItemTorchBandolier));
        ItemTorchBandolier item = ModItems.getTorchBandolier(torch.getItem());
        if (torch.isEmpty() || item == null) {
            return ItemStack.EMPTY;
        }
        return ItemTorchBandolier.createStack(item, 1);
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Cannot determine
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 1;
    }
}
