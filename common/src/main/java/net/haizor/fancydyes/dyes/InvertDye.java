package net.haizor.fancydyes.dyes;

import java.awt.*;

public class InvertDye implements FancyDye {
    @Override
    public String getArmorRenderType() {
        return "invert_armor";
    }

    @Override
    public String getItemRenderType() {
        return "invert_armor";
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public Type getType() {
        return Type.BASE;
    }
}
