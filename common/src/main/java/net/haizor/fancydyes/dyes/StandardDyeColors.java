package net.haizor.fancydyes.dyes;

import net.minecraft.world.item.DyeColor;

import java.awt.*;

public class StandardDyeColors {
    public static final FancyDyeColor[] DYE_COLORS = new FancyDyeColor[] {
        new FancyDyeColor("red", Color.RED),
        new FancyDyeColor("orange", Color.ORANGE),
        new FancyDyeColor("yellow", Color.YELLOW),
        new FancyDyeColor("green", new Color(0x00AA00)),
        new FancyDyeColor("lime", new Color(0x00FF00)),
        new FancyDyeColor("cyan", new Color(0x3C8DB0)),
        new FancyDyeColor("light_blue", new Color(0x7098FF)),
        new FancyDyeColor("blue", Color.BLUE),
        new FancyDyeColor("purple", new Color(0x7300FF)),
        new FancyDyeColor("pink", new Color(0xF675FF)),
        new FancyDyeColor("magenta", new Color(0xFF009A)),
        new FancyDyeColor("brown", new Color(0x703A1B)),
        new FancyDyeColor("white", Color.WHITE),
        new FancyDyeColor("light_gray", new Color(0xB7B7B7)),
        new FancyDyeColor("gray", new Color(0x595959)),
        new FancyDyeColor("black", Color.BLACK)
    };

    public record FancyDyeColor(String string, Color color) {}
}
