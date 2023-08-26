package net.haizor.fancydyes.mixin;

import net.haizor.fancydyes.dye.FancyDye;
import net.haizor.fancydyes.item.DyedThrownTrident;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ThrownTrident.class)
abstract class MixinThrownTrident extends AbstractArrow {
    protected MixinThrownTrident(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At("TAIL")
    )
    private void fancydyes$thrownTridentConstructor(Level level, LivingEntity livingEntity, ItemStack itemStack, CallbackInfo ci) {
        Optional<FancyDye> dye = FancyDye.getDye(itemStack, false);
        if (dye.isPresent()) {
            this.entityData.set(DyedThrownTrident.DYE_ID, dye.get().toIdString());
        }
    }

    @Inject(
        method = "defineSynchedData",
        at = @At("TAIL")
    )
    private void fancydyes$thrownTridentDefineData(CallbackInfo ci) {
        this.entityData.define(DyedThrownTrident.DYE_ID, "");
    }

    @Inject(
        method = "addAdditionalSaveData",
        at = @At("TAIL")
    )
    private void fancydyes$thrownTridentSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putString("FancyDye", this.entityData.get(DyedThrownTrident.DYE_ID));
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At("TAIL")
    )
    private void fancydyes$thrownTridentReadData(CompoundTag compoundTag, CallbackInfo ci) {
        String dyeId = compoundTag.getString("FancyDye");
        if (dyeId == null) dyeId = "";
        this.entityData.set(DyedThrownTrident.DYE_ID, dyeId);
    }
}
