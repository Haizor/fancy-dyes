package net.haizor.fancydyes.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Shadow @Final private ItemColors itemColors;

    @Shadow protected abstract void renderModelLists(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer);

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    public void fancydyes$cancelDefaultRender(ItemRenderer instance, BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer) {}

    @Inject(
        method = "render",
        at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$renderDyed(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci, boolean bl1, RenderType type, VertexConsumer vertexConsumer) {
        Optional<FancyDye> primary = FancyDye.getDye(itemStack, false);
        Optional<FancyDye> secondary = FancyDye.getDye(itemStack, true);

        if (primary.isEmpty() && secondary.isEmpty()) {
            this.renderModelLists(bakedModel, itemStack, i, j, poseStack, vertexConsumer);
        } else {
            RandomSource randomSource = RandomSource.create();

            for (Direction direction : Direction.values()) {
                randomSource.setSeed(42L);
                FancyDyesRendering.renderDyedItem(poseStack, multiBufferSource, type, bakedModel.getQuads(null, direction, randomSource), itemStack, i, j, itemColors, itemDisplayContext, primary, secondary);
            }
            randomSource.setSeed(42L);
            FancyDyesRendering.renderDyedItem(poseStack, multiBufferSource, type, bakedModel.getQuads(null, null, randomSource), itemStack, i, j, itemColors, itemDisplayContext, primary, secondary);
        }
    }
}
