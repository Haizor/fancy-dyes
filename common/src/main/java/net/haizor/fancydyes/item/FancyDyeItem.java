package net.haizor.fancydyes.item;

import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FancyDyeItem extends Item {
    public final FancyDye dye;
    public FancyDyeItem(Properties properties, FancyDye dye) {
        super(properties);
        this.dye = dye;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (level == null || !level.isClientSide) return;
        Component tooltip = this.dye.tooltip();
        if (tooltip != null) {
            list.add(tooltip);
        }
        if (!Screen.hasShiftDown()) {
            list.add(new TextComponent("Press <Shift> for info").withStyle(ChatFormatting.GRAY));
        } else {
            String data = new TranslatableComponent("gui.dye.tooltip.apply_instructions").getString();
            String[] split = data.split(" ");
            int l = data.length();
            int i = 0;
            list.add(new TranslatableComponent("gui.dye.tooltip.to_apply").withStyle(ChatFormatting.YELLOW));
            while (l > 0) {
                String line = "";
                int charCount = 0;
                while (charCount < 24 && i < split.length) {
                    String curr = split[i];
                    if (i != split.length - 1) curr += " ";
                    line += curr;
                    i++;
                    charCount += curr.length();
                }
                l -= charCount;
                list.add(new TextComponent("  %s".formatted(line)).withStyle(ChatFormatting.GRAY));
            }
//            list.add(new TextComponent("To Apply:").withStyle(ChatFormatting.YELLOW));
//            list.add(new TextComponent("  Right click on any dyable item").withStyle(ChatFormatting.GRAY));
//            list.add(new TextComponent("  in your inventory with this dye").withStyle(ChatFormatting.GRAY));
//            list.add(new TextComponent("  selected.").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        ItemStack other = slot.getItem();
        if (other.isEmpty()) return false;
        if (clickAction.equals(ClickAction.SECONDARY)) {
            FancyDye otherDye = FancyDye.getDye(other);

            if (!player.isCreative()) {
                itemStack.shrink(1);
                if (otherDye != null) {
                    ItemStack otherDyeStack = new ItemStack(FancyDye.getItem(otherDye));
                    if (itemStack.isEmpty()) {
                        player.containerMenu.setCarried(otherDyeStack);
                    } else if (!player.addItem(otherDyeStack)) {
                        player.drop(otherDyeStack, true);
                    }
                }

            }

            CompoundTag tag = other.getOrCreateTag();
            tag.putString("dye", FancyDyes.DYES.inverse().get(dye));
            other.setTag(tag);

            if (player.getLevel().isClientSide)
                player.playSound(SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 1.0f, 1.0f);
            return true;
        }
        return super.overrideStackedOnOther(itemStack, slot, clickAction, player);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return Optional.empty();
    }
}
