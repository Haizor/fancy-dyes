package net.haizor.fancydyes.dyes;

import java.awt.*;

public class OverlayDye implements FancyDye {
    String name;

    public OverlayDye(String name) {
        this.name = name;
    }

    @Override
    public String getArmorRenderType() {
        return name + "_armor";
    }

    @Override
    public String getItemRenderType() {
        return name + "_item";
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }
}
