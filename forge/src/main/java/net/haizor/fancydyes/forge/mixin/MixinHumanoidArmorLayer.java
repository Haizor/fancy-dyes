package net.haizor.fancydyes.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.forge.client.FancyDyesXplatForge;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


import java.util.Optional;

@Mixin(HumanoidArmorLayer.class)
abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Shadow @Final private TextureAtlas armorTrimAtlas;

    @Shadow protected abstract ResourceLocation getArmorLocation(ArmorItem armorItem, boolean bl, @Nullable String string);

    MixinHumanoidArmorLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "renderArmorPiece", locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z", shift = At.Shift.BEFORE))
    void fancydyes$renderArmorDyes(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int i, A humanoidModel, CallbackInfo ci, ItemStack stack, Item item, ArmorItem armorItem, Model armorModel, boolean bl) {
        if (FancyDyesXplatForge.isGeoItem(armorItem)) return;

        Optional<FancyDye> primaryDye = FancyDye.getDye(stack, false);
        Optional<FancyDye> secondaryDye = FancyDye.getDye(stack, true);

        if (primaryDye.isPresent() || secondaryDye.isPresent()) {
            //ok, so basically without this, the order of the buffers is messed up??? and so it doesn't work with iris/oculus???
            bufferSource.getBuffer(FancyDyesRendering.getArmorStencilWriter(this.getArmorLocation(armorItem, false, null), false));
            bufferSource.getBuffer(FancyDyesRendering.getArmorStencilWriter(this.getArmorLocation(armorItem, true, null), false));

            VertexConsumer mainStencil = bufferSource.getBuffer(FancyDyesRendering.getArmorStencilWriter(this.getArmorLocation(armorItem, bl, null), false));
            armorModel.renderToBuffer(poseStack, mainStencil, i, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

            Optional<ArmorTrim> trim = ArmorTrim.getTrim(entity.level().registryAccess(), stack);
            if (trim.isPresent()) {
                TextureAtlasSprite sprite = armorTrimAtlas.getSprite(bl ? trim.get().innerTexture(armorItem.getMaterial()) : trim.get().outerTexture(armorItem.getMaterial()));
                VertexConsumer trimStencil = sprite.wrap(bufferSource.getBuffer(FancyDyesRendering.getArmorStencilWriter(Sheets.ARMOR_TRIMS_SHEET, true)));

                armorModel.renderToBuffer(poseStack, trimStencil, i, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }

            if (primaryDye.isPresent()) {
                RenderType type = FancyDyesRendering.getDyeArmorType(primaryDye.get(), false);
                VertexConsumer primaryConsumer = FancyDyesRendering.PLATFORM.getArmorVertexConsumerFor(type, bufferSource, poseStack);
                armorModel.renderToBuffer(poseStack, primaryConsumer, i, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }

            if (secondaryDye.isPresent()) {
                RenderType type = FancyDyesRendering.getDyeArmorType(secondaryDye.get(), true);
                VertexConsumer secondaryConsumer = FancyDyesRendering.PLATFORM.getArmorVertexConsumerFor(type, bufferSource, poseStack);
                armorModel.renderToBuffer(poseStack, secondaryConsumer, i, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            }
        }
    }
}
