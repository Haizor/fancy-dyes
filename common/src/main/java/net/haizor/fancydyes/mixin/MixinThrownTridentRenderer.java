package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.DyedThrownTrident;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ThrownTridentRenderer.class)
abstract class MixinThrownTridentRenderer {
    @Shadow @Final private TridentModel model;

    @Shadow public abstract ResourceLocation getTextureLocation(Entity par1);

    @Inject(
        method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/TridentModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void fancydyes$thrownTridentRenderer(ThrownTrident thrownTrident, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        String dyeId = thrownTrident.getEntityData().get(DyedThrownTrident.DYE_ID);
        if (dyeId == null) return;
        FancyDye dye = FancyDye.fromId(new ResourceLocation(dyeId));
        if (dye == null) return;

        FancyDyesRendering.renderDyedModel(dye, poseStack,multiBufferSource, i, this.model, 1.0f, 1.0f, 1.0f, getTextureLocation(thrownTrident));
        poseStack.popPose();
        ci.cancel();
    }
}
