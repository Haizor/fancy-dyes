package net.haizor.fancydyes.item;

import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DyeRemovalItem extends Item {
    public DyeRemovalItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (level == null || !level.isClientSide) return;
        list.add(new TextComponent("Used to remove dyes").withStyle(ChatFormatting.GRAY));
        if (!Screen.hasShiftDown()) {
            list.add(new TextComponent("Press <Shift> for info").withStyle(ChatFormatting.GRAY));
        } else {
            list.add(new TextComponent("To Use:").withStyle(ChatFormatting.YELLOW));
            list.add(new TextComponent("  Right click on any item with").withStyle(ChatFormatting.GRAY));
            list.add(new TextComponent("  dye in your inventory with").withStyle(ChatFormatting.GRAY));
            list.add(new TextComponent("  this item selected.").withStyle(ChatFormatting.GRAY));
        }
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
