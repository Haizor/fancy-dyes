package net.haizor.fancydyes;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class FancyDyeHelper {
    @ExpectPlatform
    public static boolean shouldDoBaseDyeRender(ItemStack stack) {
        throw new AssertionError();
    }
}
