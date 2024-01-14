package net.haizor.fancydyes.fabric.client.compat.azurelib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelibarmor.animatable.GeoItem;
import mod.azure.azurelibarmor.cache.object.BakedGeoModel;
import mod.azure.azurelibarmor.renderer.GeoArmorRenderer;
import mod.azure.azurelibarmor.renderer.layer.GeoRenderLayer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class AzureDyeRenderLayer<T extends Item & GeoItem> extends GeoRenderLayer<T> {
    private final GeoArmorRenderer<T> armorRenderer;
    public AzureDyeRenderLayer(GeoArmorRenderer<T> entityRendererIn) {
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
