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

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.config.Config;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemTorchBandolier extends Item implements ILeftClickItem {
    private static final String NBT_ROOT = "TorchBandolier";
    private static final String NBT_AUTO_FILL = "AutoFill";
    private static final String NBT_COUNT = "Count";

    private static final int ABSORB_DELAY = 20;

    public ItemTorchBandolier() {
        super(new Properties()
                .maxStackSize(1)
                .setNoRepair()
                .group(TorchBandolier.ITEM_GROUP)
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && worldIn.getGameTime() % ABSORB_DELAY == 0 && entityIn instanceof EntityPlayer && isAutoFillOn(stack)) {
            absorbTorches(stack, (EntityPlayer) entityIn);
        }
    }

    private static void absorbTorches(ItemStack stack, EntityPlayer player) {
        int maxTorches = getMaxTorchCount(stack);
        if (getTorchCount(stack) >= maxTorches) return;
        // FIXME
        Item itemTorch = getBlockPlaced(stack).asItem();

        for (ItemStack invStack : PlayerUtils.getNonEmptyStacks(player, true, true, false)) {
            if (invStack.getItem() == itemTorch) {
                int current = getTorchCount(stack);

                if (current + invStack.getCount() > maxTorches) {
                    setTorchCount(stack, maxTorches);
                    invStack.shrink(maxTorches - current);
                } else {
                    setTorchCount(stack, current + invStack.getCount());
                    invStack.setCount(0);
                }

                if (invStack.getCount() <= 0) {
                    PlayerUtils.removeItem(player, invStack);
                }
            }
        }
    }

    public static int absorbTorchesFromEntityItem(ItemStack bandolier, ItemStack blockStack) {
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
    public EnumActionResult onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        EntityPlayer player = context.getPlayer();
        if (getTorchCount(stack) <= 0 && (player == null || !player.abilities.isCreativeMode)) {
            // Empty and not in creative mode
            return EnumActionResult.PASS;
        }

        // Create fake block stack and use it
        Block block = getBlockPlaced(stack);
        if (block == null) {
            return EnumActionResult.PASS;
        }

        // Create of fake block stack and use it
        ItemStack fakeBlockStack = new ItemStack(block);
        EnumActionResult result = block.asItem().onItemUse(new ItemUseContext(player, fakeBlockStack, context.getPos(), context.getFace(), context.getHitX(), context.getHitY(), context.getHitZ()));

        if (result == EnumActionResult.SUCCESS && !player.abilities.isCreativeMode) {
            setTorchCount(stack, getTorchCount(stack) - 1);
        }
        return result;
    }

    @Override
    public ActionResult<ItemStack> onItemLeftClickSL(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.world.isRemote && player.isSneaking() && getTorchCount(stack) > 0) {
            Block block = getBlockPlaced(stack);
            if (block == null) {
                return ActionResult.newResult(EnumActionResult.PASS, stack);
            }

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
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
        if (!isInGroup(tab)) return;
        ItemStack empty = new ItemStack(this);
        ItemStack full = new ItemStack(this);
        setTorchCount(full, getMaxTorchCount(full));
        items.add(empty);
        items.add(full);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        boolean autoFill = isAutoFillOn(stack);
        int torches = getTorchCount(stack);
        int maxTorches = getMaxTorchCount(stack);
        Block block = getBlockPlaced(stack);
        String key = "item.torchbandolier.torch_bandolier";
        ITextComponent blockName = block != null
                ? block.getNameTextComponent()
                : new TextComponentTranslation(key + ".empty");

        tooltip.add(new TextComponentTranslation(key + ".blockPlaced", blockName));
        tooltip.add(new TextComponentTranslation(key + ".count", torches, maxTorches));
        tooltip.add(new TextComponentTranslation(key + ".autoFill." + (autoFill ? "on" : "off")));
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

    @Nullable
    private static Block getBlockPlaced(ItemStack stack) {
        // TODO
        return Blocks.TORCH;
    }
    
    private static NBTTagCompound getData(ItemStack stack) {
        return stack.getOrCreateChildTag(NBT_ROOT);
    }

    private static int getTorchCount(ItemStack stack) {
        return getData(stack).getInt(NBT_COUNT);
    }

    private static void setTorchCount(ItemStack stack, int value) {
        getData(stack).setInt(NBT_COUNT, value);
    }

    private static int getMaxTorchCount(ItemStack stack) {
        // TODO
        return Config.GENERAL.maxTorchCount.get();
    }

    private static boolean isAutoFillOn(ItemStack stack) {
        return getData(stack).getBoolean(NBT_AUTO_FILL);
    }

    private static void setAutoFill(ItemStack stack, boolean value) {
        getData(stack).setBoolean(NBT_AUTO_FILL, value);
    }
}
