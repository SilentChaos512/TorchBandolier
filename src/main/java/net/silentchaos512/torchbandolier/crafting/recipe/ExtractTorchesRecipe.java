package net.silentchaos512.torchbandolier.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;
import net.silentchaos512.torchbandolier.setup.ModDataComponents;
import net.silentchaos512.torchbandolier.setup.ModItems;
import net.silentchaos512.torchbandolier.setup.ModRecipes;
import org.jetbrains.annotations.NotNull;

public final class ExtractTorchesRecipe extends CustomRecipe {
    public ExtractTorchesRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.EXTRACT_TORCHES.get();
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        ItemStack torchBandolier = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            var stackInSlot = inv.getItem(i);
            if (stackInSlot.isEmpty()) {
                continue;
            }
            if (stackInSlot.get(ModDataComponents.TORCH) != null && torchBandolier.isEmpty()) {
                torchBandolier = stackInSlot;
            } else {
                return false;
            }
        }

        return !torchBandolier.isEmpty();
    }

    private static @NotNull ItemStack getResult(CraftingInput inv) {
        ItemStack stack = StackList.from(inv).uniqueOfType(TorchBandolierItem.class);
        TorchBandolierItem item = (TorchBandolierItem) stack.getItem();
        Block block = item.getTorchBlock(stack);
        if (block == null) {
            return ItemStack.EMPTY;
        }
        int torchCount = TorchBandolierItem.getTorchCount(stack);
        int extractCount = Math.min(torchCount, 64);
        return new ItemStack(block, extractCount);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        return getResult(inv);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for(int i = 0; i < list.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (item.getItem() instanceof TorchBandolierItem) {
                // Extract torches, but leave the modified bandolier in the crafting grid
                ItemStack torches = getResult(inv);
                int newTorchCount = TorchBandolierItem.getTorchCount(item) - torches.getCount();
                ItemStack newBandolier;
                if (newTorchCount > 0) {
                    newBandolier = TorchBandolierItem.createCopyWithNewCount(item, newTorchCount, false);
                } else {
                    newBandolier = ModItems.EMPTY_TORCH_BANDOLIER.toStack();
                }
                list.set(i, newBandolier);
            }
            else if (item.hasCraftingRemainingItem()) {
                list.set(i, item.getCraftingRemainingItem());
            }
        }

        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }
}
