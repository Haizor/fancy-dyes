package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderTarget.class)
class MixinRenderTarget {
    @ModifyArg(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", ordinal = 1), index = 1)
    private int fancyDyes$setDepthStencilBuffer(int i) {
        return GL30.GL_DEPTH_STENCIL_ATTACHMENT;
    }

    @ModifyArg(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0), index = 2)
    private int fancyDyes$stencilInternalFormat(int i) {
        return GL30.GL_DEPTH32F_STENCIL8;
    }

    @ModifyArg(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0), index = 6)
    private int fancyDyes$stencilFormat(int i) {
        return GL30.GL_DEPTH_STENCIL;
    }

    @ModifyArg(method = "createBuffers", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0), index = 7)
    private int fancyDyes$stencilType(int i) {
        return GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
    }

    @Redirect(method = "clear", at = @At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;_clear(IZ)V"))
    private void fancyDyes$clearStencil(int mask, boolean getError) {
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilMask(0xFF);
        GL11C.glClearStencil(0);
        GlStateManager._clear(mask | GL11C.GL_STENCIL_BUFFER_BIT, getError);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    }
}
