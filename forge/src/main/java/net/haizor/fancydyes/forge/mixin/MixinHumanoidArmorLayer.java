package net.haizor.fancydyes.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.FancyDyeHelper;
import net.haizor.fancydyes.client.DyeArmorVertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


import java.util.Optional;

@Mixin(HumanoidArmorLayer.class)
abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow @Final private TextureAtlas armorTrimAtlas;

    MixinHumanoidArmorLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }
    @Inject(method = "renderArmorPiece", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z", shift = At.Shift.BEFORE))
    void fancydyes$armorOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack stack, Item item, ArmorItem armorItem, Model armorModel) {
        Optional<FancyDye> optDye = FancyDye.getDye(stack, false);
        if (optDye.isEmpty()) return;

        FancyDye dye = optDye.get();
        if (!FancyDyeHelper.shouldDoBaseDyeRender(stack)) return;
        VertexConsumer consumer = new DyeArmorVertexConsumer(multiBufferSource.getBuffer(FancyDyesRendering.getDyeArmorType(dye, false)), livingEntity, poseStack);
        armorModel.renderToBuffer(poseStack, consumer, i, OverlayTexture.NO_OVERLAY, dye.getColor().x, dye.getColor().y, dye.getColor().z, 1f);
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    void fancydyes$armorTrimOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, Item item, ArmorItem armorItem, Model model, boolean bl) {
        Optional<FancyDye> optDye = FancyDye.getDye(itemStack, true);
        if (optDye.isEmpty()) return;
        Optional<ArmorTrim> optTrim = ArmorTrim.getTrim(livingEntity.level().registryAccess(), itemStack);
        if (optTrim.isEmpty()) return;

        FancyDye dye = optDye.get();

        VertexConsumer consumer = new DyeArmorVertexConsumer(multiBufferSource.getBuffer(FancyDyesRendering.getDyeArmorType(dye, true)), livingEntity, poseStack);
        poseStack.pushPose();
        poseStack.scale(1.01f, 1.01f, 1.01f);
        humanoidModel.renderToBuffer(poseStack, consumer, i, OverlayTexture.NO_OVERLAY, dye.getColor().x, dye.getColor().y, dye.getColor().z, 1.0f);
        poseStack.popPose();
    }

    @Redirect(method = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    void fancydyes$scaleTrim(Model instance, PoseStack poseStack, VertexConsumer vertexConsumer, int i, int f, float r, float g, float b, float a, ArmorMaterial am, PoseStack poseStack2, MultiBufferSource source, int i2, ArmorTrim trim, Model model2, boolean bl) {
        poseStack.pushPose();
        poseStack.scale(1.01f, 1.01f, 1.01f);
        TextureAtlasSprite textureAtlasSprite = this.armorTrimAtlas.getSprite(bl ? trim.innerTexture(am) : trim.outerTexture(am));
        VertexConsumer consumer = textureAtlasSprite.wrap(source.getBuffer(FancyDyesRendering.armorTrimOffset()));
        instance.renderToBuffer(poseStack, consumer, i, f, r, g, b, a);
        poseStack.popPose();
    }
}
