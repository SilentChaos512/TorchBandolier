package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.torchbandolier.init.ModRecipes;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

public final class ExtractTorchesRecipe extends SpecialRecipe {
    public ExtractTorchesRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.EXTRACT_TORCHES.get();
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
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack stack = StackList.from(inv).uniqueOfType(TorchBandolierItem.class);
        TorchBandolierItem item = (TorchBandolierItem) stack.getItem();
        Block block = item.getTorchBlock();
        int torchCount = TorchBandolierItem.getTorchCount(stack);
        int extractCount = Math.min(torchCount, 64);
        return new ItemStack(block, extractCount);
    }



    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (item.getItem() instanceof TorchBandolierItem) {
                // Extract torches, but leave the modified bandolier in the crafting grid
                ItemStack torches = assemble(inv);
                int newTorchCount = TorchBandolierItem.getTorchCount(item) - torches.getCount();
                ItemStack newBandolier;
                if (newTorchCount > 0) {
                    newBandolier = TorchBandolierItem.createStack((TorchBandolierItem) item.getItem(), newTorchCount);
                    TorchBandolierItem.setTorchCount(newBandolier, newTorchCount);
                    TorchBandolierItem.setAutoFill(newBandolier, false);
                } else {
                    newBandolier = new ItemStack(ModItems.EMPTY_TORCH_BANDOLIER);
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
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }
}
