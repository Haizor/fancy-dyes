package net.haizor.fancydyes.fabric.mixin;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
    @Inject(
        method = "renderTooltip",
        at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$setItemContext(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci, ItemStack itemStack) {
        ClientTooltipEvent.additionalContexts().setItem(itemStack);
    }

    @Inject(
        method = "renderTooltip",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void fancydyes$freeItemContext(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci, ItemStack itemStack) {
        ClientTooltipEvent.additionalContexts().setItem(null);
    }
}
