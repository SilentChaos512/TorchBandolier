package net.silentchaos512.torchbandolier.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TorchBandolier.MOD_ID);

    public static final DeferredItem<TorchBandolierItem> EMPTY_TORCH_BANDOLIER = register("empty_torch_bandolier", () ->
            new TorchBandolierItem(
                    () -> null,
                    ModTags.Items.ACCEPTED_TORCHES_EMPTY
            )
    );
    public static final DeferredItem<TorchBandolierItem> TORCH_BANDOLIER = register("torch_bandolier", () ->
            new TorchBandolierItem(
                    () -> Blocks.TORCH,
                    ModTags.Items.ACCEPTED_TORCHES_TORCH
            )
    );
    public static final DeferredItem<TorchBandolierItem> SOUL_TORCH_BANDOLIER = register("soul_torch_bandolier", () ->
            new TorchBandolierItem(
                    () -> Blocks.SOUL_TORCH,
                    ModTags.Items.ACCEPTED_TORCHES_SOUL_TORCH
            )
    );
    public static final DeferredItem<TorchBandolierItem> STONE_TORCH_BANDOLIER = register("stone_torch_bandolier", () ->
            new TorchBandolierItem(
                    () -> null,
                    ModTags.Items.ACCEPTED_TORCHES_STONE_TORCH
            )
    );

    private static final Map<Item, TorchBandolierItem> TORCH_BANDOLIERS = new HashMap<>();

    private ModItems() {
    }

    @Nullable
    private static Block getTorch(ResourceLocation... possibleIds) {
        for (ResourceLocation id : possibleIds) {
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                return BuiltInRegistries.BLOCK.get(id);
            }
        }
        return null;
    }

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    public static Stream<TorchBandolierItem> getTorchBandoliers() {
        return ITEMS.getEntries().stream()
                .filter(ro -> ro.get() instanceof TorchBandolierItem)
                .map(ro -> (TorchBandolierItem) ro.get());
    }
}
