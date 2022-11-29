package net.haizor.fancydyes.dyes;

import java.awt.*;

public class SolidDye implements FancyDye {
    public Color color;
    public SolidDye(Color color) {
        this.color = color;
    }

    @Override
    public String getArmorRenderType() {
        return "solid_armor";
    }

    @Override
    public String getItemRenderType() {
        return "solid_item";
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Type getType() {
        return Type.OVERLAY;
    }
}
