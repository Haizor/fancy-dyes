package net.haizor.fancydyes.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.mixin.TextureAtlasAccessor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30C;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class FancyDyesRendering extends RenderType {
    private static final Map<ResourceLocation, RenderType> ARMOR_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ARMOR_TRIM_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ITEM_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ITEM_DIAGONAL_TYPES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<ResourceLocation, RenderType> ITEM_TRIM_TYPES = new Object2ObjectLinkedOpenHashMap<>();

    private static final int BASE_MASK = 0b0000_0001;
    private static final int TRIM_MASK = 0b0000_0010;

    public static FancyDyesXplat PLATFORM;

    private static final ColorLogicStateShard WRITE_STENCIL_TRIM = new ColorLogicStateShard("fancydyes:stencil_write_trim", () -> {
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilFunc(GL11.GL_ALWAYS, TRIM_MASK, 0xFF);
    }, () -> {
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilMask(0xFF);
    });

    private static final ColorLogicStateShard WRITE_STENCIL = new ColorLogicStateShard("fancydyes:stencil_write", () -> {
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(false);

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilFunc(GL11.GL_ALWAYS, BASE_MASK, 0xff);
    }, () -> {
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilMask(0xFF);
    });

    private static final ColorLogicStateShard READ_STENCIL_TRIM = new ColorLogicStateShard("fancydyes:stencil_read_trim", () -> {
        GL11.glStencilFunc(GL11.GL_EQUAL, 0xFF, TRIM_MASK);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    }, () -> {
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilMask(0xFF);
    });

    private static final ColorLogicStateShard READ_STENCIL = new ColorLogicStateShard("fancydyes:stencil_read", () -> {
        GL11.glStencilFunc(GL11.GL_EQUAL, 0xFF, BASE_MASK);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    }, () -> {
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glStencilMask(0xFF);
    });

    private static final BiFunction<ResourceLocation, Boolean, RenderType> ARMOR_STENCIL_WRITER_TYPE = Util.memoize((loc, trim) -> {
        CompositeState compositeState = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(loc, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setColorLogicState(trim ? WRITE_STENCIL_TRIM : WRITE_STENCIL)
            .createCompositeState(true);

        return create("armor_stencil_writer%s".formatted(trim ? "_trim" : ""), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });

    public static final RenderType ITEM_STENCIL_TRIM_WRITER = create("item_stencil_trim_writer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(ITEM_ENTITY_TARGET)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setColorLogicState(WRITE_STENCIL_TRIM)
            .setWriteMaskState(new WriteMaskStateShard(false, false))
            .createCompositeState(true)
    );

    private static final RenderType ITEM_STENCIL_WRITER = create("item_stencil_writer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
        RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(ITEM_ENTITY_TARGET)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setColorLogicState(WRITE_STENCIL)
            .setWriteMaskState(new WriteMaskStateShard(false, false))
            .createCompositeState(true)
    );

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

    public static RenderType getArmorStencilWriter(ResourceLocation loc, boolean trim) {
        return ARMOR_STENCIL_WRITER_TYPE.apply(loc, trim);
    }

    public static RenderType getItemStencilWriter(boolean trim) {
        if (trim) {
            return ITEM_STENCIL_TRIM_WRITER;
        }
        return ITEM_STENCIL_WRITER;
    }

    public static void addDyeTypes(Map<RenderType, BufferBuilder> map) {
        if (!map.containsKey(ITEM_STENCIL_WRITER)) {
            map.put(ITEM_STENCIL_WRITER, new BufferBuilder(ITEM_STENCIL_WRITER.bufferSize()));
        }

        if (!map.containsKey(ITEM_STENCIL_TRIM_WRITER)) {
            map.put(ITEM_STENCIL_TRIM_WRITER, new BufferBuilder(ITEM_STENCIL_TRIM_WRITER.bufferSize()));
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
        CompositeState state = CompositeState.builder()
            .setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
            .setTextureState(new TextureStateShard(dye.getTexture(), true, false))
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setTransparencyState(dye.getBlendMode().equals(FancyDye.BlendMode.ADDITIVE) ? ADDITIVE : MULTIPLICATIVE)
            .setTexturingState(new TexturingStateShard(dye.toIdString() + "_armor_texturing", () -> {
                RenderSystem.setTextureMatrix(dye.getTextureMatrix().scale(5f, 1, 1));
            }, RenderSystem::resetTextureMatrix))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setColorLogicState(trim ? READ_STENCIL_TRIM : READ_STENCIL)
            .createCompositeState(false);

        RenderType type = create(dye.toIdString() + "_armor" + (trim ? "_trim" : ""), DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, state);
        PLATFORM.postProcessRenderType(type);
        return type;
    }
    public static RenderType createItemDye(FancyDye dye, boolean diagonal, boolean trim) {
        CompositeState state = CompositeState.builder()
            .setShaderState(RENDERTYPE_GLINT_DIRECT_SHADER)
            .setTextureState(new TextureStateShard(dye.getTexture(), true, false))
            .setWriteMaskState(COLOR_WRITE)
            .setCullState(NO_CULL)
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setTransparencyState(dye.getBlendMode() == FancyDye.BlendMode.ADDITIVE ? ADDITIVE : MULTIPLICATIVE)
            .setTexturingState(new TexturingStateShard(dye.toIdString() + "_item_texturing", () -> {
                //TODO: there's probably a better way to get the current texture atlas, but w/e
                TextureAtlasAccessor atlas = (TextureAtlasAccessor) Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);Matrix4f mat = dye.getTextureMatrix();

                float xScale = (atlas.invokeGetWidth() / 2048f) * 96;
                float yScale = (atlas.invokeGetHeight() / 2048f) * 96;

                if (diagonal) {
                    mat.rotateZ(-(float)Math.toRadians(45));
                }

                mat.scale(xScale, yScale, 1);

                RenderSystem.setTextureMatrix(mat);
            }, () -> RenderSystem.resetTextureMatrix()))
            .setLayeringState(NO_LAYERING)
            .setColorLogicState(trim ? READ_STENCIL_TRIM : READ_STENCIL)
            .createCompositeState(false);

        RenderType type = create(dye.toIdString() + "_item" + (diagonal ? "_diagonal" : ""), DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, state);
        PLATFORM.postProcessRenderType(type);
        return type;
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

    public static List<BakedQuad> getItemQuads(BakedModel model, Predicate<BakedQuad> predicate) {
        RandomSource source = RandomSource.create();
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            source.setSeed(42L);
            for (BakedQuad quad : model.getQuads(null, dir, source)) {
                if (!predicate.test(quad)) continue;
                quads.add(quad);
            }
        }

        for (BakedQuad quad : model.getQuads(null, null, source)) {
            if (!predicate.test(quad)) continue;
            quads.add(quad);
        }

        return quads;
    }

    public static void renderDefaultItemDyes(BakedModel model, ItemStack itemStack, int i, int j, PoseStack poseStack, MultiBufferSource source) {
        Optional<FancyDye> primaryDye = FancyDye.getDye(itemStack, false);
        Optional<FancyDye> secondaryDye = FancyDye.getDye(itemStack, true);

        if (primaryDye.isPresent() || secondaryDye.isPresent()) {
            PoseStack.Pose pose = poseStack.last();
            List<BakedQuad> primaryQuads = FancyDyesRendering.getItemQuads(model, quad -> quad.getTintIndex() != 1);
            List<BakedQuad> secondaryQuads = FancyDyesRendering.getItemQuads(model, quad -> quad.getTintIndex() == 1);
            VertexConsumer primaryStencil = source.getBuffer(FancyDyesRendering.getItemStencilWriter(false));
            VertexConsumer secondaryStencil = source.getBuffer(FancyDyesRendering.getItemStencilWriter(true));

            for (BakedQuad quad : primaryQuads) {
                primaryStencil.putBulkData(pose, quad, 1, 1, 1, i, j);
            }

            for (BakedQuad quad : secondaryQuads) {
                secondaryStencil.putBulkData(pose, quad, 1, 1, 1, i, j);
            }

            primaryDye.ifPresent(dye -> {
                //TODO: it was the texture atlas expanding that causes the dye stretching improperly.
                TextureAtlasSprite dyeSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(dye.getTexture());
                VertexConsumer dyeConsumer = source.getBuffer(FancyDyesRendering.getDyeItemType(dye, itemStack.is(FancyDye.DIAGONAL_SCROLL), false));
                for (BakedQuad quad : primaryQuads) {
                    dyeConsumer.putBulkData(pose, quad, 1, 1, 1, i, j);
                }
            });

            secondaryDye.ifPresent(dye -> {
                VertexConsumer dyeConsumer = source.getBuffer(FancyDyesRendering.getDyeItemType(dye, itemStack.is(FancyDye.DIAGONAL_SCROLL), true));
                for (BakedQuad quad : secondaryQuads) {
                    dyeConsumer.putBulkData(pose, quad, 1, 1, 1, i, j);
                }
            });
        }
    }
}
