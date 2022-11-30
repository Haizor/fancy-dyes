package net.haizor.fancydyes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState.CompositeStateBuilder;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.minecraft.client.renderer.RenderType.*;

public class DyeRenderTypes {
    public static final Map<String, RenderTypeSupplier> TYPES = new HashMap<>();
    private static final List<RenderType> SOLID_TYPES = new ArrayList<>();

    public static final RenderTypeSupplier SOLID_ARMOR = armor("solid_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_LIT_SHARD)
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/solid.png"), true, false))
    );

    public static final RenderTypeSupplier SOLID_ITEM = item("solid_item", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_LIT_SHARD)
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/solid.png"), true, false))
    );

    public static final RenderTypeSupplier SHIMMER_ARMOR = armor("shimmer_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(ENTITY_GLINT_TEXTURING)
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/shimmer.png"), true, false))
    );

    public static final RenderTypeSupplier SHIMMER_ITEM = item("shimmer_item", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(GLINT_TEXTURING)
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/shimmer.png"), true, false))
    );

    public static final RenderTypeSupplier INVERT_ARMOR = baseArmor("invert_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.INVERT_SHARD)
    );

    public static final RenderTypeSupplier RAINBOW_ARMOR = armor("rainbow_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(verticalScroll(0.16f))
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/gradient.png"), true, false))
    );

    public static final RenderTypeSupplier RAINBOW_ITEM = item("rainbow_item", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(verticalScroll(8f))
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/gradient.png"), true, false))
    );

    public static final RenderTypeSupplier AURORA_ARMOR = armor("aurora_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(angledScroll(0.6f, 80f))
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/aurora.png"), true, false))
    );

    public static final RenderTypeSupplier AURORA_ITEM = item("aurora_item", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(angledScroll(8f, 80f))
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/aurora.png"), true, false))
    );

    public static final RenderTypeSupplier FLAME_ARMOR = armor("flame_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(verticalScroll(0.32f))
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/flame.png"), true, false))
    );

    public static final RenderTypeSupplier FLAME_ITEM = item("flame_item", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(Shaders.OVERLAY_TEXTURE_SHARD)
            .setTexturingState(verticalScroll(8f))
            .setTextureState(new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, "textures/dye_scrolls/flame.png"), true, false))
    );

    public static void init() {
        Shaders.init();
    }
    public static void addTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
        for (RenderType type : SOLID_TYPES) {
            map.put(type, new BufferBuilder(type.bufferSize()));
        }
    }

    public static RenderTypeSupplier armor(String id, VertexFormat format, Consumer<CompositeStateBuilder> f) {
        CompositeStateBuilder builder = RenderType.CompositeState.builder()
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setLightmapState(LIGHTMAP)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING);

        f.accept(builder);

        CompositeRenderType type = create(id, format, VertexFormat.Mode.QUADS, 256, builder.createCompositeState(false));

        return register(id, type);
    }

    public static RenderTypeSupplier baseArmor(String id, VertexFormat format, Consumer<CompositeStateBuilder> f) {
        return register(id, (loc) -> {
            CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setTransparencyState(NO_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING);

            f.accept(builder);

            builder.setTextureState(new TextureStateShard(loc, false, false));

            return create(id, format, VertexFormat.Mode.QUADS, 256, builder.createCompositeState(true));
        });
    }

    public static RenderTypeSupplier item(String id, VertexFormat format, Consumer<CompositeStateBuilder> f)  {
        CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setWriteMaskState(COLOR_WRITE)
                .setCullState(NO_CULL)
                .setDepthTestState(EQUAL_DEPTH_TEST)
                .setLightmapState(LIGHTMAP)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY);

        f.accept(builder);

        return register(id, create(id, format, VertexFormat.Mode.QUADS, 256, builder.createCompositeState(false)));
    }

    public static TexturingStateShard verticalScroll(float f) {
        return new TexturingStateShard("vertical_scroll", () -> {
            long l = Util.getMillis() * 8L;
            float h = (float)(l % 30000L) / 30000.0F;
            Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0.0F, h, 0.0F);
            matrix4f.multiply(Matrix4f.createScaleMatrix(f, f, f));
            RenderSystem.setTextureMatrix(matrix4f);
        }, RenderSystem::resetTextureMatrix);
    }

    public static TexturingStateShard horizontalScroll(float f) {
        return new TexturingStateShard("horizontal_scroll", () -> {
            long l = Util.getMillis() * 8L;
            float h = (float)(l % 30000L) / 30000.0F;
            Matrix4f matrix4f = Matrix4f.createTranslateMatrix(h, 0.0f, 0.0F);
            matrix4f.multiply(Matrix4f.createScaleMatrix(f, f, f));
            RenderSystem.setTextureMatrix(matrix4f);
        }, RenderSystem::resetTextureMatrix);
    }

    public static TexturingStateShard angledScroll(float f, float angle) {
        return new TexturingStateShard("horizontal_scroll", () -> {
            long l = Util.getMillis() * 8L;
            float h = (float)(l % 30000L) / 30000.0F;
            Matrix4f matrix4f = Matrix4f.createTranslateMatrix(h, 0.0f, 0.0F);
            matrix4f.multiply(Vector3f.ZP.rotationDegrees(angle));
            matrix4f.multiply(Matrix4f.createScaleMatrix(f, f, f));
            RenderSystem.setTextureMatrix(matrix4f);
        }, RenderSystem::resetTextureMatrix);
    }

    public static RenderTypeSupplier register(String id, RenderType type) {
        SOLID_TYPES.add(type);
        return register(id, (loc) -> type);
    }

    public static RenderTypeSupplier register(String id, RenderTypeSupplier supplier) {
        TYPES.put(id, supplier);
        return supplier;
    }

    public static RenderType get(String id, ResourceLocation texture) {
        return TYPES.get(id).supply(texture);
    }


    @FunctionalInterface
    public interface RenderTypeSupplier {
        RenderType supply(ResourceLocation location);
    }

    public static class Shaders {
        public static ShaderInstance OVERLAY_TEXTURE;
        public static ShaderStateShard OVERLAY_TEXTURE_SHARD = new ShaderStateShard(() -> OVERLAY_TEXTURE);

        public static ShaderInstance INVERT;
        public static ShaderStateShard INVERT_SHARD = new ShaderStateShard(() -> INVERT);

        public static ShaderInstance OVERLAY_TEXTURE_LIT;
        public static ShaderStateShard OVERLAY_TEXTURE_LIT_SHARD = new ShaderStateShard(() -> OVERLAY_TEXTURE_LIT);

        public static void init() {
            ClientReloadShadersEvent.EVENT.register((manager, sink) -> {
                try {
                    sink.registerShader(new ShaderInstance(manager, "dyes/overlay_texture", DefaultVertexFormat.POSITION_COLOR_TEX), (s) -> OVERLAY_TEXTURE = s);
                    sink.registerShader(new ShaderInstance(manager, "dyes/invert", DefaultVertexFormat.NEW_ENTITY), (s) -> INVERT = s);
                    sink.registerShader(new ShaderInstance(manager, "dyes/overlay_texture_lit", DefaultVertexFormat.NEW_ENTITY), (s) -> OVERLAY_TEXTURE_LIT = s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
