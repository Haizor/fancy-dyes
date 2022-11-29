package net.haizor.fancydyes.forge;

import com.mojang.datafixers.util.Either;
import dev.architectury.platform.forge.EventBuses;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.FancyDyesClient;
import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.forge.data.FancyDyesDataForge;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.haizor.fancydyes.tooltip.ClientDyeTooltip;
import net.haizor.fancydyes.tooltip.ClientDyeTutorialTooltip;
import net.haizor.fancydyes.tooltip.DyeTooltip;
import net.haizor.fancydyes.tooltip.DyeTutorialTooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FancyDyes.MOD_ID)
public class FancyDyesForge {
    public FancyDyesForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(FancyDyes.MOD_ID, modEventBus);
        FancyDyes.init();
        modEventBus.register(ModBusEvents.class);
        modEventBus.addListener(FancyDyesDataForge::gatherData);
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);

        FancyDyesDataForge.GLM.register(modEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> FancyDyesClient::init);
    }


    public static class ModBusEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event){
            MinecraftForgeClient.registerTooltipComponentFactory(DyeTooltip.class, ClientDyeTooltip::new);
            MinecraftForgeClient.registerTooltipComponentFactory(DyeTutorialTooltip.class, ClientDyeTutorialTooltip::new);
        }
    }

    public static class ForgeEvents {
        @SubscribeEvent
        public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
            FancyDye dye = FancyDye.getDye(event.getItemStack());
            if (dye != null) {
                event.getTooltipElements().add(Either.right(new DyeTooltip(dye)));
            }

            if (event.getItemStack().getItem() instanceof FancyDyeItem) {
                if (Screen.hasShiftDown()) {
                    event.getTooltipElements().add(Either.right(new DyeTutorialTooltip(event.getItemStack())));
                }
            }
        }
    }

}
