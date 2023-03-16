package net.haizor.fancydyes.forge.oculus.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.haizor.fancydyes.forge.oculus.DyeRenderType;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/client/renderer/RenderType$CompositeRenderType")
public abstract class MixinCompositeRenderType extends RenderType implements DyeRenderType {
    private MixinCompositeRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @Override
    public boolean isDye() {
        return this.name.contains("fancy_dye");
    }
}
