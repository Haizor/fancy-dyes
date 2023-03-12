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
import org.jetbrains.annotations.Nullable;

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

    public static final Type SOLID = createType(
        "solid",
        Shaders.OVERLAY_TEXTURE_LIT_SHARD,
        texture("textures/dye_scrolls/solid.png"),
        null,
        null
    );

    public static final Type SHIMMER = createType(
        "shimmer",
        Shaders.OVERLAY_TEXTURE_SHARD,
        texture("textures/dye_scrolls/shimmer.png"),
        ENTITY_GLINT_TEXTURING,
        GLINT_TEXTURING
    );

    public static final Type RAINBOW = createType(
        "rainbow",
        Shaders.OVERLAY_TEXTURE_SHARD,
        texture("textures/dye_scrolls/gradient.png"),
        verticalScroll(0.16f),
        verticalScroll(8f)
    );

    public static final Type AURORA = createType(
        "aurora",
        Shaders.OVERLAY_TEXTURE_SHARD,
        texture("textures/dye_scrolls/aurora.png"),
        angledScroll(0.6f, 80f),
        angledScroll(8f, 80f)
    );

    public static final Type FLAME = createType(
        "flame",
        Shaders.OVERLAY_TEXTURE_SHARD,
        texture("textures/dye_scrolls/flame.png"),
        verticalScroll(0.32f),
        verticalScroll(8f)
    );

    public static final Type GLOWSQUID = createType(
        "glowsquid",
        Shaders.OVERLAY_TEXTURE_SHARD,
        texture("textures/dye_scrolls/glowsquid.png"),
        verticalScroll(0.32f),
        verticalScroll(8f)
    );

    public static final Type TRANS = createType(
            "trans",
            Shaders.OVERLAY_TEXTURE_SHARD,
            texture("textures/dye_scrolls/trans.png"),
            verticalScroll(0.16f),
            verticalScroll(8f)
    );

    public static final Type ENBY = createType(
            "enby",
            Shaders.OVERLAY_TEXTURE_SHARD,
            texture("textures/dye_scrolls/enby.png"),
            verticalScroll(0.16f),
            verticalScroll(8f)
    );

    public static void init() {
        Shaders.init();
    }
    public static void addTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
        for (RenderType type : SOLID_TYPES) {
            map.put(type, new BufferBuilder(type.bufferSize()));
        }
    }

    public static Type createType(String name, ShaderStateShard shader, TextureStateShard texture, @Nullable TexturingStateShard armor, @Nullable TexturingStateShard item) {
        RenderTypeSupplier armorType = armor(name + "_armor", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(shader)
            .setTexturingState(armor != null ? armor : DEFAULT_TEXTURING)
            .setTextureState(texture)
        );

        RenderTypeSupplier itemType = item(name + "_item", DefaultVertexFormat.NEW_ENTITY, b -> b
            .setShaderState(shader)
            .setTexturingState(item != null ? item : DEFAULT_TEXTURING)
            .setTextureState(texture)
        );

        return new Type(armorType, itemType);
    }

    public static TextureStateShard texture(String path) {
        return new TextureStateShard(new ResourceLocation(FancyDyes.MOD_ID, path), true, false);
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

    public record Type(RenderTypeSupplier armor, RenderTypeSupplier item) {}

    public static class Shaders {
        public static ShaderInstance OVERLAY_TEXTURE;
        public static ShaderStateShard OVERLAY_TEXTURE_SHARD = new ShaderStateShard(() -> OVERLAY_TEXTURE);

        public static ShaderInstance OVERLAY_TEXTURE_LIT;
        public static ShaderStateShard OVERLAY_TEXTURE_LIT_SHARD = new ShaderStateShard(() -> OVERLAY_TEXTURE_LIT);

        public static ShaderInstance INVERT;
        public static ShaderStateShard INVERT_SHARD = new ShaderStateShard(() -> INVERT);


        public static void init() {
            ClientReloadShadersEvent.EVENT.register((manager, sink) -> {
                try {
                    sink.registerShader(new ShaderInstance(manager, "dyes/overlay_texture", DefaultVertexFormat.POSITION_COLOR_TEX), (s) -> OVERLAY_TEXTURE = s);
                    sink.registerShader(new ShaderInstance(manager, "dyes/overlay_texture_lit", DefaultVertexFormat.NEW_ENTITY), (s) -> OVERLAY_TEXTURE_LIT = s);
                    sink.registerShader(new ShaderInstance(manager, "dyes/invert", DefaultVertexFormat.NEW_ENTITY), (s) -> INVERT = s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
