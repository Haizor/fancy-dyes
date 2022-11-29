package net.haizor.fancydyes.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record DyeTutorialTooltip(ItemStack itemStack) implements TooltipComponent {
}
