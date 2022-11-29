package net.haizor.fancydyes.dyes;

import java.awt.*;

public class ShimmerDye implements FancyDye {
    public Color color;
    public ShimmerDye(Color color) {
        this.color = color;
    }

    @Override
    public String getArmorRenderType() {
        return "shimmer_armor";
    }

    @Override
    public String getItemRenderType() {
        return "shimmer_item";
    }

    @Override
    public Color getColor() {
        return color;
    }
}
