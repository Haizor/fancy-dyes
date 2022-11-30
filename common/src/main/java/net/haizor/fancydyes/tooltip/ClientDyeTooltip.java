package net.haizor.fancydyes.tooltip;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public record ClientDyeTooltip(DyeTooltip tooltip) implements ClientTooltipComponent {
    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getWidth(Font font) {
        return font.width(this.getText());
    }

    private Component getText() {
        return new TranslatableComponent("gui.tooltip.dye").append("    ").append(FancyDye.getItem(tooltip.dye()).getDescription()).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void renderText(Font font, int i, int j, Matrix4f matrix4f, MultiBufferSource.BufferSource bufferSource) {
        font.drawInBatch(this.getText(), (float)i, (float)j, -1, true, matrix4f, bufferSource, false, 0, 15728880);
    }

    @Override
    public void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int k) {
        PoseStack stack = new PoseStack();
        int l = font.width(new TranslatableComponent("gui.tooltip.dye").append(" "));
        stack.translate(-0.25f, 0.25f, 0f);
        stack.scale(0.6f, 0.6f, 1f);

        this.renderGuiItem(new ItemStack(FancyDye.getItem(tooltip.dye())), x + l, y, itemRenderer, stack);
    }

    protected void renderGuiItem(ItemStack itemStack, int i, int j, ItemRenderer renderer, PoseStack poseStack2) {
        BakedModel bakedModel = renderer.getModel(itemStack, null, null, 0);
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate((double)i, (double)j, (double)(100.0F + renderer.blitOffset));
        poseStack.translate(8.0D, 8.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl = !bakedModel.usesBlockLight();
        if (bl) {
            Lighting.setupForFlatItems();
        }

        renderer.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack2, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) {
            Lighting.setupFor3DItems();
        }

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

}
