package net.silentchaos512.torchbandolier;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.torchbandolier.config.Config;
import net.silentchaos512.torchbandolier.init.Registration;

class SideProxy {
    SideProxy() {
        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcProcess);

        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);

        Config.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void imcEnqueue(InterModEnqueueEvent event) { }

    private void imcProcess(InterModProcessEvent event) { }

    private void serverAboutToStart(ServerAboutToStartEvent event) { }

    private void serverStarted(ServerStartedEvent event) { }

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        }

        private void clientSetup(FMLClientSetupEvent event) { }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) { }
    }
}

