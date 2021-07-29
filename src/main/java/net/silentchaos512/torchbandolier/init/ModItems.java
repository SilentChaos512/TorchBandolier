package net.silentchaos512.torchbandolier.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.torchbandolier.item.TorchBandolierItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ModItems {
    public static final ItemRegistryObject<TorchBandolierItem> EMPTY_TORCH_BANDOLIER = register("empty_torch_bandolier", () ->
            new TorchBandolierItem((Block) null));
    public static final ItemRegistryObject<TorchBandolierItem> TORCH_BANDOLIER = register("torch_bandolier", () ->
        new TorchBandolierItem(Blocks.TORCH));
    public static final ItemRegistryObject<TorchBandolierItem> SOUL_TORCH_BANDOLIER = register("soul_torch_bandolier", () ->
            new TorchBandolierItem(Blocks.SOUL_TORCH));
    public static final ItemRegistryObject<TorchBandolierItem> STONE_TORCH_BANDOLIER = register("stone_torch_bandolier", () ->
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
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            if (block != null) {
                return block;
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

    private static <T extends Item> ItemRegistryObject<T> register(String name, Supplier<T> item) {
        return new ItemRegistryObject<>(Registration.ITEMS.register(name, item));
    }

    public static Stream<TorchBandolierItem> getTorchBandoliers() {
        return Registration.ITEMS.getEntries().stream()
                .filter(ro -> ro.get() instanceof TorchBandolierItem)
                .map(ro -> (TorchBandolierItem) ro.get());
    }
}
