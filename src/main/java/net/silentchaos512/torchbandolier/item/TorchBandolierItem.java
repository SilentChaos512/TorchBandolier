package net.silentchaos512.torchbandolier.item;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.lib.item.FakeItemUseContext;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.torchbandolier.Config;
import net.silentchaos512.torchbandolier.setup.ModDataComponents;
import net.silentchaos512.torchbandolier.setup.ModItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TorchBandolierItem extends Item {
    private static final int ABSORB_DELAY = 20;

    private final Supplier<Block> torchBlock;
    private final TagKey<Item> acceptedTorches;

    public TorchBandolierItem(Supplier<Block> torchBlockSupplier, TagKey<Item> acceptedTorches) {
        super(new Properties()
                .stacksTo(1)
                .setNoRepair()
        );
        this.torchBlock = torchBlockSupplier;
        this.acceptedTorches = acceptedTorches;
    }

    public TagKey<Item> getAcceptedTorches() {
        return this.acceptedTorches;
    }

    @Nullable
    public Block getDefaultTorchBlock() {
        return torchBlock.get();
    }

    public static ItemStack createStack(TorchBandolierItem item, Block torch, int torchCount) {
        ItemStack result = new ItemStack(item);
        setTorchBlock(result, torch);
        setTorchCount(result, torchCount);
        setAutoFill(result, true);
        return result;
    }

    public static ItemStack createCopyWithNewCount(ItemStack stack, int newTorchCount, boolean autoFill) {
        if (stack.getItem() instanceof TorchBandolierItem torchBandolierItem) {
            ItemStack result = new ItemStack(stack.getItem());
            setTorchBlock(result, Objects.requireNonNull(torchBandolierItem.getTorchBlock(stack)));
            setTorchCount(result, newTorchCount);
            setAutoFill(result, autoFill);
            return result;
        }
        throw new IllegalArgumentException("Item is not a torch bandolier: " + stack);
    }

    public ItemStack createFullStack() {
        if (this.getDefaultTorchBlock() != null) {
            return createStack(this, this.getDefaultTorchBlock(), getMaxTorchCount());
        }
        return new ItemStack(this);
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
        Block torch = getTorchBlock(stack);
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
        ItemStack stack = playerIn.getItemInHand(handIn);
        Block torch = getTorchBlock(stack);
        if (torch == null || torch instanceof AirBlock) {
            return super.use(worldIn, playerIn, handIn);
        }

        if (!playerIn.level().isClientSide && playerIn.isCrouching()) {
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
        ItemStack stack = context.getItemInHand();
        Block torch = getTorchBlock(stack);
        if (torch == null || torch instanceof AirBlock) {
            return InteractionResult.PASS;
        }

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
            player.getInventory().setItem(getItemSlot(player, stack), ModItems.EMPTY_TORCH_BANDOLIER.toStack());
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
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        String key = "item.torchbandolier.torch_bandolier";
        Block torch = getTorchBlock(stack);
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
        return getTorchBlock(stack) != null;
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

    @Nullable
    public Block getTorchBlock(ItemStack stack) {
        var block = stack.get(ModDataComponents.TORCH);
        return block != null ? block : this.getDefaultTorchBlock();
    }

    public static void setTorchBlock(ItemStack stack, Block torch) {
        stack.set(ModDataComponents.TORCH, torch);
    }

    public static int getTorchCount(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TORCH_COUNT, 0);
    }

    public static void setTorchCount(ItemStack stack, int value) {
        stack.set(ModDataComponents.TORCH_COUNT, value);
    }

    public static int getMaxTorchCount() {
        return Config.maxTorchCount;
    }

    public static int getMaxTorchCount(ItemStack stack) {
        return getMaxTorchCount();
    }

    public static boolean isAutoFillOn(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.AUTOFILL, true);
    }

    public static void setAutoFill(ItemStack stack, boolean value) {
        stack.set(ModDataComponents.AUTOFILL, value);
    }
}
