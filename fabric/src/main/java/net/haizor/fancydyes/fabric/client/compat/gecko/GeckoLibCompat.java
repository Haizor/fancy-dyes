package net.haizor.fancydyes.fabric.client.compat.gecko;

import software.bernie.geckolib.event.GeoRenderEvent;

public class GeckoLibCompat {
    public static void compileLayersEvent(GeoRenderEvent.Armor.CompileRenderLayers event) {
        event.addLayer(new GeoDyeRenderLayer(event.getRenderer()));
    }
}
