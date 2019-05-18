package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.item.ItemTorchBandolier;

public final class ExtractTorchesRecipe implements IRecipe {
    public static final IRecipeSerializer<ExtractTorchesRecipe> SERIALIZER = new RecipeSerializers.SimpleSerializer<>(
            "torchbandolier:extract_torches",
            ExtractTorchesRecipe::new
    );

    private final ResourceLocation recipeId;

    private ExtractTorchesRecipe(ResourceLocation id) {
        this.recipeId = id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        StackList list = StackList.from(inv);
        if (list.size() != 1) {
            return false;
        }
        ItemStack stack = list.uniqueMatch(s ->
                s.getItem() instanceof ItemTorchBandolier && ((ItemTorchBandolier) s.getItem()).getTorchBlock() != null);
        return !stack.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack stack = StackList.from(inv).uniqueOfType(ItemTorchBandolier.class);
        ItemTorchBandolier item = (ItemTorchBandolier) stack.getItem();
        Block block = item.getTorchBlock();
        int torchCount = ItemTorchBandolier.getTorchCount(stack);
        int extractCount = Math.min(torchCount, 64);
        return new ItemStack(block, extractCount);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(IInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.getItem() instanceof ItemTorchBandolier) {
                // Extract torches, but leave the modified bandolier in the crafting grid
                ItemStack torches = getCraftingResult(inv);
                int newTorchCount = ItemTorchBandolier.getTorchCount(item) - torches.getCount();
                ItemStack newBandolier;
                if (newTorchCount > 0) {
                    newBandolier = ItemTorchBandolier.createStack((ItemTorchBandolier) item.getItem(), newTorchCount);
                    ItemTorchBandolier.setTorchCount(newBandolier, newTorchCount);
                    ItemTorchBandolier.setAutoFill(newBandolier, false);
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
