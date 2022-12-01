package net.haizor.fancydyes.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.DyeRenderTypes;
import net.haizor.fancydyes.DyeRenderer;
import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;

@Mixin(HumanoidArmorLayer.class)
abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot equipmentSlot);

    @Shadow protected abstract void renderModel(PoseStack par1, MultiBufferSource par2, int par3, boolean par4, Model par5, float par6, float par7, float par8, ResourceLocation par9);

    @Shadow public abstract ResourceLocation getArmorResource(Entity par1, ItemStack par2, EquipmentSlot par3, String par4);

    @Inject(locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true, method = "renderArmorPiece", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;getArmorModelHook(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/client/model/HumanoidModel;)Lnet/minecraft/client/model/Model;"))
    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack stack, ArmorItem armorItem, Model model) {
        FancyDye dye = FancyDye.getDye(stack);
        if (dye == null) return;
        this.renderModel(poseStack, multiBufferSource, i, model, this.getArmorResource(livingEntity, stack, equipmentSlot, null), dye, equipmentSlot);
        if (stack.getItem() instanceof DyeableArmorItem) {
            renderModel(poseStack, multiBufferSource, i, false, model, 1f, 1f, 1f, this.getArmorResource(livingEntity, stack, equipmentSlot, "overlay"));
        }

        dye.renderTick(livingEntity, equipmentSlot, stack);
        ci.cancel();
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Model model, ResourceLocation loc, FancyDye dye, EquipmentSlot slot) {
        VertexConsumer dyeConsumer = multiBufferSource.getBuffer(DyeRenderTypes.get(dye.getArmorRenderType(), loc));
        Color c = dye.getColor();
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        float a = DyeRenderer.getAlpha(slot);
        model.renderToBuffer(poseStack, dyeConsumer, i, OverlayTexture.NO_OVERLAY, r, g, b, a);
        if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
            VertexConsumer baseConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(loc));
            model.renderToBuffer(poseStack, baseConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
