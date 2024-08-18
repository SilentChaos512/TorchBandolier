package net.silentchaos512.torchbandolier.setup;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.silentchaos512.torchbandolier.TorchBandolier;

public class ModTags {
    public static final class Items {
        public static final TagKey<Item> ACCEPTED_TORCHES_EMPTY = mod("accepted_torches/empty");
        public static final TagKey<Item> ACCEPTED_TORCHES_TORCH = mod("accepted_torches/torch");
        public static final TagKey<Item> ACCEPTED_TORCHES_SOUL_TORCH = mod("accepted_torches/soul_torch");
        public static final TagKey<Item> ACCEPTED_TORCHES_STONE_TORCH = mod("accepted_torches/stone_torch");

        private static TagKey<Item> mod(String path) {
            return ItemTags.create(TorchBandolier.getId(path));
        }
    }
}
