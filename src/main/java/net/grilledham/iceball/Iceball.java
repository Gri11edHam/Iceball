package net.grilledham.iceball;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.grilledham.iceball.registry.EntityRegistry;
import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;

public class Iceball implements ModInitializer {
	
	@Override
	public void onInitialize() {
		ItemRegistry.init();
		EntityRegistry.init();
		LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
			if(source.isBuiltin() && BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE.equals(key)) {
				tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(ItemRegistry.SPIKEBALL_ITEM)));
			}
		});
	}
}
