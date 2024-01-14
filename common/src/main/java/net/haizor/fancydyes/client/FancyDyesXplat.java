package net.haizor.fancydyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface FancyDyesXplat {
    boolean isShaderPackEnabled();
    VertexConsumer getArmorVertexConsumerFor(RenderType type, MultiBufferSource bufferSource, PoseStack poseStack);
    void renderItemDye(BakedModel model, ItemStack itemStack, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource);
    void postProcessRenderType(RenderType type);
}
