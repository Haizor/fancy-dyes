package net.haizor.fancydyes.fabric;

import net.haizor.fancydyes.FancyDyes;
import net.fabricmc.api.ModInitializer;

public class FancyDyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FancyDyes.init();
    }
}
