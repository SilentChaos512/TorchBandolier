package net.silentchaos512.torchbandolier.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.torchbandolier.TorchBandolier;

public class Registration {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TorchBandolier.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER, TorchBandolier.MOD_ID);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        ModItems.register();
        ModRecipes.register();
    }
}
