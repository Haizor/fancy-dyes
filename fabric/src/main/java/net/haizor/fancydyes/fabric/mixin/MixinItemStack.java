package net.haizor.fancydyes.fabric.mixin;

import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class MixinItemStack {
    @Inject(method = "hasFoil", cancellable = true, at = @At("HEAD"))
    public void hasFoil(CallbackInfoReturnable<Boolean> cir) {
        if (FancyDye.getDye((ItemStack) (Object) this) != null) {
            cir.setReturnValue(false);
        }
    }
}
