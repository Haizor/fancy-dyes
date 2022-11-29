package net.haizor.fancydyes.tooltip;

import net.haizor.fancydyes.dyes.FancyDye;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record DyeTooltip(FancyDye dye) implements TooltipComponent {
}
