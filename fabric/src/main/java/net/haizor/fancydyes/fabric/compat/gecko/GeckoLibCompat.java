package net.haizor.fancydyes.fabric.compat.gecko;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.DyeArmorVertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.Optional;

public class GeckoLibCompat {
    public static void postRenderEvent(GeoRenderEvent.Armor.Post event) {
        Optional<FancyDye> dye = FancyDye.getDye(event.getItemStack(), false);
        if (dye.isEmpty()) return;
        Vector3f color = dye.get().getColor();
        RenderType type = FancyDyesRendering.getDyeArmorType(dye.get(), false);
        VertexConsumer consumer = new DyeArmorVertexConsumer(event.getBufferSource().getBuffer(type), event.getEntity(), event.getPoseStack());
        GeoRenderer<GeoAnimatable> render = (GeoRenderer<GeoAnimatable>) event.getRenderer();
        render.reRender(
            event.getModel(),
            event.getPoseStack(),
            event.getBufferSource(),
            event.getRenderer().getAnimatable(),
            type,
            consumer,
            event.getPartialTick(),
            event.getPackedLight(),
            OverlayTexture.NO_OVERLAY,
            color.x,
            color.y,
            color.z,
            1.0f
        );
    }
}
