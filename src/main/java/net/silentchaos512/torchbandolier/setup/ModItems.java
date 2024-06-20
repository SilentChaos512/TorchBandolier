package net.silentchaos512.torchbandolier.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ModItems {
    public static final DeferredItem<TorchBandolierItem> EMPTY_TORCH_BANDOLIER = register("empty_torch_bandolier", () ->
            new TorchBandolierItem((Block) null));
    public static final DeferredItem<TorchBandolierItem> TORCH_BANDOLIER = register("torch_bandolier", () ->
        new TorchBandolierItem(Blocks.TORCH));
    public static final DeferredItem<TorchBandolierItem> SOUL_TORCH_BANDOLIER = register("soul_torch_bandolier", () ->
            new TorchBandolierItem(Blocks.SOUL_TORCH));
    public static final DeferredItem<TorchBandolierItem> STONE_TORCH_BANDOLIER = register("stone_torch_bandolier", () ->
            new TorchBandolierItem(getTorch(
                    new ResourceLocation("silentgear", "stone_torch"),
                    new ResourceLocation("slurpiesdongles", "stone_torch")
            )));

    private static final Map<Item, TorchBandolierItem> TORCH_BANDOLIERS = new HashMap<>();

    private ModItems() {}

    static void register() {}

    @Nullable
    private static Block getTorch(ResourceLocation... possibleIds) {
        for (ResourceLocation id : possibleIds) {
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                return BuiltInRegistries.BLOCK.get(id);
            }
        }
        return null;
    }

    @Nullable
    public static TorchBandolierItem getTorchBandolier(ItemLike torch) {
        return Registration.ITEMS.getEntries().stream()
                .filter(ro -> ro.get() instanceof TorchBandolierItem)
                .map(ro -> (TorchBandolierItem) ro.get())
                .filter(item -> item.getTorchBlock() != null && item.getTorchBlock().asItem() == torch)
                .findAny().orElse(null);
    }

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> item) {
        return Registration.ITEMS.register(name, item);
    }

    public static Stream<TorchBandolierItem> getTorchBandoliers() {
        return Registration.ITEMS.getEntries().stream()
                .filter(ro -> ro.get() instanceof TorchBandolierItem)
                .map(ro -> (TorchBandolierItem) ro.get());
    }
}
