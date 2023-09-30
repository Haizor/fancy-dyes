package net.haizor.fancydyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Optional;

public class DyeItemVertexConsumer implements VertexConsumer {
    final VertexConsumer baseConsumer;
    VertexConsumer trimConsumer;
    VertexConsumer primaryConsumer;
    VertexConsumer secondaryConsumer;
    FancyDye primaryDye;
    FancyDye secondaryDye;

    public DyeItemVertexConsumer(MultiBufferSource source, VertexConsumer base, ItemStack stack) {
        this.baseConsumer = base;
        if (stack.is(FancyDye.DYEABLE_SECONDARY) || stack.getItem() instanceof FancyDyeItem)
            this.trimConsumer = source.getBuffer(FancyDyesRendering.itemTrimOffset(TextureAtlas.LOCATION_BLOCKS));

        Optional<FancyDye> primary = FancyDye.getDye(stack, false);
        Optional<FancyDye> secondary = FancyDye.getDye(stack, true);

        boolean isDiagonal = stack.is(FancyDye.DIAGONAL_SCROLL);

        if (primary.isPresent()) {
            primaryDye = primary.get();
            primaryConsumer = source.getBuffer(FancyDyesRendering.getDyeItemType(primary.get(), isDiagonal, false));
        }

        if (secondary.isPresent()) {
            secondaryDye = secondary.get();
            secondaryConsumer = source.getBuffer(FancyDyesRendering.getDyeItemType(secondary.get(), isDiagonal, true));
        }
    }

    @Override
    public VertexConsumer vertex(double d, double e, double f) {
        baseConsumer.vertex(d, e, f);
        if (primaryConsumer != null)
            primaryConsumer.vertex(d, e, f);
        return this;
    }

    @Override
    public VertexConsumer color(int i, int j, int k, int l) {
        baseConsumer.color(i, j, k, l);
        if (primaryConsumer != null)
            primaryConsumer.color(i, j, k, l);
        return this;
    }

    @Override
    public VertexConsumer uv(float f, float g) {
        baseConsumer.uv(f, g);
        if (primaryConsumer != null)
            primaryConsumer.uv(f, g);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int i, int j) {
        baseConsumer.overlayCoords(i, j);
        if (primaryConsumer != null)
            primaryConsumer.overlayCoords(i, j);
        return this;
    }

    @Override
    public VertexConsumer uv2(int i, int j) {
        baseConsumer.uv2(i, j);
        if (primaryConsumer != null)
            primaryConsumer.uv2(i, j);
        return this;
    }

    @Override
    public VertexConsumer normal(float f, float g, float h) {
        baseConsumer.normal(f, g, h);
        if (primaryConsumer != null)
            primaryConsumer.normal(f, g, h);
        return this;
    }

    @Override
    public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        baseConsumer.vertex(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
        if (primaryConsumer != null)
            primaryConsumer.vertex(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
    }

    @Override
    public void endVertex() {
        baseConsumer.endVertex();
        if (primaryConsumer != null)
            primaryConsumer.endVertex();
    }

    @Override
    public void defaultColor(int i, int j, int k, int l) {
        baseConsumer.defaultColor(i, j, k, l);
        if (primaryConsumer != null)
            primaryConsumer.defaultColor(i, j, k, l);
    }

    @Override
    public void unsetDefaultColor() {
        baseConsumer.unsetDefaultColor();
        if (primaryConsumer != null)
            primaryConsumer.unsetDefaultColor();
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float[] fs, float r, float g, float b, int[] is, int i, boolean bl) {
        this.putBulkData(pose, bakedQuad, fs, r, g, b, 1.0f, is, i, bl);
    }

    public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float[] fs, float r, float g, float b, float a, int[] is, int i, boolean bl) {
        if (bakedQuad.getTintIndex() > 0 && this.trimConsumer != null) {
            trimConsumer.putBulkData(pose, bakedQuad, fs, r, g, b, is, i, bl);
        } else {
            baseConsumer.putBulkData(pose, bakedQuad, fs, r, g, b, is, i, bl);
        }

        if (bakedQuad.getTintIndex() == 0 && primaryConsumer != null) {
            Vector3f color = primaryDye.getColor();
            primaryConsumer.putBulkData(pose, bakedQuad, fs, color.x, color.y, color.z, is, i, bl);
        }

        if (bakedQuad.getTintIndex() > 0 && secondaryConsumer != null) {
            Vector3f color = secondaryDye.getColor();
            secondaryConsumer.putBulkData(pose, bakedQuad, fs, color.x, color.y, color.z, is, i, bl);
        }
    }
}
