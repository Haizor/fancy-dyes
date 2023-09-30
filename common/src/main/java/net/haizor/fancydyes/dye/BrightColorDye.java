package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class BrightColorDye implements FancyDye {
    public final Vector3f color;

    public BrightColorDye(Vector3f color) {
        this.color = color;
    }

    @Override
    public BlendMode getBlendMode() {
        return BlendMode.ADDITIVE;
    }

    @Override
    public Type getType() {
        return Type.COLORED_TEXTURE;
    }

    @Override
    public Vector3f getColor() {

        return color.mul(0.75f, new Vector3f());
    }

    @Override
    public ResourceLocation getTexture() {
        return FancyDyes.id("textures/dye/solid.png");
    }
}
