package net.haizor.fancydyes.fabric.client.compat.azurelib;

import mod.azure.azurelibarmor.event.GeoRenderArmorEvent;

public class AzureLibCompat {
    public static void test(GeoRenderArmorEvent.Post event) {
//        Optional<FancyDye> dye = FancyDye.getDye(event.getItemStack(), false);
//        if (dye.isEmpty()) return;
//        Vector3f color = dye.get().getColor();
//        GeoArmorRenderer renderer = event.getRenderer();
//        var test = renderer.getAnimatable();
//
//        ResourceLocation loc = renderer.getTextureLocation((GeoAnimatable) test);
//
//        RenderType stenciLType = FancyDyesRendering.stencilWriter(loc, false);
//        RenderType dyeType = FancyDyesRendering.getDyeArmorType(dye.get(), false);
//
//        VertexConsumer stencil = event.getBufferSource().getBuffer(stenciLType);
//        GeoRenderer<GeoAnimatable> render = (GeoRenderer<GeoAnimatable>) event.getRenderer();
//
//        render.reRender(
//                event.getModel(),
//                event.getPoseStack(),
//                event.getBufferSource(),
//                event.getRenderer().getAnimatable(),
//                stenciLType,
//                stencil,
//                event.getPartialTick(),
//                event.getPackedLight(),
//                OverlayTexture.NO_OVERLAY,
//                color.x,
//                color.y,
//                color.z,
//                1.0f
//        );
//
//        VertexConsumer dyeConsumer = FancyDyesHelper.getArmorVertexConsumerFor(dye.get(), event.getBufferSource(), event.getPoseStack(), false);
//
//        render.reRender(
//                event.getModel(),
//                event.getPoseStack(),
//                event.getBufferSource(),
//                event.getRenderer().getAnimatable(),
//                dyeType,
//                dyeConsumer,
//                event.getPartialTick(),
//                event.getPackedLight(),
//                OverlayTexture.NO_OVERLAY,
//                color.x,
//                color.y,
//                color.z,
//                1.0f
//        );
    }
}
