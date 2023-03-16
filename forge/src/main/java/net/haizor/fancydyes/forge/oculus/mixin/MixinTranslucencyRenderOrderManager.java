package net.haizor.fancydyes.forge.oculus.mixin;

import net.coderbot.batchedentityrendering.impl.TransparencyType;
import net.coderbot.batchedentityrendering.impl.ordering.GraphTranslucencyRenderOrderManager;
import net.coderbot.batchedentityrendering.impl.ordering.RenderOrderManager;
import net.haizor.fancydyes.forge.oculus.DyeRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GraphTranslucencyRenderOrderManager.class)
public abstract class MixinTranslucencyRenderOrderManager implements RenderOrderManager {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/coderbot/batchedentityrendering/impl/BlendingStateHolder;getTransparencyType()Lnet/coderbot/batchedentityrendering/impl/TransparencyType;", shift = At.Shift.BEFORE, remap = false), method = "getTransparencyType", remap = false, cancellable = true)
    private static void getTransparencyType(RenderType type, CallbackInfoReturnable<TransparencyType> cir) {
        if (type instanceof DyeRenderType state && state.isDye()) {
            cir.setReturnValue(TransparencyType.DECAL);
        }
    }
}
