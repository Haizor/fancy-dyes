package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.haizor.fancydyes.DyeRenderTypes;
import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.List;

@Mixin(BlockEntityWithoutLevelRenderer.class)
abstract class MixinBlockEntityWithoutLevelRenderer {
    @Shadow private ShieldModel shieldModel;

    @Shadow private TridentModel tridentModel;

    @Inject(cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION, method = "renderByItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", shift = At.Shift.AFTER))
    private void shieldRender(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci, Item item, boolean bl, Material material) {
        FancyDye dye = FancyDye.getDye(itemStack);
        if (dye == null) return;
        Color c = dye.getColor();
        float r = (c.getRed() / 255f);
        float g = (c.getGreen() / 255f);
        float b = (c.getBlue() / 255f);
        VertexConsumer baseConsumer = material.sprite().wrap(multiBufferSource.getBuffer(this.shieldModel.renderType(material.atlasLocation())));
        VertexConsumer dyeConsumer = material.sprite().wrap(multiBufferSource.getBuffer(DyeRenderTypes.get(dye.getItemRenderType(), TextureAtlas.LOCATION_BLOCKS)));
        this.shieldModel.handle().render(poseStack, dyeConsumer, i, j, r, g, b, 1.0f);
        if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
            this.shieldModel.handle().render(poseStack, baseConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (bl) {
            List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.createPatterns(ShieldItem.getColor(itemStack), BannerBlockEntity.getItemPatterns(itemStack));
            this.shieldModel.plate().render(poseStack, dyeConsumer, i, j, r, g, b, 1.0F);
            if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
                this.shieldModel.plate().render(poseStack, baseConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
            }
            renderPatterns(poseStack, multiBufferSource, i, j, this.shieldModel.plate(), material, false, list, itemStack.hasFoil());
        } else {
            this.shieldModel.plate().render(poseStack, dyeConsumer, i, j, r, g, b, 1.0F);
            if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
                this.shieldModel.plate().render(poseStack, baseConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        poseStack.popPose();
        ci.cancel();
    }

    private static void renderPatterns(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, ModelPart modelPart, Material material, boolean bl, List<Pair<BannerPattern, DyeColor>> list, boolean bl2) {

        for(int k = 0; k < 17 && k < list.size(); ++k) {
            Pair<BannerPattern, DyeColor> pair = list.get(k);
            float[] fs = pair.getSecond().getTextureDiffuseColors();
            BannerPattern bannerPattern = pair.getFirst();
            Material material2 = bl ? Sheets.getBannerMaterial(bannerPattern) : Sheets.getShieldMaterial(bannerPattern);
            modelPart.render(poseStack, material2.buffer(multiBufferSource, RenderType::entityNoOutline), i, j, fs[0], fs[1], fs[2], 1.0F);
        }

    }
}
