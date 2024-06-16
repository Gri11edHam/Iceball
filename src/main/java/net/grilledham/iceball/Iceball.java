package net.grilledham.iceball;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;

public class Iceball implements ModInitializer {
	
	@Override
	public void onInitialize() {
		ItemRegistry.init();
		LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
			if(source.isBuiltin() && LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST.equals(key)) {
				tableBuilder.modifyPools(builder -> builder.with(ItemEntry.builder(ItemRegistry.SPIKEBALL_ITEM)));
			}
		});
	}
}
