package net.haizor.fancydyes.fabric.client.compat.azurelib.mixins;

import mod.azure.azurelibarmor.event.GeoRenderArmorEvent;
import mod.azure.azurelibarmor.event.GeoRenderEvent;
import mod.azure.azurelibarmor.platform.services.GeoRenderPhaseEventFactory;
import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import net.haizor.fancydyes.fabric.client.compat.azurelib.AzureDyeRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GeoArmorRenderer.class)
public class MixinGeoArmorRenderer {
    //please tell me i'm missing something. how am i supposed to register event listeners??? surely they don't just have the events with no way to use them???
    @Redirect(method = "fireCompileRenderLayersEvent", at = @At(value = "INVOKE", target = "Lmod/azure/azurelibarmor/platform/services/GeoRenderPhaseEventFactory$GeoRenderPhaseEvent;handle(Lmod/azure/azurelibarmor/event/GeoRenderEvent;)Z"), remap = false)
    private boolean fancyDyes$azureLibCompileLayers(GeoRenderPhaseEventFactory.GeoRenderPhaseEvent instance, GeoRenderEvent event) {
        GeoRenderArmorEvent.CompileRenderLayers compileEvent = (GeoRenderArmorEvent.CompileRenderLayers)event;
        compileEvent.addLayer(new AzureDyeRenderLayer(compileEvent.getRenderer()));
        return GeoRenderArmorEvent.CompileRenderLayers.EVENT.handle(event);
    }
}
