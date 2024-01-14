package net.haizor.fancydyes.dye;

import net.haizor.fancydyes.FancyDyeUtil;
import net.haizor.fancydyes.FancyDyes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;

import java.util.Locale;

public enum FancyDyeColor {
    RED(0xFF0000, Items.RED_DYE),
    ORANGE(0xFF6A00, Items.ORANGE_DYE),
    YELLOW(0xFFFF00, Items.YELLOW_DYE),
    GREEN(0x008800, Items.GREEN_DYE),
    LIME(0x00FF00, Items.LIME_DYE),
    LIGHT_BLUE(0x5588FF, Items.LIGHT_BLUE_DYE),
    CYAN(0x00BBFF, Items.CYAN_DYE),
    BLUE(0x0000FF, Items.BLUE_DYE),
    PURPLE(0xAA00FF, Items.PURPLE_DYE),
    PINK(0xFF77FF, Items.PINK_DYE),
    MAGENTA(0xFF00FF, Items.MAGENTA_DYE);

    private final int color;
    private final Item vanillaDye;

    FancyDyeColor(int color, Item vanillaDye) {
        this.color = color;
        this.vanillaDye = vanillaDye;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.US);
    }

    public Vector3f asVector3f() {
        return FancyDyeUtil.colorFromInt(color);
    }
    public Item getVanillaDye() {
        return vanillaDye;
    }

    public static void registerDyes() {
        for (FancyDyeColor color : FancyDyeColor.values()) {
            FancyDyes.registerDye(color.toString(), () -> new ColorDye(color));
            FancyDyes.registerDye("bright_%s".formatted(color.toString()), () -> new BrightColorDye(color));
        }
    }
}
