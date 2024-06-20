package net.silentchaos512.torchbandolier;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = TorchBandolier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_TORCH_COUNT = BUILDER
            .comment("The number of torches a torch bandolier can store")
            .defineInRange("general.maxTorchCount", 1024, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxTorchCount;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        maxTorchCount = MAX_TORCH_COUNT.get();
    }
}
