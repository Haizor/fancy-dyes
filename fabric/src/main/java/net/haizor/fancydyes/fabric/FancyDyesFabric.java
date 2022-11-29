package net.haizor.fancydyes.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.haizor.fancydyes.FancyDyes;
import net.haizor.fancydyes.dyes.AuroraDye;
import net.haizor.fancydyes.dyes.FancyDye;
import net.haizor.fancydyes.dyes.FlameDye;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;

public class FancyDyesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FancyDyes.init();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin()) {
                if (FlameDye.LOOT_TABLE.equals(id))
                    tableBuilder.pool(LootPool.lootPool()
                        .with(LootItem.lootTableItem(FancyDye.getItem("flame")).build())
                        .conditionally(FlameDye.LOOT_CONDITION)
                        .build()
                    );
                if (AuroraDye.LOOT_TABLE.equals(id))
                    tableBuilder.pool(LootPool.lootPool()
                        .with(LootItem.lootTableItem(FancyDye.getItem("aurora")).build())
                        .conditionally(FlameDye.LOOT_CONDITION)
                        .build()
                    );
            }
        });
    }
}
