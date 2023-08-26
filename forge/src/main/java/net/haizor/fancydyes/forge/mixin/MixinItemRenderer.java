package net.haizor.fancydyes.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(ItemRenderer.class)
abstract class MixinItemRenderer {
    @Shadow @Final private ItemColors itemColors;

    @Shadow protected abstract void renderModelLists(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer);

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    private void fancydyes$disableDefaultRenderCall(ItemRenderer renderer, BakedModel model, ItemStack stack, int x, int y, PoseStack poseStack, VertexConsumer consumer) {}


    //TODO: looks like forge baked models have a way to get quads based on render type, and a render type list???
    // this can probably be done better
    @Inject(
        method = "render",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$renderDyed(ItemStack itemStack, ItemDisplayContext context, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int x, int y, BakedModel model, CallbackInfo ci, boolean flag, boolean bl2, Iterator iterator1, BakedModel currModel, Iterator iterator2, RenderType type, VertexConsumer consumer) {
        Optional<FancyDye> primary = FancyDye.getDye(itemStack, false);
        Optional<FancyDye> secondary = FancyDye.getDye(itemStack, true);

        if (primary.isEmpty() && secondary.isEmpty()) {
            this.renderModelLists(currModel, itemStack, x, y, poseStack, consumer);
        } else {
            RandomSource randomSource = RandomSource.create();

            for (Direction direction : Direction.values()) {
                randomSource.setSeed(42L);
                FancyDyesRendering.renderDyedItem(poseStack, multiBufferSource, type, currModel.getQuads(null, direction, randomSource), itemStack, x, y, itemColors, context, primary, secondary);
            }
            randomSource.setSeed(42L);
            FancyDyesRendering.renderDyedItem(poseStack, multiBufferSource, type, currModel.getQuads(null, null, randomSource), itemStack, x, y, itemColors, context, primary, secondary);
        }
    }
}
