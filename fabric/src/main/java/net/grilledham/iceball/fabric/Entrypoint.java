package net.grilledham.iceball.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.grilledham.iceball.Iceball;
import net.grilledham.iceball.fabric.registry.FabricEntityRegistry;
import net.grilledham.iceball.fabric.registry.FabricItemRegistry;
import net.grilledham.iceball.registry.IceballEntities;
import net.grilledham.iceball.registry.IceballItems;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;

public class Entrypoint implements ModInitializer {
	
	@Override
	public void onInitialize() {
		Iceball.init();
		IceballItems.init(FabricItemRegistry.getInstance());
		IceballEntities.init(FabricEntityRegistry.getInstance());
		LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
			if(source.isBuiltin() && BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE.equals(key)) {
				tableBuilder.modifyPools(builder -> builder.add(LootItem.lootTableItem(IceballItems.SPIKEBALL_ITEM)));
			}
		});
	}
}
