package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class BrightTexturedDye extends TexturedDye {
    public BrightTexturedDye(ResourceLocation texture) {
        super(texture);
    }

    @Override
    public String getShaderType() {
        return "texture_additive";
    }

    @Override
    public Vector3f getColor() {
        return new Vector3f(0.5f);
    }
}
