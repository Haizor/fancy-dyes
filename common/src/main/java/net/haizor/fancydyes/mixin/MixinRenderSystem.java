package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.haizor.fancydyes.client.FancyDyeRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {
    @Inject(
        method = "setupDefaultState",
        at = @At("TAIL"),
        remap = false
    )
    private static void fancydyes$setupDefaultState(int i, int j, int k, int l, CallbackInfo ci) {
        FancyDyeRenderSystem.setupDefaultState(i, j, k, l);
    }
}
