package net.haizor.fancydyes.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.coderbot.batchedentityrendering.impl.BlendingStateHolder;
import net.coderbot.batchedentityrendering.impl.TransparencyType;
import net.haizor.fancydyes.client.DyeArmorVertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.client.FancyDyesXplat;
import net.haizor.fancydyes.forge.client.compat.sodium.DyeArmorVertexConsumerSodium;
import net.haizor.fancydyes.forge.client.compat.sodium.FancyDyesSodiumCompat;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import software.bernie.geckolib.animatable.GeoItem;

public class FancyDyesXplatForge implements FancyDyesXplat {
    @Override
    public boolean isShaderPackEnabled() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

    @Override
    public VertexConsumer getArmorVertexConsumerFor(RenderType type, MultiBufferSource bufferSource, PoseStack poseStack) {
        if (ModList.get().isLoaded("sodium")) {
            return new DyeArmorVertexConsumerSodium(bufferSource.getBuffer(type), poseStack);
        }
        return new DyeArmorVertexConsumer(bufferSource.getBuffer(type), poseStack);
    }

    @Override
    public void renderItemDye(BakedModel model, ItemStack itemStack, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (ModList.get().isLoaded("sodium")) {
            FancyDyesSodiumCompat.renderItemDye(model, itemStack, packedLight, packedOverlay, poseStack, bufferSource);
            return;
        }

        FancyDyesRendering.renderDefaultItemDyes(model, itemStack, packedLight, packedOverlay, poseStack, bufferSource);
    }

    @Override
    public void postProcessRenderType(RenderType type) {
        if (ModList.get().isLoaded("iris")) {
            ((BlendingStateHolder)type).setTransparencyType(TransparencyType.DECAL);
        }
    }

    public static boolean isGeoItem(Item item) {
        if (ModList.get().isLoaded("geckolib")) {
            return item instanceof GeoItem;
        }
        return false;
    }
}
