package net.haizor.fancydyes.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
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
    @Shadow protected abstract void renderModel(PoseStack par1, MultiBufferSource par2, int par3, ArmorItem par4, Model par5, boolean par6, float par7, float par8, float par9, ResourceLocation par10);

    @Shadow public abstract ResourceLocation getArmorResource(Entity par1, ItemStack par2, EquipmentSlot par3, String par4);

    @Shadow protected abstract void renderTrim(ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorTrim armorTrim, A humanoidModel, boolean bl);

    @Shadow protected abstract void renderTrim(ArmorMaterial par1, PoseStack par2, MultiBufferSource par3, int par4, ArmorTrim par5, Model par6, boolean par7);

    MixinHumanoidArmorLayer(RenderLayerParent<T, M> arg) {
        super(arg);
    }

    @Redirect(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V")
    )
    private void fancydyes$disableDefaultRender(HumanoidArmorLayer instance, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, Model humanoidModel, boolean bl, float f, float g, float h, ResourceLocation location) {}

    @Inject(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void fancydyes$renderLeatherArmor(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot slot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, Item item, ArmorItem armorItem, Model model, boolean flag, int color, float r, float g, float b) {
        Optional<FancyDye> dye = FancyDye.getDye(itemStack, false);
        if (dye.isPresent()) {
            this.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, armorItem, humanoidModel, flag, r, g, b, getArmorResource(livingEntity, itemStack, slot, null));
            this.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, armorItem, humanoidModel, flag, 1.0f, 1.0f, 1.0f, getArmorResource(livingEntity, itemStack, slot, "overlay"));
        } else {
            this.renderModel(poseStack, multiBufferSource, i, armorItem, model, flag, r, g, b, getArmorResource(livingEntity, itemStack, slot, null));
            this.renderModel(poseStack, multiBufferSource, i, armorItem, model, flag, 1.0f, 1.0f, 1.0f, getArmorResource(livingEntity, itemStack, slot, "overlay"));
        }
    }

    @Inject(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 2, target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/Model;ZFFFLnet/minecraft/resources/ResourceLocation;)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void fancydyes$renderArmor(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot slot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, Item item, ArmorItem armorItem, Model model, boolean flag) {
        Optional<FancyDye> dye = FancyDye.getDye(itemStack, false);
        if (dye.isPresent()) {
            this.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, armorItem, model, flag, 1.0f, 1.0f, 1.0f, getArmorResource(livingEntity, itemStack, slot, null));
        } else {
            this.renderModel(poseStack, multiBufferSource, i, armorItem, model, flag, 1.0f, 1.0f, 1.0f, getArmorResource(livingEntity, itemStack, slot, null));
        }
    }

    @Redirect(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;")
    )
    private Optional<ArmorTrim> fancydyes$disableDefaultTrimRendering(RegistryAccess registryAccess, ItemStack itemStack) {
        return Optional.empty();
    }

    @Inject(
        method = "renderArmorPiece",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/armortrim/ArmorTrim;getTrim(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$trimRendering(PoseStack poseStack, MultiBufferSource multiBufferSource, T livingEntity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack itemStack, Item item, ArmorItem armorItem, Model model, boolean flag) {
        Optional<ArmorTrim> trim = ArmorTrim.getTrim(livingEntity.level().registryAccess(), itemStack);
        if (trim.isPresent()) {
            Optional<FancyDye> dye = FancyDye.getDye(itemStack, true);

            if (dye.isPresent()) {
                FancyDyesRendering.renderTrim(dye.get(), armorItem.getMaterial(), poseStack, multiBufferSource, i, trim.get(), humanoidModel, flag);
            } else {
                this.renderTrim(armorItem.getMaterial(), poseStack, multiBufferSource, i, trim.get(), model, flag);
            }

        }
    }

    private void renderDyedModel(FancyDye dye, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, Model humanoidModel, boolean bl, float f, float g, float h, ResourceLocation location) {
        FancyDyesRendering.renderDyedModel(dye, poseStack, multiBufferSource, i, armorItem, humanoidModel, bl, f, g, h, location);
    }
}