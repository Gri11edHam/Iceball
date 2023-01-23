package net.grilledham.iceball;

import net.fabricmc.api.ModInitializer;
import net.grilledham.iceball.registry.ItemRegistry;

public class Iceball implements ModInitializer {
	
	@Override
	public void onInitialize() {
		ItemRegistry.init();
	}
}
