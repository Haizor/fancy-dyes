package net.haizor.fancydyes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DyeArmorVertexConsumer implements VertexConsumer {
    public final VertexConsumer base;
    public final Matrix4f matrix;
    protected float vertexX;
    protected float vertexY;
    protected float vertexZ;
    public DyeArmorVertexConsumer(VertexConsumer base, PoseStack pose) {
        this.base = base;
        this.matrix = pose.last().pose().invert(new Matrix4f());
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        Vector3f pos = matrix.transformPosition(new Vector3f((float)x, (float)y, (float)z));
        this.vertexX = pos.x;
        this.vertexY = pos.y;
        this.vertexZ = pos.z;
        this.base.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int i, int j, int k, int l) {
        this.base.color(i, j, k, l);
        return this;
    }

    @Override
    public VertexConsumer uv(float f, float g) {
        this.base.uv(f, vertexY);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int i, int j) {
        this.base.overlayCoords(i, j);
        return this;
    }

    @Override
    public VertexConsumer uv2(int i, int j) {
        this.base.uv2(i, j);
        return this;
    }

    @Override
    public VertexConsumer normal(float f, float g, float h) {
        this.base.normal(f, g, h);
        return this;
    }

    @Override
    public void endVertex() {
        this.base.endVertex();
    }

    @Override
    public void defaultColor(int i, int j, int k, int l) {
        this.base.defaultColor(i, j, k, l);
    }

    @Override
    public void unsetDefaultColor() {
        this.base.unsetDefaultColor();
    }
}
