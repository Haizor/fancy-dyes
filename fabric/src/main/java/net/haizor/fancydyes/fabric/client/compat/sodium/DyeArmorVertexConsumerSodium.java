package net.haizor.fancydyes.fabric.client.compat.sodium;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.vertex.attributes.CommonVertexAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.TextureAttribute;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.VertexFormatDescription;
import net.haizor.fancydyes.client.DyeArmorVertexConsumer;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class DyeArmorVertexConsumerSodium extends DyeArmorVertexConsumer implements VertexBufferWriter {
    public DyeArmorVertexConsumerSodium(VertexConsumer base, PoseStack pose) {
        super(base, pose);
    }

    @Override
    public void push(MemoryStack stack, long ptr, int count, VertexFormatDescription format) {
        transform(ptr, count, format);

        VertexBufferWriter.of(this.base)
                .push(stack, ptr, count, format);
    }

    private void transform(long ptr, int count, VertexFormatDescription format) {
        long stride = format.stride();
        long posOffset = format.getElementOffset(CommonVertexAttribute.POSITION);
        long textureOffset = format.getElementOffset(CommonVertexAttribute.TEXTURE);

        for (int i = 0; i < count; i++) {
            float x = PositionAttribute.getX(ptr + posOffset);
            float y = PositionAttribute.getY(ptr + posOffset);
            float z = PositionAttribute.getZ(ptr + posOffset);

            Vector3f pos = matrix.transformPosition(new Vector3f(x, y, z));

            float u = TextureAttribute.getU(ptr + textureOffset);

            TextureAttribute.put(ptr + textureOffset, u, pos.y);

            ptr += stride;
        }
    }
}
