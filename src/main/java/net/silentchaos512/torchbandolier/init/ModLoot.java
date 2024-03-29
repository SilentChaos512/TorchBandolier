package net.silentchaos512.torchbandolier.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.torchbandolier.TorchBandolier;

import java.util.Set;

@Mod.EventBusSubscriber(modid = TorchBandolier.MOD_ID)
public final class ModLoot {
    private static final Set<ResourceLocation> ADD_BANDOLIER_TO = ImmutableSet.of(
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.SIMPLE_DUNGEON
    );

    /*@SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        // FIXME: Use a global loot modifier?
        if (ADD_BANDOLIER_TO.contains(event.getName()) && !hasLootPool(event.getTable(), "torch_bandolier")) {
            event.getTable().add((new LootPool.Builder())
                    .name("torch_bandolier")
                    .add(LootItem.lootTableItem(ModItems.EMPTY_TORCH_BANDOLIER))
                    .when(LootItemRandomChanceCondition.randomChance(0.2f))
                    .build());
        }
    }*/

    /*@SuppressWarnings("ConstantConditions")
    private static boolean hasLootPool(LootTable table, String poolName) {
        return table.getPool(poolName) != null;
    }*/
}
