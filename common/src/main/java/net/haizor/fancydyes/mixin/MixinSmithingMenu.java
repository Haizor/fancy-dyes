package net.haizor.fancydyes.mixin;

import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.crafting.SmithingDyeRecipe;
import net.haizor.fancydyes.crafting.SmithingDyeRemoveRecipe;
import net.haizor.fancydyes.dye.FancyDye;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(SmithingMenu.class)
abstract class MixinSmithingMenu extends ItemCombinerMenu {
    @Shadow @Nullable private SmithingRecipe selectedRecipe;

    @Shadow @Final public static int TEMPLATE_SLOT;

    @Shadow @Final public static int BASE_SLOT;

    @Inject(method = "shrinkStackInSlot", at = @At("HEAD"), cancellable = true)
    private void fancydyes$dyeRemoval(int i, CallbackInfo ci) {
        if (i == TEMPLATE_SLOT) {
            if (selectedRecipe instanceof SmithingDyeRemoveRecipe recipe) {
                Optional<FancyDye> dye = FancyDye.getDye(inputSlots.getItem(BASE_SLOT), recipe.secondary);
                ItemStack curr = inputSlots.getItem(TEMPLATE_SLOT);
                Item dyeItem = dye.orElseThrow().getItem(recipe.secondary);
                if (curr == null || curr.isEmpty()) {
                    curr = new ItemStack(dye.orElseThrow().getItem(recipe.secondary));
                } else if (curr.is(dyeItem)){
                    curr.grow(1);
                } else {
                    return;
                }
                inputSlots.setItem(TEMPLATE_SLOT, curr);
                ci.cancel();
            } else if (selectedRecipe instanceof SmithingDyeRecipe recipe) {
                ItemStack curr = inputSlots.getItem(TEMPLATE_SLOT);
                Item item =  recipe.secondary ? FancyDyes.SMALL_DYE_BOTTLE.get() : FancyDyes.DYE_BOTTLE.get();
                if (curr == null || curr.isEmpty()) {
                    curr = new ItemStack(item);
                } else if (curr.is(item)) {
                    curr.grow(1);
                } else {
                    return;
                }
                inputSlots.setItem(TEMPLATE_SLOT, curr);
                ci.cancel();
            }
        }
    }

    public MixinSmithingMenu(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }
}
