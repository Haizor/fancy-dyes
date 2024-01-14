package net.haizor.fancydyes.forge;

import com.mojang.datafixers.util.Either;
import dev.architectury.platform.forge.EventBuses;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.client.FancyDyedItemTooltip;
import net.haizor.fancydyes.client.FancyDyesClient;
import net.haizor.fancydyes.client.FancyDyesRendering;
import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.forge.client.FancyDyesXplatForge;
import net.haizor.fancydyes.forge.client.compat.gecko.GeckoLibCompat;
import net.haizor.fancydyes.forge.mixin.RenderBuffersAccessor;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Optional;

@Mod(FancyDyes.MOD_ID)
public class FancyDyesForge {
    public FancyDyesForge() {
        EventBuses.registerModEventBus(FancyDyes.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FancyDyes.init();

        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
        FMLJavaModLoadingContext.get().getModEventBus().register(ModBusEvents.class);
    }

    private static class ModBusEvents {
        @SubscribeEvent
        public static void onClientInit(FMLClientSetupEvent event) {
            FancyDyesRendering.PLATFORM = new FancyDyesXplatForge();
            FancyDyesClient.init();
            if (ModList.get().isLoaded("geckolib")) {
                MinecraftForge.EVENT_BUS.register(GeckoLibCompat.class);
            }
            FancyDyesRendering.addDyeTypes(((RenderBuffersAccessor) Minecraft.getInstance().renderBuffers()).getFixedBuffers());
        }

        @SubscribeEvent
        public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(FancyDyedItemTooltip.class, FancyDyedItemTooltip.Client::new);
        }
    }

    private static class ForgeEvents {
        @SubscribeEvent
        public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
            ItemStack stack = event.getItemStack();

            if (stack.getItem() instanceof FancyDyeItem) return;

            Optional<FancyDye> primary = FancyDye.getDye(stack, false);
            Optional<FancyDye> secondary = FancyDye.getDye(stack, true);

            if (primary.isPresent() || secondary.isPresent()) {
                if (event.getTooltipElements().size() != 1) {
                    event.getTooltipElements().add(Either.left(FormattedText.EMPTY));
                }

                event.getTooltipElements().add(Either.left(Component.translatable("gui.fancydyes.tooltip.dyes").append(": ").withStyle(ChatFormatting.GRAY)));
                primary.ifPresent(dye -> event.getTooltipElements().add(Either.right(new FancyDyedItemTooltip(dye, false))));
                secondary.ifPresent(dye -> event.getTooltipElements().add(Either.right(new FancyDyedItemTooltip(dye, true))));
            }
        }
    }
}
