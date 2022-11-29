package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.DyeRenderTypes;
import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
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

import java.awt.*;

@Mixin(ElytraLayer.class)
public class MixinElytraLayer<T extends LivingEntity> {
    @Shadow @Final private ElytraModel<T> elytraModel;

    @Inject(cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION, method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ElytraModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER))
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float _f, float _g, float _h, float _j, float _k, float _l, CallbackInfo ci, ItemStack stack, ResourceLocation location) {
        FancyDye dye = FancyDye.getDye(stack);
        if (dye == null) return;
        VertexConsumer dyeConsumer = multiBufferSource.getBuffer(DyeRenderTypes.get(dye.getArmorRenderType(), location));
        Color c = dye.getColor();
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        float a = c.getAlpha() / 255f;
        this.elytraModel.renderToBuffer(poseStack, dyeConsumer, i, OverlayTexture.NO_OVERLAY, r, g, b, a);
        if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
            VertexConsumer baseConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(location));
            this.elytraModel.renderToBuffer(poseStack, baseConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        poseStack.popPose();
        ci.cancel();
    }
}
