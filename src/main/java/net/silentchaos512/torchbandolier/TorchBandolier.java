package net.silentchaos512.torchbandolier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.torchbandolier.init.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(TorchBandolier.MOD_ID)
@SuppressWarnings({"unused", "WeakerAccess"})
public class TorchBandolier {
    public static final String MOD_ID = "torchbandolier";
    public static final String MOD_NAME = "Torch Bandolier";
    public static final String VERSION = "1.8.0";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static TorchBandolier INSTANCE;
    public static SideProxy PROXY;

    public TorchBandolier() {
        INSTANCE = this;
        PROXY = DistExecutor.safeRunForDist(() -> SideProxy.Client::new, () -> SideProxy.Server::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TorchBandolier::onBuildContentsOfCreativeTabs);
    }

    private static void onBuildContentsOfCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.EMPTY_TORCH_BANDOLIER.get());
            event.accept(ModItems.TORCH_BANDOLIER.get().createFullStack());
            event.accept(ModItems.SOUL_TORCH_BANDOLIER.get().createFullStack());
            event.accept(ModItems.STONE_TORCH_BANDOLIER.get().createFullStack());
        }
    }

    public static String getVersion() {
        return getVersion(false);
    }

    public static String getVersion(boolean correctInDev) {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            String str = o.get().getModInfo().getVersion().toString();
            if (correctInDev && "NONE".equals(str))
                return VERSION;
            return str;
        }
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion(false));
    }

    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}