package net.haizor.fancydyes.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.haizor.fancydyes.client.FancyDyeRenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class MixinVertexBuffer {
    @Inject(
        method = "_drawWithShader",
        at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V")
    )
    private void setupCustomUniforms(Matrix4f matrix4f, Matrix4f matrix4f2, ShaderInstance shaderInstance, CallbackInfo ci) {
        shaderInstance.safeGetUniform("IModelViewMat").set(FancyDyeRenderSystem.getInverseModelViewMatrix());
        shaderInstance.safeGetUniform("DyeMat").set(FancyDyeRenderSystem.getDyeMatrix());
    }
}
