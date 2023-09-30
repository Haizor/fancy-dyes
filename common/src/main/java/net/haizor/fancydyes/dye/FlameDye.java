package net.haizor.fancydyes.dye;

import net.minecraft.resources.ResourceLocation;

public class FlameDye extends TexturedDye {
    public FlameDye(ResourceLocation texture) {
        super(texture);
        this.texturing = Texturing.FLAME;
    }


}
