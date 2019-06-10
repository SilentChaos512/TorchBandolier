package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

public final class ExtractTorchesRecipe implements ICraftingRecipe {
    public static final IRecipeSerializer<ExtractTorchesRecipe> SERIALIZER = new SpecialRecipeSerializer<>(ExtractTorchesRecipe::new);

    private final ResourceLocation recipeId;

    private ExtractTorchesRecipe(ResourceLocation id) {
        this.recipeId = id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        StackList list = StackList.from(inv);
        if (list.size() != 1) {
            return false;
        }
        ItemStack stack = list.uniqueMatch(s ->
                s.getItem() instanceof TorchBandolierItem && ((TorchBandolierItem) s.getItem()).getTorchBlock() != null);
        return !stack.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = StackList.from(inv).uniqueOfType(TorchBandolierItem.class);
        TorchBandolierItem item = (TorchBandolierItem) stack.getItem();
        Block block = item.getTorchBlock();
        int torchCount = TorchBandolierItem.getTorchCount(stack);
        int extractCount = Math.min(torchCount, 64);
        return new ItemStack(block, extractCount);
    }



    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.getItem() instanceof TorchBandolierItem) {
                // Extract torches, but leave the modified bandolier in the crafting grid
                ItemStack torches = getCraftingResult(inv);
                int newTorchCount = TorchBandolierItem.getTorchCount(item) - torches.getCount();
                ItemStack newBandolier;
                if (newTorchCount > 0) {
                    newBandolier = TorchBandolierItem.createStack((TorchBandolierItem) item.getItem(), newTorchCount);
                    TorchBandolierItem.setTorchCount(newBandolier, newTorchCount);
                    TorchBandolierItem.setAutoFill(newBandolier, false);
                } else {
                    newBandolier = new ItemStack(ModItems.emptyTorchBandolier);
                }
                list.set(i, newBandolier);
            }
            else if (item.hasContainerItem()) {
                list.set(i, item.getContainerItem());
            }
        }

        return list;
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
