package net.haizor.fancydyes.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.fabricmc.loader.api.FabricLoader;
import net.haizor.fancydyes.client.DyeArmorVertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.client.FancyDyesXplat;
import net.haizor.fancydyes.fabric.client.compat.sodium.DyeArmorVertexConsumerSodium;
import net.haizor.fancydyes.fabric.client.compat.sodium.FancyDyesSodiumCompat;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class FancyDyesXplatFabric implements FancyDyesXplat {
    @Override
    public boolean isShaderPackEnabled() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

    @Override
    public VertexConsumer getArmorVertexConsumerFor(RenderType type, MultiBufferSource bufferSource, PoseStack poseStack) {
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            return new DyeArmorVertexConsumerSodium(bufferSource.getBuffer(type), poseStack);
        }

        return new DyeArmorVertexConsumer(bufferSource.getBuffer(type), poseStack);
    }

    @Override
    public void renderItemDye(BakedModel model, ItemStack itemStack, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            FancyDyesSodiumCompat.renderItemDye(model, itemStack, packedLight, packedOverlay, poseStack, bufferSource);
            return;
        }

        FancyDyesRendering.renderDefaultItemDyes(model, itemStack, packedLight, packedOverlay, poseStack, bufferSource);
    }

    @Override
    public void postProcessRenderType(RenderType type) {
        if (FabricLoader.getInstance().isModLoaded("iris")) {
            ((BlendingStateHolder)type).setTransparencyType(TransparencyType.DECAL);
        }
    }
}
