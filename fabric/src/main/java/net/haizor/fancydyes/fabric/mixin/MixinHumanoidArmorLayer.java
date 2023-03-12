package net.haizor.fancydyes.fabric.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.DyeRenderTypes;
import net.haizor.fancydyes.DyeRenderer;
import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;

@Mixin(HumanoidArmorLayer.class)
abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
    @Shadow
    protected abstract void setPartVisibility(A humanoidModel, EquipmentSlot equipmentSlot);

    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot equipmentSlot);

    @Shadow protected abstract ResourceLocation getArmorLocation(ArmorItem armorItem, boolean bl, @Nullable String string);

    @Shadow protected abstract void renderModel(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, boolean bl, A humanoidModel, boolean bl2, float f, float g, float h, @Nullable String string);

    @Shadow protected abstract A getArmorModel(EquipmentSlot equipmentSlot);

    @Inject(at = @At("TAIL"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        this.renderDye(poseStack, multiBufferSource, livingEntity, EquipmentSlot.HEAD, i, this.getArmorModel(EquipmentSlot.HEAD));
        this.renderDye(poseStack, multiBufferSource, livingEntity, EquipmentSlot.CHEST, i, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderDye(poseStack, multiBufferSource, livingEntity, EquipmentSlot.LEGS, i, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderDye(poseStack, multiBufferSource, livingEntity, EquipmentSlot.FEET, i, this.getArmorModel(EquipmentSlot.FEET));
    }

    private void renderDye(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel) {
        ItemStack stack = livingEntity.getItemBySlot(equipmentSlot);
        if (!(stack.getItem() instanceof ArmorItem armorItem)) return;
        FancyDye dye = FancyDye.getDye(stack);
        if (dye == null) return;
        boolean bl = this.usesInnerModel(equipmentSlot);
        this.setPartVisibility(humanoidModel, equipmentSlot);
        this.renderModel(poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, (String) null, dye);
    }

//    @Inject(locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true, method = "renderArmorPiece", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
//    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack stack, ArmorItem armorItem) {
//        boolean bl = this.usesInnerModel(equipmentSlot);
//        FancyDye dye = FancyDye.getDye(stack);
//        if (dye == null) return;
//        this.renderModel(poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, (String) null, dye);
//        if (stack.getItem() instanceof DyeableArmorItem) {
//            renderModel(poseStack, multiBufferSource, i, armorItem, false, humanoidModel, bl, 1, 1,1, "overlay");
//        }
//        dye.renderTick(livingEntity, equipmentSlot, stack);
//        ci.cancel();
//    }

    private void renderModel(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, A humanoidModel, boolean bl2, @Nullable String string, FancyDye dye) {
        ResourceLocation loc = this.getArmorLocation(armorItem, bl2, string);
        VertexConsumer dyeConsumer = multiBufferSource.getBuffer(DyeRenderTypes.get(dye.getArmorRenderType(), loc));
        Color c = dye.getColor();
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        float a = DyeRenderer.getAlpha(armorItem.getSlot());
        humanoidModel.renderToBuffer(poseStack, dyeConsumer, i, OverlayTexture.NO_OVERLAY, r, g, b, a);
    }


}
