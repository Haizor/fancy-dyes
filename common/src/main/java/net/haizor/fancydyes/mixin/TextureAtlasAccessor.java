package net.haizor.fancydyes.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
    @Invoker()
    public int invokeGetWidth();
    @Invoker()
    public int invokeGetHeight();
}
