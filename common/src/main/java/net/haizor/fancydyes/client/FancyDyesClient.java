package net.haizor.fancydyes.client;

import dev.architectury.event.events.client.ClientReloadShadersEvent;

public class FancyDyesClient {
    public static void init() {
        ClientReloadShadersEvent.EVENT.register(FancyDyesRendering.Shaders::onShaderReload);
        FancyDyesRendering.init();
    }
}
