package net.haizor.fancydyes;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.haizor.fancydyes.dyes.*;
import net.haizor.fancydyes.item.DyeRemovalItem;
import net.haizor.fancydyes.item.FancyDyeItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class FancyDyes {
    public static final String MOD_ID = "fancydyes";
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> ICON = ITEMS.register("dye_bottle", () -> new Item(new Item.Properties()));

    public static final CreativeModeTab FANCY_DYES_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "dyes"), () ->
            new ItemStack(ICON.get()));

    public static final RegistrySupplier<Item> EMPTY_DYE_BOTTLE = ITEMS.register("empty_dye_bottle", () -> new Item(new Item.Properties().tab(FANCY_DYES_TAB)));
    public static final RegistrySupplier<Item> DYE_REMOVER = ITEMS.register("soap", () -> new DyeRemovalItem(new Item.Properties().tab(FANCY_DYES_TAB).stacksTo(1)));

    public static final BiMap<String, FancyDye> DYES = HashBiMap.create();
    
    public static void init() {
        for (StandardDyeColors.DyeColor color : StandardDyeColors.DYE_COLORS) {
            DYES.put("solid_" + color.string(), new SolidDye(color.color()));
        }

        for (StandardDyeColors.DyeColor color : StandardDyeColors.DYE_COLORS) {
            DYES.put("shimmer_" + color.string(), new ShimmerDye(color.color()));
        }

        DYES.put("inverted", new InvertDye());
        DYES.put("rainbow", new RainbowDye());
        DYES.put("aurora", new AuroraDye());
        DYES.put("flame", new FlameDye());

        for (String key : DYES.keySet()) {
            FancyDye dye = DYES.get(key);
            ITEMS.register(key + "_dye", () -> new FancyDyeItem(new Item.Properties().tab(FancyDyes.FANCY_DYES_TAB), dye));
        }



        ITEMS.register();
    }
}
