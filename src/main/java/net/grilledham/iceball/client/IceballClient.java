package net.grilledham.iceball.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.grilledham.iceball.registry.EntityRegistry;
import net.grilledham.iceball.registry.ItemRegistry;

@Environment(EnvType.CLIENT)
public class IceballClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		ItemRegistry.initClient();
		EntityRegistry.initClient();
	}
}
