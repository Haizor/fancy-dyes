package net.haizor.fancydyes.dyes;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class FlameDye implements FancyDye {
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation("chests/nether_bridge");
    public static final LootItemCondition LOOT_CONDITION = LootItemRandomChanceCondition.randomChance(0.2f).build();

    @Override
    public String getArmorRenderType() {
        return "flame_armor";
    }

    @Override
    public String getItemRenderType() {
        return "flame_item";
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public Type getType() {
        return Type.OVERLAY;
    }

    @Override
    public @Nullable Component tooltip() {
        return Component.translatable("item.fancydyes.flame_dye.desc").withStyle(ChatFormatting.GRAY);
    }
}
