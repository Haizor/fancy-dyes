package net.haizor.fancydyes.dyes;

import java.awt.*;

public class StandardDyeColors {
    public static final DyeColor[] DYE_COLORS = new DyeColor[] {
        new DyeColor("white", Color.WHITE),
        new DyeColor("red", Color.RED),
        new DyeColor("orange", Color.ORANGE),
        new DyeColor("yellow", Color.YELLOW),
        new DyeColor("green", Color.GREEN),
        new DyeColor("cyan", Color.CYAN),
        new DyeColor("blue", Color.BLUE),
        new DyeColor("purple", new Color(0x9200FF)),
        new DyeColor("pink", new Color(0xF675FF))
    };

    public record DyeColor(String string, Color color) {}
}
