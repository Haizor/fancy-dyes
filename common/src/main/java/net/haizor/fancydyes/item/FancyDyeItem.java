package net.haizor.fancydyes.item;

import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.tooltip.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

public class FancyDyeItem extends Item {
    public final FancyDye dye;
    public FancyDyeItem(Properties properties, FancyDye dye) {
        super(properties);
        this.dye = dye;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (level == null || !level.isClientSide) return;
        Component tooltip = this.dye.tooltip();
        if (tooltip != null) {
            tooltipComponents.add(tooltip);
        }

        tooltipComponents.addAll(TooltipHelper.extended(() -> {
            List<Component> list = new ArrayList<>();
            String data = new TranslatableComponent("item.fancydyes.dye.use_info").getString();
            list.add(new TranslatableComponent("gui.tooltip.to_apply").withStyle(ChatFormatting.YELLOW));
            list.addAll(TooltipHelper.wrap(data, (c) -> c.withStyle(ChatFormatting.GRAY)));
            return list;
        }));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        ItemStack other = slot.getItem();
        if (!FancyDye.isDyeable(other)) return false;
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
        return false;
    }
}
