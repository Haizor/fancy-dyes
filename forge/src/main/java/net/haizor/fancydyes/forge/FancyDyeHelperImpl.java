package net.haizor.fancydyes.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import software.bernie.geckolib.animatable.GeoItem;

public class FancyDyeHelperImpl {
    public static boolean shouldDoBaseDyeRender(ItemStack stack) {
        if (ModList.get().isLoaded("geckolib")) {
            if (stack.getItem() instanceof GeoItem) {
                return false;
            }
        }
        return true;
    }
}
