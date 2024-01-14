package net.haizor.fancydyes.forge.client.compat.gecko;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib.event.GeoRenderEvent;

public class GeckoLibCompat {
    @SubscribeEvent
    public static void onCompileArmorRenderLayers(GeoRenderEvent.Armor.CompileRenderLayers event) {
        event.addLayer(new GeoDyeRenderLayer<>(event.getRenderer()));
    }
}
