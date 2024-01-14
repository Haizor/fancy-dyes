package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.pipeline.MainTarget;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MainTarget.class)
public class MixinMainTarget {
    @ModifyArg(method = "createFrameBuffer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", ordinal = 1), index = 1)
    private int fancyDyes$setDepthStencilBuffer(int i) {
            return GL30.GL_DEPTH_STENCIL_ATTACHMENT;
    }

    @ModifyArg(method = "allocateDepthAttachment", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"), index = 2)
    private int fancyDyes$stencilInternalFormat(int i) {
        return GL30.GL_DEPTH32F_STENCIL8;
    }

    @ModifyArg(method = "allocateDepthAttachment", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"), index = 6)
    private int fancyDyes$stencilFormat(int i) {
        return GL30.GL_DEPTH_STENCIL;
    }

    @ModifyArg(method = "allocateDepthAttachment", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"), index = 7)
    private int fancyDyes$stencilType(int i) {
        return GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
    }
}
