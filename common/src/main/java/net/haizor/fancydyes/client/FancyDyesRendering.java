package net.haizor.fancydyes.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FancyDyesRendering extends RenderType {
    private static Map<FancyDye, Function<ResourceLocation, RenderType>> RENDER_TYPES = new Object2ObjectOpenHashMap<>();

    public static void init() {
        for (FancyDye dye : FancyDyes.DYES_REGISTRAR) {
            RENDER_TYPES.put(dye, createRenderTypeFor(dye));
        }
    }

    public static Function<ResourceLocation, RenderType> createRenderTypeFor(FancyDye dye) {
        return Util.memoize((tex) -> {
            CompositeState.CompositeStateBuilder builder = CompositeState.builder()
                .setTextureState(new TextureStateShard(tex, false, false))
                .setShaderState(Shaders.getShard("fancydyes/%s".formatted(dye.getShaderType())))
                .setTexturingState(new DyeRenderShard(dye))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setCullState(NO_CULL)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING);
            return create(dye.toId().toString(), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, builder.createCompositeState(true));
        });
    }

    public static Function<ResourceLocation, RenderType> getRenderType(FancyDye dye) {
        return RENDER_TYPES.get(dye);
    }

    public static void renderTrim(FancyDye dye, ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorTrim armorTrim, HumanoidModel<?> humanoidModel, boolean bl) {
        Vector3f color = dye.getColor();
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(bl ? armorTrim.innerTexture(armorMaterial) : armorTrim.outerTexture(armorMaterial));
        VertexConsumer vertexConsumer = textureAtlasSprite.wrap(multiBufferSource.getBuffer(FancyDyesRendering.getRenderType(dye).apply(Sheets.ARMOR_TRIMS_SHEET)));
        FancyDyeRenderSystem.setInverseModelViewMatrix(new Matrix4f(poseStack.last().pose()));
        FancyDyeRenderSystem.setDyeMatrix(new Matrix4f().rotate((float) Math.toRadians(180), 0, 0, 1));
        humanoidModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, color.x, color.y, color.z, 1.0f);
    }

    public static void renderDyedModel(FancyDye dye, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ArmorItem armorItem, Model humanoidModel, boolean bl, float f, float g, float h, ResourceLocation loc) {
        Vector3f color = dye.getColor();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(FancyDyesRendering.getRenderType(dye).apply(loc));
        FancyDyeRenderSystem.setInverseModelViewMatrix(new Matrix4f(poseStack.last().pose()));
        FancyDyeRenderSystem.setDyeMatrix(new Matrix4f().rotate((float) Math.toRadians(180), 0, 0, 1));
        humanoidModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, color.x * f, color.y * g, color.z * h, 1.0F);
    }

    public static void renderDyedItem(PoseStack poseStack, MultiBufferSource buffers, RenderType base, List<BakedQuad> list, ItemStack itemStack, int i, int j, ItemColors itemColors, ItemDisplayContext display, Optional<FancyDye> primary, Optional<FancyDye> secondary) {
        PoseStack.Pose pose = poseStack.last();
        FancyDyeRenderSystem.setInverseModelViewMatrix(new Matrix4f(pose.pose()));

        Matrix4f textureMatrix = new Matrix4f();

        if (itemStack.is(FancyDye.DIAGONAL_SCROLL)) {
            textureMatrix.rotate((float) Math.toRadians(45d), 0, 0, 1);
        }

        FancyDyeRenderSystem.setDyeMatrix(textureMatrix);

        RenderType primaryType = base;
        RenderType secondaryType = base;
        if (primary.isPresent()) {
            primaryType = FancyDyesRendering.getRenderType(primary.get()).apply(InventoryMenu.BLOCK_ATLAS);
        }

        final boolean isDye = itemStack.getItem() instanceof FancyDyeItem;

        if (isDye) {
            secondaryType = FancyDyesRendering.getRenderType(((FancyDyeItem)itemStack.getItem()).dye.get()).apply(InventoryMenu.BLOCK_ATLAS);
        } else if (secondary.isPresent()) {
            secondaryType = FancyDyesRendering.getRenderType(secondary.get()).apply(InventoryMenu.BLOCK_ATLAS);
        }

        boolean hasTrim = !secondaryType.equals(base) || ArmorTrim.getTrim(Minecraft.getInstance().level.registryAccess(), itemStack).isPresent();

        for (BakedQuad bakedQuad : list) {
            ResourceLocation loc = bakedQuad.getSprite().contents().name();
            boolean isSecondary = hasTrim && (isDye ? bakedQuad.getTintIndex() == 1 : loc.getPath().contains("trims"));

            Vector3f color = (isSecondary ? secondary : primary).map(FancyDye::getColor).orElse(new Vector3f(1));
            Vector3f tintColor = new Vector3f(1);

            VertexConsumer consumer = buffers.getBuffer(isSecondary ? secondaryType : primaryType);

            if (!itemStack.isEmpty() && bakedQuad.isTinted()) {
                int t = itemColors.getColor(itemStack, bakedQuad.getTintIndex());
                if (t != -1) {
                    float r = (float) (t >> 16 & 0xFF) / 255.0f;
                    float g = (float) (t >> 8 & 0xFF) / 255.0f;
                    float b = (float) (t & 0xFF) / 255.0f;
                    tintColor = new Vector3f(r, g, b);
                }
            }

            color = tintColor.mul(color);

            poseStack.pushPose();
            if (isSecondary) {
                if (display == ItemDisplayContext.GUI) {
                    poseStack.translate(0, 0, 0.5f);
                } else {
                    float z = display.equals(ItemDisplayContext.GROUND) ? 1.05f : 1.01f;

                    poseStack.translate(-0.0025f, -0.0025f, -((z - 1) / 2));
                    poseStack.scale(1.005f, 1.005f, z);
                }

                consumer.putBulkData(poseStack.last(), bakedQuad, color.x, color.y, color.z, i, j);
            } else {
                consumer.putBulkData(poseStack.last(), bakedQuad, color.x, color.y, color.z, i, j);
            }

            poseStack.popPose();
        }
    }

    public FancyDyesRendering(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    public static class DyeRenderShard extends TexturingStateShard {
        public DyeRenderShard(FancyDye dye) {
            super("dye_texturing", dye::setupRenderState, dye::resetRenderState);
        }
    }

    public static class Shaders {
        private static final Map<String, Info> MAP = new Object2ObjectOpenHashMap<>();

        public static final Info TEXTURE_MULTIPLY = register("fancydyes/texture_multiply", DefaultVertexFormat.NEW_ENTITY);
        public static final Info TEXTURE_ADDITIVE = register("fancydyes/texture_additive", DefaultVertexFormat.NEW_ENTITY);
        public static final Info COLOR_MULTIPLY = register("fancydyes/color_multiply", DefaultVertexFormat.NEW_ENTITY);
        public static final Info COLOR_ADDITIVE = register("fancydyes/color_additive", DefaultVertexFormat.NEW_ENTITY);
        public static final Info FLAME_MULTIPLY = register("fancydyes/flame_multiply", DefaultVertexFormat.NEW_ENTITY);
        public static final Info FLAME_ADDITIVE = register("fancydyes/flame_additive", DefaultVertexFormat.NEW_ENTITY);

        public static Info register(String path, VertexFormat format) {
            Info info = new Info(path, format);
            MAP.put(path, info);
            return info;
        }

        public static void onShaderReload(ResourceProvider provider, ClientReloadShadersEvent.ShadersSink sink) {
            for (Info info : MAP.values()) {
                try {
                    ShaderInstance shader = new ShaderInstance(provider, info.path, info.format);
                    sink.registerShader(shader, (inst) -> info.instance = inst);
                } catch (IOException e) {
                    e.printStackTrace();
                    FancyDyes.LOGGER.warn("Failed to load shader with path \"%s\"!".formatted(info.path));

                }
            }
        }

        public static ShaderStateShard getShard(String path) {
            return MAP.get(path).getShard();
        }

        public static class Info {
            private ShaderInstance instance;
            private ShaderStateShard shard;
            private VertexFormat format;

            public String path;

            public Info(String path, VertexFormat format) {
                this.path = path;
                this.format = format;
                this.shard = new ShaderStateShard(() -> instance);
            }

            public ShaderStateShard getShard() {
                return shard;
            }
        }
    }
}
