package net.haizor.fancydyes;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.haizor.fancydyes.crafting.SmithingDyeRecipe;
import net.haizor.fancydyes.crafting.SmithingDyeRemoveRecipe;
import net.haizor.fancydyes.dye.*;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;


public class FancyDyes {
    public static final String MOD_ID = "fancydyes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final ResourceLocation DYES_REGISTRAR_LOCATION = new ResourceLocation(MOD_ID, "dyes");

    public static final Registrar<FancyDye> DYES_REGISTRAR = RegistrarManager.get(MOD_ID).builder(DYES_REGISTRAR_LOCATION, new FancyDye[0]).build();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(MOD_ID, Registries.RECIPE_SERIALIZER);
    public static final DeferredRegister<FancyDye> DYES = DeferredRegister.create(MOD_ID, ResourceKey.createRegistryKey(DYES_REGISTRAR_LOCATION));

    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("dyes", () -> CreativeTabRegistry.create(Component.translatable("itemgroup.%s.dyes".formatted(MOD_ID)), FancyDyes::getCreativeTabIcon));

    public static final RegistrySupplier<Item> DYE_BOTTLE = ITEMS.register("dye_bottle", () -> new Item(new Item.Properties().arch$tab(TAB)));
    public static final RegistrySupplier<Item> SMALL_DYE_BOTTLE = ITEMS.register("small_dye_bottle", () -> new Item(new Item.Properties().arch$tab(TAB)));

    public static final RegistrySupplier<FancyDye> RAINBOW = registerDye("rainbow", () -> new TexturedDye(FancyDyes.id("textures/dye/rainbow.png")).withTexturing(TexturedDye.Texturing.VERTICAL_SCROLL));
    public static final RegistrySupplier<FancyDye> BRIGHT_RAINBOW = registerDye("bright_rainbow", () -> new BrightTexturedDye(FancyDyes.id("textures/dye/rainbow.png")).withTexturing(TexturedDye.Texturing.VERTICAL_SCROLL));

    public static final RegistrySupplier<FancyDye> AURORA = registerDye("aurora", () -> new TexturedDye(FancyDyes.id("textures/dye/aurora.png")).withTexturing(TexturedDye.Texturing.VERTICAL_SCROLL));
    public static final RegistrySupplier<FancyDye> BRIGHT_AURORA = registerDye("bright_aurora", () -> new BrightTexturedDye(FancyDyes.id("textures/dye/aurora.png")).withTexturing(TexturedDye.Texturing.VERTICAL_SCROLL));

    public static final RegistrySupplier<FancyDye> FLAME = registerDye("flame", () -> new FlameDye(FancyDyes.id("textures/dye/flame.png")));
    public static final RegistrySupplier<FancyDye> BRIGHT_FLAME = registerDye("bright_flame", () -> new BrightFlameDye(FancyDyes.id("textures/dye/flame.png")));
    public static final RegistrySupplier<FancyDye> GREEN_FLAME = registerDye("green_flame", () -> new FlameDye(FancyDyes.id("textures/dye/green_flame.png")));
    public static final RegistrySupplier<FancyDye> BRIGHT_GREEN_FLAME = registerDye("bright_green_flame", () -> new BrightFlameDye(FancyDyes.id("textures/dye/green_flame.png")));
    public static final RegistrySupplier<FancyDye> BLUE_FLAME = registerDye("blue_flame", () -> new FlameDye(FancyDyes.id("textures/dye/blue_flame.png")));
    public static final RegistrySupplier<FancyDye> BRIGHT_BLUE_FLAME = registerDye("bright_blue_flame", () -> new BrightFlameDye(FancyDyes.id("textures/dye/blue_flame.png")));

    public static ItemStack getCreativeTabIcon() {
        return new ItemStack(AURORA.get().getItem(false));
    }

    public static void init() {
        FancyDyeColor.registerDyes();
        SmithingDyeRecipe.register();
        SmithingDyeRemoveRecipe.register();

        DYES.register();
        ITEMS.register();
        TABS.register();
        SERIALIZERS.register();
    }

    public static RegistrySupplier<FancyDye> registerDye(String name, Supplier<FancyDye> supplier) {
        RegistrySupplier<FancyDye> dye = DYES.register(name, supplier);

        ITEMS.register("%s_dye".formatted(name), () -> new FancyDyeItem(dye, false));
        ITEMS.register("small_%s_dye".formatted(name), () -> new FancyDyeItem(dye, true));

        return dye;
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
