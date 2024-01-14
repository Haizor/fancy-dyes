package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
class MixinRenderBuffers {
    @Inject(method = "put", at = @At("TAIL"))
    private static void fancydyes$putRenderTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> object2ObjectLinkedOpenHashMap, RenderType renderType, CallbackInfo ci) {
        FancyDyesRendering.addDyeTypes(object2ObjectLinkedOpenHashMap);
    }
}
