package net.haizor.fancydyes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class FancyDyeRenderSystem {
    private static Matrix4f inverseModelViewMatrix = new Matrix4f();
    private static Matrix4f dyeMatrix = new Matrix4f();

    public static void setupDefaultState(int i, int j, int k, int l) {
        inverseModelViewMatrix.identity();
        dyeMatrix.identity();
    }

    public static void setInverseModelViewMatrix(Matrix4f mat) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                inverseModelViewMatrix = mat;
            });
        } else {
            inverseModelViewMatrix = mat;
        }
    }

    public static Matrix4f getInverseModelViewMatrix() {
        RenderSystem.assertOnRenderThread();
        return inverseModelViewMatrix;
    }

    public static void setDyeMatrix(Matrix4f mat) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                dyeMatrix = mat;
            });
        } else {
            dyeMatrix = mat;
        }
    }

    public static Matrix4f getDyeMatrix() {
        RenderSystem.assertOnRenderThread();
        return dyeMatrix;
    }

    public static void setTexture(int sampler, ResourceLocation location) {
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        manager.getTexture(location).setFilter(false, false);
        RenderSystem.setShaderTexture(sampler, location);
    }
}
