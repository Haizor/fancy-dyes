package net.haizor.fancydyes.dye;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class BrightFlameDye extends FlameDye {
    public BrightFlameDye(ResourceLocation texture) {
        super(texture);
    }

    @Override
    public BlendMode getBlendMode() {
        return BlendMode.ADDITIVE;
    }

    @Override
    public Vector3f getColor() {
        return new Vector3f(0.5f);
    }
}
