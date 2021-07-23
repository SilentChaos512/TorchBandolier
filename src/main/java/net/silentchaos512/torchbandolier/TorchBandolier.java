package net.silentchaos512.torchbandolier;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.torchbandolier.init.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(TorchBandolier.MOD_ID)
@SuppressWarnings({"unused", "WeakerAccess"})
public class TorchBandolier {
    public static final String MOD_ID = "torchbandolier";
    public static final String MOD_NAME = "Torch Bandolier";
    public static final String VERSION = "1.5.0";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static TorchBandolier INSTANCE;
    public static SideProxy PROXY;

    public TorchBandolier() {
        INSTANCE = this;
        PROXY = DistExecutor.safeRunForDist(() -> SideProxy.Client::new, () -> SideProxy.Server::new);
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

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.TORCH_BANDOLIER);
        }
    };
}