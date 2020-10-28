/*
 * TorchBandolier -- ModItems
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

package net.silentchaos512.torchbandolier.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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
    public static TorchBandolierItem getTorchBandolier(IItemProvider torch) {
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
