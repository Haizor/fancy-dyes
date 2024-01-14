package net.haizor.fancydyes.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(remap = false, value = ArmorRenderer.class)
public interface MixinArmorRenderer {
    @Inject(method = "renderPart", at = @At("TAIL"))
    private static void fancydyes$renderArmorPartDye(PoseStack poseStack, MultiBufferSource source, int light, ItemStack stack, Model model, ResourceLocation texture, CallbackInfo ci) {
        Optional<FancyDye> primaryDye = FancyDye.getDye(stack, false);
        if (primaryDye.isPresent()) {
            VertexConsumer stencil = source.getBuffer(FancyDyesRendering.getArmorStencilWriter(texture, false));
            model.renderToBuffer(poseStack, stencil, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

            RenderType dyeRenderType = FancyDyesRendering.getDyeArmorType(primaryDye.get(), false);
            VertexConsumer dyeConsumer = FancyDyesRendering.PLATFORM.getArmorVertexConsumerFor(dyeRenderType, source, poseStack);
            model.renderToBuffer(poseStack, dyeConsumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }
}
