package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/ElytraModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        cancellable = true
    )
    private void fancydyes$renderElytraModel(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack stack, ResourceLocation loc) {
        Optional<FancyDye> dye = FancyDye.getDye(stack, false);
        if (dye.isPresent()) {
            FancyDyesRendering.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, this.elytraModel, 1.0f, 1.0f, 1.0f, loc);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
