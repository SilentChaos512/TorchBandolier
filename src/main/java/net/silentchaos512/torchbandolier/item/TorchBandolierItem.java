package net.silentchaos512.torchbandolier.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.lib.item.FakeItemUseContext;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.config.Config;
import net.silentchaos512.torchbandolier.init.ModItems;
import net.silentchaos512.utils.Lazy;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TorchBandolierItem extends Item {
    private static final String NBT_ROOT = "TorchBandolier";
    private static final String NBT_AUTO_FILL = "AutoFill";
    private static final String NBT_COUNT = "Count";

    private static final int ABSORB_DELAY = 20;

    private final Lazy<Block> torchBlock;

    /**
     * Use if {@code torchBlock} already exists. If the torch may not yet exist, or may not exist at
     * all, use {@link TorchBandolierItem#TorchBandolierItem(Supplier)}.
     *
     * @param torchBlock The torch to store and place
     */
    public TorchBandolierItem(@Nullable Block torchBlock) {
        this(() -> torchBlock);
    }

    /**
     * Constructor for cases in which the existence of the torch block is questionable. It may come
     * from an optional mod, or may just not exist yet.
     *
     * @param torchBlock The torch supplier, typically involves calling {@code getValue} on {@link
     *                   net.minecraftforge.registries.ForgeRegistries#BLOCKS}.
     */
    public TorchBandolierItem(Supplier<Block> torchBlock) {
        super(new Properties()
                .maxStackSize(1)
                .setNoRepair()
                .group(TorchBandolier.ITEM_GROUP)
        );
        this.torchBlock = Lazy.of(torchBlock);
    }

    /**
     * Gets the torch this bandolier places. Could be null (empty bandolier) or air (bandolier with
     * an invalid torch).
     *
     * @return The torch block placed
     */
    @Nullable
    public Block getTorchBlock() {
        return torchBlock.get();
    }

    public static ItemStack createStack(TorchBandolierItem item, int torchCount) {
        ItemStack result = new ItemStack(item);
        setTorchCount(result, torchCount);
        setAutoFill(result, true);
        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && worldIn.getGameTime() % ABSORB_DELAY == 0 && entityIn instanceof PlayerEntity && isAutoFillOn(stack)) {
            absorbTorches(stack, (PlayerEntity) entityIn);
        }
    }

    private void absorbTorches(ItemStack stack, PlayerEntity player) {
        int maxTorches = getMaxTorchCount(stack);
        Block torch = getTorchBlock();
        if (torch == null || torch instanceof AirBlock || getTorchCount(stack) >= maxTorches) {
            return;
        }

        Item itemTorch = torch.asItem();

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

    public static int absorbTorchesFromItemEntity(ItemStack bandolier, ItemStack blockStack) {
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        Block torch = getTorchBlock();
        if (torch == null || torch instanceof AirBlock) {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }

        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!playerIn.world.isRemote && playerIn.isCrouching()) {
            // Toggle auto-fill
            boolean mode = !isAutoFillOn(stack);
            setAutoFill(stack, mode);
            String translationKey = "item.torchbandolier.torch_bandolier.autoFill." + (mode ? "on" : "off");
            playerIn.sendStatusMessage(new TranslationTextComponent(translationKey), true);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        Block torch = getTorchBlock();
        if (torch == null || torch instanceof AirBlock) {
            return ActionResultType.PASS;
        }

        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        boolean consumeTorch = player == null || !player.abilities.isCreativeMode;
        if (getTorchCount(stack) <= 0 && consumeTorch) {
            // Empty and not in creative mode
            return ActionResultType.PASS;
        }

        // Create fake block stack and use it
        ItemStack fakeBlockStack = new ItemStack(torch);
        ActionResultType result = fakeBlockStack.onItemUse(new FakeItemUseContext(context, fakeBlockStack));

        if (result == ActionResultType.SUCCESS && consumeTorch) {
            setTorchCount(stack, getTorchCount(stack) - 1);
        }

        if (getTorchCount(stack) == 0 && player != null) {
            player.replaceItemInInventory(getItemSlot(player, stack), new ItemStack(ModItems.EMPTY_TORCH_BANDOLIER));
        }

        return result;
    }

    private static int getItemSlot(PlayerEntity player, ItemStack stack) {
        // Copied from PlayerInventory.getSlotFor (it's client-side only...)
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            ItemStack stack1 = player.inventory.mainInventory.get(i);
            if (!stack1.isEmpty() && stack.getItem() == stack1.getItem() && ItemStack.areItemStacksEqual(stack, stack1)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items) {
        if (!isInGroup(tab) || getTorchBlock() instanceof AirBlock) {
            return;
        }
        items.add(createStack(this, getMaxTorchCount()));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String key = "item.torchbandolier.torch_bandolier";
        Block torch = getTorchBlock();
        ITextComponent blockName = torch != null && !(torch instanceof AirBlock)
                ? torch.getTranslatedName()
                : new TranslationTextComponent(key + ".empty");
        tooltip.add(new TranslationTextComponent(key + ".blockPlaced", blockName));

        if (torch != null) {
            int torches = getTorchCount(stack);
            int maxTorches = getMaxTorchCount(stack);
            tooltip.add(new TranslationTextComponent(key + ".count", torches, maxTorches));
            boolean autoFill = isAutoFillOn(stack);
            tooltip.add(new TranslationTextComponent(key + ".autoFill." + (autoFill ? "on" : "off")));
        } else {
            tooltip.add(new TranslationTextComponent(key + ".emptyHint").mergeStyle(TextFormatting.ITALIC));
        }
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getTorchBlock() != null;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int max = getMaxTorchCount(stack);
        return max > 0 ? 1.0 - (double) getTorchCount(stack) / (double) max : 1.0;
    }

    private static CompoundNBT getData(ItemStack stack) {
        return stack.getOrCreateChildTag(NBT_ROOT);
    }

    public static int getTorchCount(ItemStack stack) {
        return getData(stack).getInt(NBT_COUNT);
    }

    public static void setTorchCount(ItemStack stack, int value) {
        getData(stack).putInt(NBT_COUNT, value);
    }

    public static int getMaxTorchCount() {
        return Config.GENERAL.maxTorchCount.get();
    }

    public static int getMaxTorchCount(ItemStack stack) {
        return getMaxTorchCount();
    }

    public static boolean isAutoFillOn(ItemStack stack) {
        return getData(stack).getBoolean(NBT_AUTO_FILL);
    }

    public static void setAutoFill(ItemStack stack, boolean value) {
        getData(stack).putBoolean(NBT_AUTO_FILL, value);
    }
}
