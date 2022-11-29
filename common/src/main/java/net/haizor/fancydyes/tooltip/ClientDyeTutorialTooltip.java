package net.haizor.fancydyes.tooltip;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record ClientDyeTutorialTooltip(DyeTutorialTooltip tooltip) implements ClientTooltipComponent {
    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return Math.max(140, font.width(tooltip.itemStack().getHoverName()));
    }

    @Override
    public void renderImage(Font font, int i, int j, PoseStack poseStack, ItemRenderer itemRenderer, int k) {
        double progress = (Util.getMillis() % 4000L) / 1000D;

        int l = i + 8;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, new ResourceLocation("fancydyes", "textures/gui/dye_tutorial.png"));

        //Slots
        GuiComponent.blit(poseStack, l, j, 0, 0, 0, 18, 18, 64, 64);
        GuiComponent.blit(poseStack, i + this.getWidth(font) - 19, j, 0, 0, 0, 18, 18, 64, 64);

        ItemStack chestplate = new ItemStack(Items.IRON_CHESTPLATE);

        int phase = (int) Math.floor(progress);
        if (phase == 0) {
            //Cursor & Mouse Button
            GuiComponent.blit(poseStack, l + 14, j + 14, 0, 19, 0, 8, 11, 64, 64);
            GuiComponent.blit(poseStack, l + 20, j + 4, 0, 28, 0, 10, 12, 64, 64);

            renderGuiItem(this.tooltip().itemStack(), l + 1, j + 1, itemRenderer, poseStack);
            renderGuiItem(chestplate, i + this.getWidth(font) - 18, j + 1, itemRenderer, poseStack);
        } else if (phase == 1) {
            int x = l + 1 + (int)(((progress - 1d)) * (this.getWidth(font) - 28));
//            int x = (int)((i + 1) + (((progress - 1d) / 3d) * this.getWidth(font) - 18));
            renderGuiItem(chestplate, i + this.getWidth(font) - 18, j + 1, itemRenderer, poseStack);
            poseStack.pushPose();
            poseStack.translate(0f, 0f, 1f);
            renderGuiItem(this.tooltip().itemStack(), x, j + 1, itemRenderer, poseStack);
            poseStack.popPose();
        } else if (phase == 2) {
            GuiComponent.blit(poseStack, i + this.getWidth(font) - 32, j + 4, 0, 39, 0, 10, 12, 64, 64);

            renderGuiItem(chestplate, i + this.getWidth(font) - 18, j + 1, itemRenderer, poseStack);
            poseStack.pushPose();
            poseStack.translate(0f, 0f, 1f);
            renderGuiItem(this.tooltip().itemStack(), i + this.getWidth(font) - 18, j + 1, itemRenderer, poseStack);
            poseStack.popPose();
        } else {
            String name = FancyDyes.DYES.inverse().get(((FancyDyeItem)this.tooltip.itemStack().getItem()).dye);
            chestplate.getOrCreateTag().putString("dye", name);
            renderGuiItem(chestplate, i + this.getWidth(font) - 18, j + 1, itemRenderer, poseStack);
        }


    }

    private void renderGuiItem(ItemStack itemStack, int i, int j, ItemRenderer renderer, PoseStack poseStack2) {
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
