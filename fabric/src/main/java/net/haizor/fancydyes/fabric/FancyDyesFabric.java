package net.haizor.fancydyes.fabric;

import net.haizor.fancydyes.FancyDyes;
import net.fabricmc.api.ModInitializer;

import java.util.function.Supplier;

public class FancyDyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FancyDyes.init();
    }
}
