package net.haizor.fancydyes.mixin;

import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
class MixinItemStack {
    @Inject(method = "hasFoil", at = @At(value = "HEAD"), cancellable = true)
    void fancydyes$itemStackFoilCheck(CallbackInfoReturnable<Boolean> cir) {
        if (FancyDye.getDye((ItemStack)(Object)this, false).isPresent() || FancyDye.getDye((ItemStack)(Object)this, true).isPresent()) {
            cir.setReturnValue(false);
        }
    }
}
