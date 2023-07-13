package net.haizor.fancydyes.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
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
    @Shadow protected abstract void renderTrim(ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorTrim armorTrim, A humanoidModel, boolean bl);

    @Shadow protected abstract boolean usesInnerModel(EquipmentSlot equipmentSlot);

    @Shadow protected abstract ResourceLocation getArmorLocation(ArmorItem armorItem, boolean bl, @Nullable String string);

    @Shadow protected abstract void renderModel(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, A humanoidModel, boolean bl, float f, float g, float h, @Nullable String string);

    public MixinHumanoidArmorLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$trimRendering(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, ArmorItem armorItem) {
        boolean bl = this.usesInnerModel(equipmentSlot);
        Optional<ArmorTrim> trim = ArmorTrim.getTrim(livingEntity.level().registryAccess(), itemStack);
        if (trim.isPresent()) {
            Optional<FancyDye> dye = FancyDye.getDye(itemStack, true);

            if (dye.isPresent()) {
                FancyDyesRendering.renderTrim(dye.get(), armorItem.getMaterial(), poseStack, multiBufferSource, i, trim.get(), humanoidModel, bl);
            } else {
                this.renderTrim(armorItem.getMaterial(), poseStack, multiBufferSource, i, trim.get(), humanoidModel, bl);
            }

        }
    }

    @Redirect(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;")
    )
    private Optional<ArmorTrim> fancydyes$disableDefaultTrimRendering(RegistryAccess registryAccess, ItemStack itemStack) {
        return Optional.empty();
    }

    @Redirect(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V")
    )
    private void fancydyes$disableDefaultRender(HumanoidArmorLayer instance, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, A humanoidModel, boolean bl, float f, float g, float h, @Nullable String string) {}

    @Inject(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$renderLeatherArmor(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, ArmorItem armorItem, boolean bl, DyeableArmorItem dyeableArmorItem, int j, float f, float g, float h) {
        Optional<FancyDye> dye = FancyDye.getDye(itemStack, false);
        if (dye.isPresent()) {
            this.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, f, g, h, null);
            this.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderModel(poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, f, g, h, null);
            this.renderModel(poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, 1.0f, 1.0f, 1.0f, "overlay");
        }
    }

    @Inject(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$renderNormalArmor(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, ArmorItem armorItem, boolean bl) {
        Optional<FancyDye> dye = FancyDye.getDye(itemStack, false);
        if (dye.isPresent()) {
            this.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, 1.0f, 1.0f, 1.0f, null);
        } else {
            this.renderModel(poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, 1.0f, 1.0f, 1.0f, null);
        }
    }

    private void renderDyedModel(FancyDye dye, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, A humanoidModel, boolean bl, float f, float g, float h, @Nullable String string) {
        ResourceLocation location = this.getArmorLocation(armorItem, bl, string);
        FancyDyesRendering.renderDyedModel(dye, poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, f, g, h, location);
    }
}
