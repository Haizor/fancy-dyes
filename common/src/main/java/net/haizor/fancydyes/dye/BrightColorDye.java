package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class BrightColorDye implements FancyDye {
    public Vector3f color;

    public BrightColorDye(Vector3f color) {
        this.color = color;
    }

    @Override
    public String getShaderType() {
        return "color_additive";
    }

    @Override
    public Vector3f getColor() {
        return color.mul(0.5f, new Vector3f());
    }
}
