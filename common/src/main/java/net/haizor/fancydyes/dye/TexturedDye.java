package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.client.FancyDyeRenderSystem;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TexturedDye implements FancyDye {
    private final ResourceLocation texture;

    protected Texturing texturing = Texturing.STATIC;

    public TexturedDye(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void setupRenderState() {
        FancyDyeRenderSystem.setTexture(3, texture);
        FancyDyeRenderSystem.setDyeMatrix(this.texturing.supplyTexturing().mul(FancyDyeRenderSystem.getDyeMatrix()));
    }

    public TexturedDye withTexturing(Texturing texturing) {
        this.texturing = texturing;
        return this;
    }

    @Override
    public String getShaderType() {
        return "texture_multiply";
    }

    @FunctionalInterface
    public interface Texturing {
        Matrix4f supplyTexturing();

        Texturing STATIC = Matrix4f::new;

        Texturing VERTICAL_SCROLL = scroll(0f, 0.2f);

        Texturing FLAME = scroll(180f, 1f);

        static Texturing scroll(float angle, float speed) {
            return () -> {
                long l = Util.getMillis();
                float h = (l / 1000f) * (speed);

                return new Matrix4f().translation(0.0f, h * 2, 0.0f).rotate((float)Math.toRadians(angle), 0, 0, 1);
            };
        }
    }
}
