package net.haizor.fancydyes.fabric.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.loader.api.FabricLoader;
import net.haizor.fancydyes.client.FancyDyedItemTooltip;
import net.haizor.fancydyes.client.FancyDyesClient;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.fabric.client.compat.gecko.GeckoLibCompat;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.List;
import java.util.Optional;

public class FancyDyesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FancyDyesRendering.PLATFORM = new FancyDyesXplatFabric();
        FancyDyesClient.init();
        if (FabricLoader.getInstance().isModLoaded("geckolib")) {
            GeoRenderEvent.Armor.CompileRenderLayers.EVENT.register(GeckoLibCompat::compileLayersEvent);
        }

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
