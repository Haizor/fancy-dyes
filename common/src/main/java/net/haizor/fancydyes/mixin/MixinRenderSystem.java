package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {
    @ModifyVariable(method = "clear", at = @At("HEAD"), ordinal = 0, argsOnly = true, remap = false)
    private static int clear(int i) {
        GL11.glStencilMask(0xFF);
        GL11.glClearStencil(0);
        return i | GL11.GL_STENCIL_BUFFER_BIT;
    }
}
