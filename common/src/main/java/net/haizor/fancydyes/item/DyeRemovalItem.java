package net.haizor.fancydyes.item;

import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.tooltip.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DyeRemovalItem extends Item {
    public DyeRemovalItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (level == null || !level.isClientSide) return;
        components.add(Component.translatable("item.fancydyes.soap.desc").withStyle(ChatFormatting.GRAY));

        components.addAll(TooltipHelper.extended(() -> {
            List<Component> list = new ArrayList<>();
            String data = Component.translatable("item.fancydyes.soap.use_info").getString();
            list.add(Component.translatable("gui.tooltip.to_apply").withStyle(ChatFormatting.YELLOW));
            list.addAll(TooltipHelper.wrap(data, (c) -> c.withStyle(ChatFormatting.GRAY)));
            return list;
        }));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        ItemStack other = slot.getItem();
        if (other.isEmpty()) return false;
        if (clickAction.equals(ClickAction.SECONDARY)) {
            FancyDye otherDye = FancyDye.getDye(other);

            if (otherDye == null) return false;

            if (!player.isCreative()) {
                ItemStack otherDyeStack = new ItemStack(FancyDye.getItem(otherDye));
                if (!player.addItem(otherDyeStack)) {
                    player.drop(otherDyeStack, true);
                }
            }

            other.getOrCreateTag().remove("dye");

            if (player.getLevel().isClientSide)
                player.playSound(SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 1.0f, 1.0f);
            return true;
        }
        return super.overrideStackedOnOther(itemStack, slot, clickAction, player);
    }
}
