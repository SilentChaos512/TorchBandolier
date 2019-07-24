package net.silentchaos512.torchbandolier.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.torchbandolier.TorchBandolier;

import java.util.Set;

@Mod.EventBusSubscriber(modid = TorchBandolier.MOD_ID)
public final class ModLoot {
    private static final Set<ResourceLocation> ADD_BANDOLIER_TO = ImmutableSet.of(
            LootTables.CHESTS_ABANDONED_MINESHAFT,
            LootTables.CHESTS_SIMPLE_DUNGEON
    );

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (ADD_BANDOLIER_TO.contains(event.getName()) && !hasLootPool(event.getTable(), "torch_bandolier")) {
            event.getTable().addPool((new LootPool.Builder())
                    .name("torch_bandolier")
                    .addEntry(ItemLootEntry.builder(ModItems.emptyTorchBandolier))
                    .acceptCondition(RandomChance.builder(0.2f))
                    .build());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static boolean hasLootPool(LootTable table, String poolName) {
        return table.getPool(poolName) != null;
    }
}
