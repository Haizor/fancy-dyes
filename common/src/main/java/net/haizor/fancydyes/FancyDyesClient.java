package net.haizor.fancydyes;

import dev.architectury.event.events.client.ClientLifecycleEvent;

public class FancyDyesClient {
    public static void init() {
        ClientLifecycleEvent.CLIENT_STARTED.register((mc) -> {
            DyeRenderTypes.init();
        });
    }
}
