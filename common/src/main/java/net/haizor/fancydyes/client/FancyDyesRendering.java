package net.haizor.fancydyes.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.function.Function;

public class FancyDyesRendering extends RenderType {
    private static final LayeringStateShard ARMOR_TRIM_LAYERING = new RenderStateShard.LayeringStateShard("armor_trim_layering", () -> {
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
    });

    private static final LayeringStateShard ITEM_TRIM_LAYERING = new RenderStateShard.LayeringStateShard("armor_trim_layering", () -> {
        RenderSystem.polygonOffset(-1.0f, -0.5f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
    });

    private static final Map<ResourceLocation, RenderType> ARMOR_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ARMOR_TRIM_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ITEM_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ITEM_DIAGONAL_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ITEM_TRIM_TYPES = new Object2ObjectLinkedOpenHashMap<>();

    private static final RenderType ARMOR_TRIM_OFFSET = createArmorTrimType();
    private static final Function<ResourceLocation, RenderType> ITEM_TRIM_OFFSET = Util.memoize((loc) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
            .setTextureState(new TextureStateShard(loc, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(ITEM_TRIM_LAYERING)
            .createCompositeState(true);
        return create("item_trim_offset", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, compositeState);
    });

    FancyDyesRendering(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    public static void init() {
        for (FancyDye dye : FancyDyes.DYES_REGISTRAR) {
            ARMOR_TYPES.put(dye.toId(), createArmorDye(dye, false));
            ARMOR_TRIM_TYPES.put(dye.toId(), createArmorDye(dye, true));
            ITEM_TYPES.put(dye.toId(), createItemDye(dye, false, false));
            ITEM_DIAGONAL_TYPES.put(dye.toId(), createItemDye(dye, true, false));
            ITEM_TRIM_TYPES.put(dye.toId(), createItemDye(dye, false, true));
        }
    }

    public static RenderType armorTrimOffset() {
        return ARMOR_TRIM_OFFSET;
    }

    public static RenderType itemTrimOffset(ResourceLocation loc) {
        return ITEM_TRIM_OFFSET.apply(loc);
    }

    private static RenderType createArmorTrimType() {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
            .setTextureState(new TextureStateShard(Sheets.ARMOR_TRIMS_SHEET, false, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(ARMOR_TRIM_LAYERING)
            .createCompositeState(true);
        return create("armor_trim", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    }

    public static void addDyeTypes(Map<RenderType, BufferBuilder> map) {
        RenderType trimOffset = ITEM_TRIM_OFFSET.apply(TextureAtlas.LOCATION_BLOCKS);
        if (!map.containsKey(trimOffset)) {
            map.put(trimOffset, new BufferBuilder(trimOffset.bufferSize()));
        }

        for (RenderType type : ARMOR_TYPES.values()) {
            if (!map.containsKey(type)) {
                map.put(type, new BufferBuilder(type.bufferSize()));
            }
        }

        for (RenderType type : ARMOR_TRIM_TYPES.values()) {
            if (!map.containsKey(type)) {
                map.put(type, new BufferBuilder(type.bufferSize()));
            }
        }

        for (RenderType type : ITEM_TYPES.values()) {
            if (!map.containsKey(type)) {
                map.put(type, new BufferBuilder(type.bufferSize()));
            }
        }

        for (RenderType type : ITEM_DIAGONAL_TYPES.values()) {
            if (!map.containsKey(type)) {
                map.put(type, new BufferBuilder(type.bufferSize()));
            }
        }

        for (RenderType type : ITEM_TRIM_TYPES.values()) {
            if (!map.containsKey(type)) {
                map.put(type, new BufferBuilder(type.bufferSize()));
            }
        }
    }

    public static RenderType getDyeArmorType(FancyDye dye, boolean trim) {
        return (trim ? ARMOR_TRIM_TYPES : ARMOR_TYPES).get(dye.toId());
    }
    public static RenderType getDyeItemType(FancyDye dye, boolean diagonal, boolean trim) {
        if (trim) {
            return ITEM_TRIM_TYPES.get(dye.toId());
        }
        return (diagonal ? ITEM_DIAGONAL_TYPES : ITEM_TYPES).get(dye.toId());
    }

    public static RenderType createArmorDye(FancyDye dye, boolean trim) {
        ShaderStateShard shader = dye.getType().equals(FancyDye.Type.COLORED_TEXTURE) ? RenderStateShard.POSITION_COLOR_TEX_SHADER : RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER;
        VertexFormat format = dye.getType().equals(FancyDye.Type.COLORED_TEXTURE) ? DefaultVertexFormat.POSITION_COLOR_TEX : DefaultVertexFormat.POSITION_TEX;

        CompositeState state = CompositeState.builder()
            .setShaderState(shader)
            .setTextureState(new TextureStateShard(dye.getTexture(), true, false))
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setTransparencyState(dye.getBlendMode().equals(FancyDye.BlendMode.ADDITIVE) ? ADDITIVE : MULTIPLICATIVE)
            .setTexturingState(new TexturingStateShard(dye.toIdString() + "_armor_texturing", () -> {
                RenderSystem.setTextureMatrix(dye.getTextureMatrix().scale(4f, 1, 1));
            }, RenderSystem::resetTextureMatrix))
            .setLayeringState(trim ? ARMOR_TRIM_LAYERING : VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false);

        return CompositeRenderType.create(dye.toIdString() + "_armor" + (trim ? "_trim" : ""), format, VertexFormat.Mode.QUADS, 256, state);
    }
    public static RenderType createItemDye(FancyDye dye, boolean diagonal, boolean trim) {
        ShaderStateShard shader = dye.getType().equals(FancyDye.Type.COLORED_TEXTURE) ? RenderStateShard.POSITION_COLOR_TEX_SHADER : RenderStateShard.RENDERTYPE_GLINT_SHADER;
        VertexFormat format = dye.getType().equals(FancyDye.Type.COLORED_TEXTURE) ? DefaultVertexFormat.POSITION_COLOR_TEX : DefaultVertexFormat.POSITION_TEX;

        CompositeState state = CompositeState.builder()
            .setShaderState(shader)
            .setTextureState(new TextureStateShard(dye.getTexture(), true, false))
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setTransparencyState(dye.getBlendMode().equals(FancyDye.BlendMode.ADDITIVE) ? ADDITIVE : MULTIPLICATIVE)
            .setTexturingState(new TexturingStateShard(dye.toIdString() + "_armor_texturing", () -> {
                Matrix4f mat = dye.getTextureMatrix();
                if (diagonal) {
                    mat.rotate((float)Math.toRadians(45), 0, 0, 1);
                }
                mat.scale(48f, 48f, 1);
                RenderSystem.setTextureMatrix(mat);
            }, RenderSystem::resetTextureMatrix))
            .setLayeringState(trim ? ITEM_TRIM_LAYERING : NO_LAYERING)
            .createCompositeState(false);

        return create(dye.toIdString() + "_item" + (diagonal ? "_diagonal" : ""), format, VertexFormat.Mode.QUADS, 256, true, false, state);
    }

    public static final RenderStateShard.TransparencyStateShard ADDITIVE = new RenderStateShard.TransparencyStateShard("dye_additive", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static final RenderStateShard.TransparencyStateShard MULTIPLICATIVE = new RenderStateShard.TransparencyStateShard("dye_multiplicative", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
}
