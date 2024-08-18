package net.silentchaos512.torchbandolier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.silentchaos512.torchbandolier.setup.ModDataComponents;
import net.silentchaos512.torchbandolier.setup.ModItems;
import net.silentchaos512.torchbandolier.setup.ModRecipes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TorchBandolier.MOD_ID)
@SuppressWarnings({"unused", "WeakerAccess"})
public class TorchBandolier {
    public static final String MOD_ID = "torchbandolier";
    public static final String MOD_NAME = "Torch Bandolier";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static TorchBandolier INSTANCE;

    public TorchBandolier(IEventBus modEventBus, ModContainer modContainer) {
        INSTANCE = this;

        modEventBus.addListener(TorchBandolier::onBuildContentsOfCreativeTabs);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModDataComponents.REGISTRAR.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
    }

    private static void onBuildContentsOfCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.EMPTY_TORCH_BANDOLIER.get());
            event.accept(ModItems.TORCH_BANDOLIER.get().createFullStack());
            event.accept(ModItems.SOUL_TORCH_BANDOLIER.get().createFullStack());
            event.accept(ModItems.STONE_TORCH_BANDOLIER.get().createFullStack());
        }
    }

    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}