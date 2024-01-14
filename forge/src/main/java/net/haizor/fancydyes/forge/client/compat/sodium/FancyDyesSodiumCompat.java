package net.haizor.fancydyes.forge.client.compat.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.model.quad.BakedQuadView;
import me.jellysquid.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import me.jellysquid.mods.sodium.client.render.texture.SpriteUtil;
import me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class FancyDyesSodiumCompat {
    public static void renderItemDye(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, MultiBufferSource source) {
        Optional<FancyDye> primary = FancyDye.getDye(itemStack, false);
        Optional<FancyDye> secondary = FancyDye.getDye(itemStack, true);

        if (primary.isPresent() || secondary.isPresent()) {
            PoseStack.Pose pose = poseStack.last();

            List<BakedQuad> secondaryQuads = FancyDyesRendering.getItemQuads(bakedModel, quad -> quad.getTintIndex() == 1);
            VertexBufferWriter secondaryStencilWriter = VertexConsumerUtils.convertOrLog(source.getBuffer(FancyDyesRendering.getItemStencilWriter(true)));

            for (BakedQuad quad : secondaryQuads) {
                BakedQuadView view = (BakedQuadView) quad;

                BakedModelEncoder.writeQuadVertices(secondaryStencilWriter, pose, view, 0xFFFFFFFF, i, j);
                SpriteUtil.markSpriteActive(quad.getSprite());
            }

            List<BakedQuad> primaryQuads = FancyDyesRendering.getItemQuads(bakedModel, quad -> quad.getTintIndex() != 1);
            VertexBufferWriter primaryStencilWriter = VertexConsumerUtils.convertOrLog(source.getBuffer(FancyDyesRendering.getItemStencilWriter(false)));

            for (BakedQuad quad : primaryQuads) {
                BakedQuadView view = (BakedQuadView) quad;

                BakedModelEncoder.writeQuadVertices(primaryStencilWriter, pose, view, 0xFFFFFFFF, i, j);
                SpriteUtil.markSpriteActive(quad.getSprite());
            }

            if (secondary.isPresent()) {
                VertexBufferWriter dyeWriter = VertexConsumerUtils.convertOrLog(source.getBuffer(FancyDyesRendering.getDyeItemType(secondary.get(), itemStack.is(FancyDye.DIAGONAL_SCROLL), true)));

                for (BakedQuad quad : secondaryQuads) {
                    BakedQuadView view = (BakedQuadView) quad;

                    BakedModelEncoder.writeQuadVertices(dyeWriter, pose, view, 0xFFFFFFFF, i, j);
                    SpriteUtil.markSpriteActive(quad.getSprite());
                }
            }

            if (primary.isPresent()) {
                VertexBufferWriter dyeWriter = VertexConsumerUtils.convertOrLog(source.getBuffer(FancyDyesRendering.getDyeItemType(primary.get(), itemStack.is(FancyDye.DIAGONAL_SCROLL), false)));

                for (BakedQuad quad : primaryQuads) {
                    BakedQuadView view = (BakedQuadView) quad;

                    BakedModelEncoder.writeQuadVertices(dyeWriter, pose, view, 0xFFFFFFFF, i, j);
                    SpriteUtil.markSpriteActive(quad.getSprite());
                }
            }

        }
    }
}
