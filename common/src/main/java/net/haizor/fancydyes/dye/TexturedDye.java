package net.haizor.fancydyes.dye;

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
    public Matrix4f getTextureMatrix() {
        return texturing.supplyTexturing();
    }

    @Override
    public ResourceLocation getTexture() {
        return this.texture;
    }

    public TexturedDye withTexturing(Texturing texturing) {
        this.texturing = texturing;
        return this;
    }

    @FunctionalInterface
    public interface Texturing {
        Matrix4f supplyTexturing();

        Texturing STATIC = Matrix4f::new;

        Texturing VERTICAL_SCROLL = scroll(180, 0.2f);

        Texturing FLAME = scroll(0f, 1f);

        static Texturing scroll(float angle, float speed) {
            return () -> {
                long l = Util.getMillis();
                float h = (l / 1000f) * (speed);

                return new Matrix4f().translation(0.0f, h * 2, 0.0f).rotateZ((float)Math.toRadians(angle));
            };
        }
    }
}
