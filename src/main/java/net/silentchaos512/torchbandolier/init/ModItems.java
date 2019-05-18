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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.torchbandolier.TorchBandolier;
import net.silentchaos512.torchbandolier.item.ItemTorchBandolier;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class ModItems {
    public static ItemTorchBandolier emptyTorchBandolier;
    public static ItemTorchBandolier torchBandolier;
    private static final Map<Item, ItemTorchBandolier> TORCH_BANDOLIERS = new HashMap<>();

    private ModItems() {}

    public static void registerAll(RegistryEvent.Register<Item> event) {
        if (!event.getName().equals(ForgeRegistries.ITEMS.getRegistryName())) return;

        emptyTorchBandolier = new ItemTorchBandolier((Block) null);
        torchBandolier = new ItemTorchBandolier(Blocks.TORCH);
        registerTorchBandolier("empty_torch_bandolier", emptyTorchBandolier);
        registerTorchBandolier("torch_bandolier", torchBandolier);
        registerTorchBandolier("stone_torch_bandolier", new ItemTorchBandolier(() ->
                ForgeRegistries.BLOCKS.getValue(new ResourceLocation("silentgear:stone_torch"))
        ));

        if (TorchBandolier.isDevBuild()) {
            registerTorchBandolier("test_item", new ItemTorchBandolier(Blocks.GLOWSTONE));
        }
    }

    @Nullable
    public static ItemTorchBandolier getTorchBandolier(IItemProvider torch) {
        return TORCH_BANDOLIERS.get(torch.asItem());
    }

    public static void registerTorchBandolier(ResourceLocation name, ItemTorchBandolier item) {
        Block torch = item.getTorchBlock();
        if (torch != null) {
            TORCH_BANDOLIERS.put(torch.asItem(), item);
        }
        register(name, item);
    }

    private static void registerTorchBandolier(String name, ItemTorchBandolier item) {
        registerTorchBandolier(TorchBandolier.getId(name), item);
    }

    private static void register(ResourceLocation name, Item item) {
        if (item.getRegistryName() == null) {
            item.setRegistryName(name);
        }
        ForgeRegistries.ITEMS.register(item);
    }

    public static Stream<Map.Entry<Item, ItemTorchBandolier>> getTorchBandolierPairs() {
        return TORCH_BANDOLIERS.entrySet().stream();
    }
}
