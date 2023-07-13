package net.haizor.fancydyes.client;

import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class FancyDyedItemTooltip implements TooltipComponent {
    public FancyDye dye;
    public boolean secondary;

    public FancyDyedItemTooltip(FancyDye dye, boolean secondary) {
        this.dye = dye;
        this.secondary = secondary;
    }

    public static class Client implements ClientTooltipComponent {
        public FancyDye dye;
        public boolean secondary;

        public Client(FancyDyedItemTooltip context) {
            this.dye = context.dye;
            this.secondary = context.secondary;
        }

        public Client(FancyDye dye, boolean secondary) {
            this.dye = dye;
            this.secondary = secondary;
        }

        @Override
        public int getHeight() {
            return 10;
        }

        @Override
        public int getWidth(Font font) {
            ItemStack stack = new ItemStack(dye.getItem(secondary));

            return font.width(" ") + font.width(stack.getHoverName()) + 8;
        }

        @Override
        public void renderImage(Font font, int i, int j, GuiGraphics guiGraphics) {
            ItemStack stack = new ItemStack(dye.getItem(secondary));
            i += font.width(" ") + 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(i - 5, j - 1 - (secondary ? 1 : 0), 0);
            guiGraphics.pose().scale(0.65f, 0.65f, 0.65f);
            guiGraphics.renderItem(stack, 0, 0);
            guiGraphics.pose().popPose();
            guiGraphics.drawString(font, stack.getHoverName(), i + 6, j, 0xCCCCCC);
        }
    }
}
