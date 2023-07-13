package net.haizor.fancydyes.fabric;

import net.fabricmc.api.ModInitializer;
import net.haizor.fancydyes.FancyDyes;

public class FancyDyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FancyDyes.init();
    }
}
