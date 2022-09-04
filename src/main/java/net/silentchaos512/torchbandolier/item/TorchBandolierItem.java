package net.silentchaos512.torchbandolier.item;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
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
                .stacksTo(1)
                .setNoRepair()
                .tab(TorchBandolier.ITEM_GROUP)
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
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isClientSide && worldIn.getGameTime() % ABSORB_DELAY == 0 && entityIn instanceof Player && isAutoFillOn(stack)) {
            absorbTorches(stack, (Player) entityIn);
        }
    }

    private void absorbTorches(ItemStack stack, Player player) {
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        Block torch = getTorchBlock();
        if (torch == null || torch instanceof AirBlock) {
            return super.use(worldIn, playerIn, handIn);
        }

        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!playerIn.level.isClientSide && playerIn.isCrouching()) {
            // Toggle auto-fill
            boolean mode = !isAutoFillOn(stack);
            setAutoFill(stack, mode);
            String translationKey = "item.torchbandolier.torch_bandolier.autoFill." + (mode ? "on" : "off");
            playerIn.displayClientMessage(Component.translatable(translationKey), true);
        }
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Block torch = getTorchBlock();
        if (torch == null || torch instanceof AirBlock) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        boolean consumeTorch = player == null || !player.getAbilities().instabuild;
        if (getTorchCount(stack) <= 0 && consumeTorch) {
            // Empty and not in creative mode
            return InteractionResult.PASS;
        }

        // Create fake block stack and use it
        ItemStack fakeBlockStack = new ItemStack(torch);
        InteractionResult result = fakeBlockStack.useOn(new FakeItemUseContext(context, fakeBlockStack));

        if (result.consumesAction() && consumeTorch) {
            setTorchCount(stack, getTorchCount(stack) - 1);
        }

        if (getTorchCount(stack) == 0 && player != null) {
            player.getInventory().setItem(getItemSlot(player, stack), new ItemStack(ModItems.EMPTY_TORCH_BANDOLIER));
        }

        return result;
    }

    private static int getItemSlot(Player player, ItemStack stack) {
        // Copied from PlayerInventory.getSlotFor (it's client-side only...)
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack1 = player.getInventory().getItem(i);
            if (!stack1.isEmpty() && stack.getItem() == stack1.getItem() && ItemStack.matches(stack, stack1)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (!allowedIn(tab) || getTorchBlock() instanceof AirBlock) {
            return;
        }
        items.add(createStack(this, getMaxTorchCount()));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        String key = "item.torchbandolier.torch_bandolier";
        Block torch = getTorchBlock();
        Component blockName = torch != null && !(torch instanceof AirBlock)
                ? torch.getName()
                : Component.translatable(key + ".empty");
        tooltip.add(Component.translatable(key + ".blockPlaced", blockName));

        if (torch != null) {
            int torches = getTorchCount(stack);
            int maxTorches = getMaxTorchCount(stack);
            tooltip.add(Component.translatable(key + ".count", torches, maxTorches));
            boolean autoFill = isAutoFillOn(stack);
            tooltip.add(Component.translatable(key + ".autoFill." + (autoFill ? "on" : "off")));
        } else {
            tooltip.add(Component.translatable(key + ".emptyHint").withStyle(ChatFormatting.ITALIC));
        }
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getTorchBlock() != null;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = getMaxTorchCount(stack);
        return max > 0 ? Math.round(13f * getTorchCount(stack) / max) : 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int max = getMaxTorchCount(stack);
        float f = Math.max(0.0F, (float) getTorchCount(stack) / max);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    private static CompoundTag getData(ItemStack stack) {
        return stack.getOrCreateTagElement(NBT_ROOT);
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
