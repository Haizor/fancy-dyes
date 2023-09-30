package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class ColorDye implements FancyDye {
    public final Vector3f color;

    public ColorDye(Vector3f color) {
        this.color = color;
    }

    @Override
    public Vector3f getColor() {
        return color;
    }

    @Override
    public ResourceLocation getTexture() {
        return FancyDyes.id("textures/dye/solid.png");
    }

    @Override
    public Type getType() {
        return Type.COLORED_TEXTURE;
    }
}
