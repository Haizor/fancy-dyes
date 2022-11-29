package net.haizor.fancydyes.fabric;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.haizor.fancydyes.FancyDyesClient;
import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.haizor.fancydyes.tooltip.ClientDyeTooltip;
import net.haizor.fancydyes.tooltip.ClientDyeTutorialTooltip;
import net.haizor.fancydyes.tooltip.DyeTooltip;
import net.haizor.fancydyes.tooltip.DyeTutorialTooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FancyDyesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FancyDyesClient.init();

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof DyeTooltip dye) return new ClientDyeTooltip(dye);
            if (data instanceof DyeTutorialTooltip dye) return new ClientDyeTutorialTooltip(dye);

            return null;
        });

        ClientTooltipEvent.RENDER_PRE.register((matrices, texts, x, y) -> {
            ItemStack stack = ClientTooltipEvent.additionalContexts().getItem();
            if (stack == null || stack.isEmpty()) return EventResult.pass();
            FancyDye dye = FancyDye.getDye(stack);
            List<ClientTooltipComponent> fixed = (List<ClientTooltipComponent>) texts;

            if (stack.getItem() instanceof FancyDyeItem && Screen.hasShiftDown()) {
                fixed.add(new ClientDyeTutorialTooltip(new DyeTutorialTooltip(stack)));
            }
            if (dye != null) {
                fixed.add(new ClientDyeTooltip(new DyeTooltip(dye)));
            }
            return EventResult.pass();
        });
    }
}
