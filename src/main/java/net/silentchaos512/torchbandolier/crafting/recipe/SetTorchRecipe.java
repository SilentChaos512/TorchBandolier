package net.silentchaos512.torchbandolier.crafting.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;
import net.silentchaos512.torchbandolier.setup.ModItems;
import net.silentchaos512.torchbandolier.setup.ModRecipes;

public final class SetTorchRecipe extends CustomRecipe {
    private final TorchBandolierItem torchBandolierItem;

    public SetTorchRecipe(CraftingBookCategory category, TorchBandolierItem torchBandolierItem) {
        super(category);
        this.torchBandolierItem = torchBandolierItem;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SET_TORCH.get();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack emptyBandolier = ItemStack.EMPTY;
        ItemStack torch = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == ModItems.EMPTY_TORCH_BANDOLIER.get()) {
                if (emptyBandolier.isEmpty()) {
                    emptyBandolier = stack;
                } else {
                    return false;
                }
            } else if (stack.is(this.torchBandolierItem.getAcceptedTorches())) {
                if (torch.isEmpty()) {
                    torch = stack;
                } else {
                    return false;
                }
            }
        }

        return !emptyBandolier.isEmpty() && !torch.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider registryAccess) {
        ItemStack torch = StackList.from(inv).uniqueMatch(s -> !(s.getItem() instanceof TorchBandolierItem));
        if (torch.isEmpty() || !(torch.getItem() instanceof BlockItem)) {
            return ItemStack.EMPTY;
        }
        Block torchBlock = ((BlockItem) torch.getItem()).getBlock();
        return TorchBandolierItem.createStack(this.torchBandolierItem, torchBlock, 1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1;
    }

    public static final class Serializer implements RecipeSerializer<SetTorchRecipe> {
        private static final Codec<TorchBandolierItem> TORCH_BANDOLIER_ITEM_CODEC =
                BuiltInRegistries.ITEM.byNameCodec()
                        .comapFlatMap(
                                item -> item instanceof TorchBandolierItem torchBandolierItem
                                        ? DataResult.success(torchBandolierItem)
                                        : DataResult.error(() -> "Item is not a torch bandolier: " + BuiltInRegistries.ITEM.getKey(item)),
                                torchBandolierItem -> torchBandolierItem
                        );

        public static final MapCodec<SetTorchRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
                        TORCH_BANDOLIER_ITEM_CODEC.fieldOf("item").forGetter(r -> r.torchBandolierItem)
                ).apply(instance, SetTorchRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, TorchBandolierItem> TORCH_BANDOLIER_ITEM_STREAM_CODEC = StreamCodec.of(
                (buf, item) -> ByteBufCodecs.registry(Registries.ITEM).encode(buf, item),
                buf -> {
                    var item = ByteBufCodecs.registry(Registries.ITEM).decode(buf);
                    if (item instanceof TorchBandolierItem torchBandolierItem) {
                        return torchBandolierItem;
                    }
                    throw new IllegalStateException("Item is not a torch bandolier: " + BuiltInRegistries.ITEM.getKey(item));
                }
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SetTorchRecipe> STREAM_CODEC = StreamCodec.composite(
                CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
                TORCH_BANDOLIER_ITEM_STREAM_CODEC, r -> r.torchBandolierItem,
                SetTorchRecipe::new
        );

        @Override
        public MapCodec<SetTorchRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SetTorchRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
