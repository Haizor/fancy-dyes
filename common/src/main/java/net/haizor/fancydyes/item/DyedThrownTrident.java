package net.haizor.fancydyes.item;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.projectile.ThrownTrident;

public interface DyedThrownTrident {
    EntityDataAccessor<String> DYE_ID = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.STRING);
}
