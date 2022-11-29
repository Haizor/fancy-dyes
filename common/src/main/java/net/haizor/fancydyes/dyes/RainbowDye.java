package net.haizor.fancydyes.dyes;

import java.awt.*;

public class RainbowDye implements FancyDye {
    @Override
    public String getArmorRenderType() {
        return "rainbow_armor";
    }

    @Override
    public String getItemRenderType() {
        return "rainbow_item";
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public Type getType() {
        return Type.OVERLAY;

    }
}
