package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class BrightColorDye implements FancyDye {
    public final FancyDyeColor color;

    public BrightColorDye(FancyDyeColor color) {
        this.color = color;
    }

    @Override
    public BlendMode getBlendMode() {
        return BlendMode.ADDITIVE;
    }

    @Override
    public ResourceLocation getTexture() {
        return FancyDyes.id("textures/dye/color/%s.png".formatted(color.name().toLowerCase()));
    }
}
