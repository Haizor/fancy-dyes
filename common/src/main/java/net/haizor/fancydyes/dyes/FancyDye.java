package net.haizor.fancydyes.dyes;

import net.haizor.fancydyes.FancyDyes;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    enum Type {
        BASE,
        OVERLAY
    }
}
