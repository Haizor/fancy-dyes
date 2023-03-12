package net.haizor.fancydyes.dyes;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class AuroraDye extends OverlayDye {
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation("chests/end_city_treasure");
    public static final LootItemCondition LOOT_CONDITION = LootItemRandomChanceCondition.randomChance(0.2f).build();

    public AuroraDye() {
        super("aurora");
    }

    @Override
    public Type getType() {
        return Type.OVERLAY;
    }

    @Override
    public @Nullable Component tooltip() {
        return new TranslatableComponent("item.fancydyes.aurora_dye.desc").withStyle(ChatFormatting.GRAY);
    }
}
