package net.haizor.fancydyes.dyes;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Wearable;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface FancyDye {
    String getArmorRenderType();
    String getItemRenderType();
    Color getColor();
    default Type getType() { return Type.OVERLAY; }
    default void renderTick(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {}
    default @Nullable Component tooltip() { return null; }

    static FancyDye getDye(ItemStack stack) {
        return FancyDyes.DYES.get(stack.getOrCreateTag().getString("dye"));
    }

    static Item getItem(FancyDye dye) {
        return Registry.ITEM.get(new ResourceLocation(FancyDyes.MOD_ID, FancyDyes.DYES.inverse().get(dye) + "_dye"));
    }

    static Item getItem(String dye) {
        return getItem(FancyDyes.DYES.get(dye));
    }

    static boolean isDyeable(ItemStack stack) {
        return !stack.isEmpty() && !stack.is(DYEABLE_BLACKLIST) && (
            stack.getItem() instanceof TieredItem ||
            stack.getItem() instanceof Wearable ||
            stack.is(DYEABLE)
        );
    }

    TagKey<Item> DYEABLE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(FancyDyes.MOD_ID, "dyeable"));
    TagKey<Item> DYEABLE_BLACKLIST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(FancyDyes.MOD_ID, "dyeable_blacklist"));

    enum Type {
        BASE,
        OVERLAY
    }
}
