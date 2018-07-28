/*
 * TorchBandolier -- ItemTorchBandolier
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.torchbandolier.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.silentchaos512.lib.item.IItemSL;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.lib.util.ItemHelper;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.torchbandolier.TorchBandolier;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTorchBandolier extends Item implements IItemSL {
    private static final String NBT_ROOT = "TorchBandolier";
    private static final String NBT_AUTO_FILL = "AutoFill";
    private static final String NBT_COUNT = "Count";

    private static final int ABSORB_DELAY = 20;

    public ItemTorchBandolier() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setNoRepair();
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && worldIn.getTotalWorldTime() % ABSORB_DELAY == 0 && entityIn instanceof EntityPlayer && isAutoFillOn(stack)) {
            absorbTorches(stack, (EntityPlayer) entityIn);
        }
    }

    private void absorbTorches(ItemStack stack, EntityPlayer player) {
        int maxTorches = getMaxTorchCount(stack);
        if (getTorchCount(stack) >= maxTorches) return;
        Item itemTorch = Item.getItemFromBlock(getBlockPlaced(stack));

        for (ItemStack invStack : PlayerHelper.getNonEmptyStacks(player, true, true, false)) {
            if (invStack.getItem() == itemTorch) {
                int current = getTorchCount(stack);

                if (current + invStack.getCount() > maxTorches) {
                    setTorchCount(stack, maxTorches);
                    invStack.shrink(maxTorches - current);
                } else {
                    setTorchCount(stack, current + invStack.getCount());
                    invStack.setCount(0);
                }

                if (invStack.getCount() <= 0)
                    PlayerHelper.removeItem(player, invStack);
            }
        }
    }

    public int absorbTorchesFromEntityItem(ItemStack bandolier, ItemStack blockStack) {
        int bandolierCount = getTorchCount(bandolier);
        int maxTorches = getMaxTorchCount(bandolier);
        int blockCount = blockStack.getCount();

        if (bandolierCount + blockCount > maxTorches) {
            setTorchCount(bandolier, maxTorches);
            return maxTorches - bandolierCount;
        } else {
            setTorchCount(bandolier, bandolierCount + blockCount);
            return blockCount;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!playerIn.world.isRemote && playerIn.isSneaking()) {
            // Toggle auto-fill
            boolean mode = !isAutoFillOn(stack);
            setAutoFill(stack, mode);
            String translationKey = "item.torchbandolier.torch_bandolier.autoFill." + (mode ? "on" : "off");
            playerIn.sendStatusMessage(new TextComponentTranslation(translationKey), true);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (getTorchCount(stack) <= 0 && !player.capabilities.isCreativeMode) {
            // Empty and not in creative mode
            return EnumActionResult.PASS;
        }

        // Create fake block stack and use it
        Block block = getBlockPlaced(stack);
        ItemStack fakeBlockStack = new ItemStack(block);
        // Swap the fake stack into the player's off hand to use it
        EnumActionResult result = ItemHelper.useItemAsPlayer(fakeBlockStack, player, worldIn, pos, facing, hitX, hitY, hitZ);

        if (result == EnumActionResult.SUCCESS && !player.capabilities.isCreativeMode)
            setTorchCount(stack, getTorchCount(stack) - 1);
        return result;
    }

    @Override
    public ActionResult<ItemStack> onItemLeftClickSL(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote && player.isSneaking() && getTorchCount(stack) > 0) {
            Block block = getBlockPlaced(stack);

            // Create block stack to drop
            ItemStack toDrop = new ItemStack(block);
            toDrop.setCount(Math.min(getTorchCount(stack), toDrop.getMaxStackSize()));
            setTorchCount(stack, getTorchCount(stack) - toDrop.getCount());

            // Make EntityItem and spawn in world
            Vec3d vec = player.getLookVec().scale(2.0);
            EntityItem entity = new EntityItem(world, player.posX + vec.x, player.posY + 1 + vec.y, player.posZ + vec.z, toDrop);
            vec = vec.scale(-0.125);
            entity.motionX = vec.x;
            entity.motionY = vec.y;
            entity.motionZ = vec.z;
            EntityHelper.safeSpawn(entity);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;
        ItemStack empty = new ItemStack(this);
        ItemStack full = new ItemStack(this);
        setTorchCount(full, getMaxTorchCount(full));
        items.add(empty);
        items.add(full);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        boolean autoFill = isAutoFillOn(stack);
        int torches = getTorchCount(stack);
        int maxTorches = getMaxTorchCount(stack);

        tooltip.add(TorchBandolier.i18n.subText(this, "count", torches, maxTorches));
        tooltip.add(TorchBandolier.i18n.subText(this, "autoFill." + (autoFill ? "on" : "off")));
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int max = getMaxTorchCount(stack);
        if (max <= 0) return 1.0;
        return 1.0 - (double) getTorchCount(stack) / (double) max;
    }

    public Block getBlockPlaced(ItemStack stack) {
        return Blocks.TORCH;
    }

    public int getTorchCount(ItemStack stack) {
        return stack.getOrCreateSubCompound(NBT_ROOT).getInteger(NBT_COUNT);
    }

    public void setTorchCount(ItemStack stack, int value) {
        stack.getOrCreateSubCompound(NBT_ROOT).setInteger(NBT_COUNT, value);
    }

    public int getMaxTorchCount(ItemStack stack) {
        // TODO
        return 1024;
    }

    public boolean isAutoFillOn(ItemStack stack) {
        return stack.getOrCreateSubCompound(NBT_ROOT).getBoolean(NBT_AUTO_FILL);
    }

    public void setAutoFill(ItemStack stack, boolean value) {
        stack.getOrCreateSubCompound(NBT_ROOT).setBoolean(NBT_AUTO_FILL, value);
    }
}
