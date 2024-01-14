package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.DyeArmorVertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ElytraLayer.class)
class MixinElytraLayer<T extends LivingEntity> {
    @Shadow @Final private ElytraModel<T> elytraModel;

    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/model/ElytraModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$renderElytraDye(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack stack, ResourceLocation loc) {
        Optional<FancyDye> dye = FancyDye.getDye(stack, false);
        if (dye.isEmpty()) return;
        VertexConsumer stencilConsumer = bufferSource.getBuffer(FancyDyesRendering.getArmorStencilWriter(loc, false));
        elytraModel.renderToBuffer(poseStack, stencilConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        VertexConsumer dyeConsumer = FancyDyesRendering.PLATFORM.getArmorVertexConsumerFor(FancyDyesRendering.getDyeArmorType(dye.get(), false), bufferSource, poseStack);
        elytraModel.renderToBuffer(poseStack, dyeConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
