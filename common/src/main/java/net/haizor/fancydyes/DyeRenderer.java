package net.haizor.fancydyes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.haizor.fancydyes.mixin.ItemRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class DyeRenderer {
    private static final Direction[] DIRS = new Direction[] {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, null};

    public static boolean renderItem(ItemStack itemStack, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, boolean bl3) {
        if (itemStack.getItem() instanceof FancyDyeItem dyeItem) {
            renderDye(itemStack, i, j, bl3, poseStack, multiBufferSource, bakedModel, dyeItem.dye);
            poseStack.popPose();
            return true;
        }
        FancyDye dye = FancyDye.getDye(itemStack);
        if (dye == null) return false;
        RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, bl3);
        renderDyed(bakedModel, itemStack, i, j, poseStack, multiBufferSource.getBuffer(DyeRenderTypes.get(dye.getItemRenderType(), TextureAtlas.LOCATION_BLOCKS)), dye);
        if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
            ((ItemRendererAccessor)Minecraft.getInstance().getItemRenderer()).invokeRenderModelLists(bakedModel, itemStack, i, j, poseStack, multiBufferSource.getBuffer(renderType));
        }

        poseStack.popPose();
        return true;
    }

    public static void renderDyed(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer, FancyDye dye) {
        RandomSource random = RandomSource.create();

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderQuadList(poseStack, vertexConsumer, bakedModel.getQuads(null, direction, random), itemStack, i, j, dye);
        }

        random.setSeed(42L);
        renderQuadList(poseStack, vertexConsumer, bakedModel.getQuads(null, null, random), itemStack, i, j, dye);
    }

    //Alpha is used to determine equipment slot in shaders for proper tiling
    public static float getAlpha(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 4.0f;
            case CHEST -> 3.0f;
            case LEGS -> 2.0f;
            case FEET -> 1.0f;
            default -> 2.5f;
        };
    }

    private static void renderDye(ItemStack itemStack, int i, int j, boolean bl, PoseStack poseStack, MultiBufferSource source, BakedModel model, FancyDye dye) {
        RandomSource random = RandomSource.create();
        VertexConsumer baseConsumer = source.getBuffer(ItemBlockRenderTypes.getRenderType(itemStack, bl));
        VertexConsumer dyeConsumer = source.getBuffer(DyeRenderTypes.get(dye.getItemRenderType(), TextureAtlas.LOCATION_BLOCKS));

        for (Direction direction : DIRS) {
            random.setSeed(42L);

            renderDyeQuads(poseStack, baseConsumer, dyeConsumer, model.getQuads(null, direction, random), i, j, dye, 1);
            poseStack.pushPose();
            poseStack.scale(1f, 1f, 0.75f);
            poseStack.translate(0f, 0f, 0.75f / 4.5f);
            renderDyeQuads(poseStack, baseConsumer, dyeConsumer, model.getQuads(null, direction, random), i, j, dye, 0);
            poseStack.popPose();
        }
    }

    private static void renderDyeQuads(PoseStack poseStack, VertexConsumer baseConsumer, VertexConsumer dyeConsumer, List<BakedQuad> list, int i, int j, FancyDye dye, int tintIndex) {
        PoseStack.Pose pose = poseStack.last();

        for (BakedQuad quad : list) {
            if (quad.getTintIndex() != tintIndex) continue;
            if (quad.getTintIndex() == 0) {
                Color c = dye.getColor();
                float f = (c.getRed() / 255f);
                float g = (c.getGreen() / 255f);
                float h = (c.getBlue() / 255f);

                dyeConsumer.putBulkData(pose, quad, f, g, h, i, j);
                if (dye.getType().equals(FancyDye.Type.OVERLAY)) {
                    baseConsumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, i, j);
                }
            } else {
                baseConsumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, i, j);
            }
        }
    }
    public static void renderQuadList(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> list, ItemStack itemStack, int i, int j, FancyDye dye) {
        PoseStack.Pose pose = poseStack.last();

        for (BakedQuad quad : list) {
            Color c = dye.getColor();
            float f = (c.getRed() / 255f);
            float g = (c.getGreen() / 255f);
            float h = (c.getBlue() / 255f);
            vertexConsumer.putBulkData(pose, quad, f, g, h, i, j);
        }
    }
}
