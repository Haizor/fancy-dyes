package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BlockEntityWithoutLevelRenderer.class)
class MixinBlockEntityWithoutLevelRenderer {
    @Shadow private TridentModel tridentModel;

    @Inject(
        method = "renderByItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/TridentModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
        ),
        cancellable = true
    )
    private void fancydyes$tridentRender(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci) {
        Optional<FancyDye> dye = FancyDye.getDye(itemStack, false);
        if (dye.isPresent()) {
            FancyDyesRendering.renderDyedModel(dye.get(), poseStack, multiBufferSource, i, tridentModel, 1.0f, 1.0f, 1.0f, TridentModel.TEXTURE);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
