package net.silentchaos512.torchbandolier.setup;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.silentchaos512.torchbandolier.TorchBandolier;

import java.util.Set;

public final class ModLoot {
    private static final Set<ResourceLocation> ADD_BANDOLIER_TO = ImmutableSet.of(
            BuiltInLootTables.ABANDONED_MINESHAFT.location(),
            BuiltInLootTables.SIMPLE_DUNGEON.location()
    );

    @EventBusSubscriber
    public static class Injector {
        @SubscribeEvent
        public static void onLootTableLoad(LootTableLoadEvent event) {
            var poolName = "torch_bandolier";
            if (ADD_BANDOLIER_TO.contains(event.getName()) && !hasLootPool(event.getTable(), poolName)) {
                event.getTable().addPool(
                        LootPool.lootPool()
                        .name(poolName)
                        .add(LootItem.lootTableItem(ModItems.EMPTY_TORCH_BANDOLIER))
                        .when(LootItemRandomChanceCondition.randomChance(0.2f))
                        .build());
                TorchBandolier.LOGGER.info("Add torch bandolier to loot table {}", event.getName());
            }
        }

        private static boolean hasLootPool(LootTable table, String poolName) {
            return table.getPool(poolName) != null;
        }
    }
}
