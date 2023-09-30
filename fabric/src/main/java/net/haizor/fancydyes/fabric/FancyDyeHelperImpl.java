package net.haizor.fancydyes.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;

public class FancyDyeHelperImpl {
    public static boolean shouldDoBaseDyeRender(ItemStack stack) {
        if (FabricLoader.getInstance().isModLoaded("geckolib")) {
            if (stack.getItem() instanceof GeoItem) {
                return false;
            }
        }
        return true;
    }
}
