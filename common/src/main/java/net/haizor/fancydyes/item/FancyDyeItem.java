package net.haizor.fancydyes.item;

import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class FancyDyeItem extends Item {
    public final Supplier<FancyDye> dye;
    public final boolean secondary;

    public FancyDyeItem(Supplier<FancyDye> dye, boolean secondary) {
        super(new Properties().arch$tab(FancyDyes.TAB));
        this.dye = dye;
        this.secondary = secondary;
    }
}
