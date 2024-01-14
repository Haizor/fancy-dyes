package net.haizor.fancydyes.forge.client.compat.gecko;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Optional;

public class GeoDyeRenderLayer<T extends Item & GeoItem> extends GeoRenderLayer<T> {
    private final GeoArmorRenderer<T> armorRenderer;

    public GeoDyeRenderLayer(GeoArmorRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        armorRenderer = entityRendererIn;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        Optional<FancyDye> primaryDye = FancyDye.getDye(armorRenderer.getCurrentStack(), false);
        if (primaryDye.isEmpty()) return;

        RenderType stencilType = FancyDyesRendering.getArmorStencilWriter(armorRenderer.getTextureLocation(animatable), false);
        VertexConsumer stencilConsumer = bufferSource.getBuffer(stencilType);
        this.armorRenderer.reRender(bakedModel, poseStack, bufferSource, animatable, stencilType, stencilConsumer, partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);

        RenderType dyeType = FancyDyesRendering.getDyeArmorType(primaryDye.get(), false);
        VertexConsumer dyeConsumer = FancyDyesRendering.PLATFORM.getArmorVertexConsumerFor(dyeType, bufferSource, poseStack);
        this.armorRenderer.reRender(bakedModel, poseStack, bufferSource, animatable, stencilType, dyeConsumer, partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
    }
}
