package net.haizor.fancydyes.fabric;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.fabricmc.api.ClientModInitializer;
import net.haizor.fancydyes.client.FancyDyedItemTooltip;
import net.haizor.fancydyes.client.FancyDyesClient;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import java.util.List;
import java.util.Optional;

public class FancyDyesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FancyDyesClient.init();

        ClientTooltipEvent.RENDER_PRE.register(FancyDyesFabricClient::tooltipRenderPre);
    }

    public static EventResult tooltipRenderPre(GuiGraphics graphics, List<? extends ClientTooltipComponent> components, int x, int y) {
        ItemStack stack = ClientTooltipEvent.additionalContexts().getItem();
        if (stack == null || stack.isEmpty()) return EventResult.pass();

        List<ClientTooltipComponent> mutable = ((List<ClientTooltipComponent>) components);

        if (stack.getItem() instanceof FancyDyeItem) {
            return EventResult.pass();
        }

        Optional<FancyDye> primary = FancyDye.getDye(stack, false);
        Optional<FancyDye> secondary = FancyDye.getDye(stack, true);

        if (primary.isPresent() || secondary.isPresent()) {
            if (components.size() != 1) {
                mutable.add(ClientTooltipComponent.create(FormattedCharSequence.EMPTY));
            }

            mutable.add(ClientTooltipComponent.create(Component.translatable("gui.fancydyes.tooltip.dyes").append(": ").withStyle(ChatFormatting.GRAY).getVisualOrderText()));
            primary.ifPresent(dye -> mutable.add(new FancyDyedItemTooltip.Client(dye, false)));
            secondary.ifPresent(dye -> mutable.add(new FancyDyedItemTooltip.Client(dye, true)));
        }

        return EventResult.pass();
    }
}
