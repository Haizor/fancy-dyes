package net.haizor.fancydyes.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.client.DyeItemVertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;

public class DyeItemVertexConsumerForge extends DyeItemVertexConsumer {
    public DyeItemVertexConsumerForge(MultiBufferSource source, VertexConsumer base, ItemStack stack) {
        super(source, base, stack);
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float[] fs, float r, float g, float b, float a, int[] is, int i, boolean bl) {
        super.putBulkData(pose, bakedQuad, fs, r, g, b, a, is, i, bl);
    }
}
