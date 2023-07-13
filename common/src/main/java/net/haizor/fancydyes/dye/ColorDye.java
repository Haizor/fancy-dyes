package net.haizor.fancydyes.dye;

import org.joml.Vector3f;

public class ColorDye implements FancyDye {
    public Vector3f color;

    public ColorDye(Vector3f color) {
        this.color = color;
    }

    @Override
    public String getShaderType() {
        return "color_multiply";
    }

    @Override
    public Vector3f getColor() {
        return color;
    }
}
