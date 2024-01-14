package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class ColorDye implements FancyDye {
    public final FancyDyeColor color;

    public ColorDye(FancyDyeColor color) {
        this.color = color;
    }

    @Override
    public ResourceLocation getTexture() {
        return FancyDyes.id("textures/dye/color/%s.png".formatted(color.name().toLowerCase()));
    }
}
