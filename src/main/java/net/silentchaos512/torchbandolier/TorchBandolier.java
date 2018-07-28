package net.silentchaos512.torchbandolier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.proxy.IProxy;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

@Mod(modid = TorchBandolier.MOD_ID, name = TorchBandolier.MOD_NAME, version = TorchBandolier.VERSION, dependencies = TorchBandolier.DEPENDENCIES)
public class TorchBandolier implements IModBase {
    public static final String MOD_ID = "torchbandolier";
    public static final String MOD_NAME = "Torch Bandolier";
    public static final String VERSION = "0.1.0";
    public static final String VERSION_SILENTLIB = "2.3.12";
    public static final int BUILD_NUM = 0;
    public static final String DEPENDENCIES = "required-after:silentlib@[" + VERSION_SILENTLIB + ",)";

    public static final LogHelper log = new LogHelper(MOD_NAME, BUILD_NUM);
    public static final I18nHelper i18n = new I18nHelper(MOD_ID, log, true);
    public static final SRegistry registry = new SRegistry(MOD_ID, log);
    // TODO: tab icon
    public static final CreativeTabs creativeTab = registry.makeCreativeTab(MOD_ID, () -> new ItemStack(Items.SLIME_BALL));

    @Mod.Instance(MOD_ID)
    public static TorchBandolier instance;

    @SidedProxy(clientSide = "net.silentchaos512.torchbandolier.ClientProxy", serverSide = "net.silentchaos512.torchbandolier.CommonProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(registry, event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(registry, event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(registry, event);
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public String getModName() {
        return MOD_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public int getBuildNum() {
        return BUILD_NUM;
    }
}