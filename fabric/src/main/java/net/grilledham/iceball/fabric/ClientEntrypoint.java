package net.grilledham.iceball.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.grilledham.iceball.client.IceballClient;
import net.grilledham.iceball.fabric.registry.FabricEntityRegistry;
import net.grilledham.iceball.fabric.registry.FabricItemRegistry;
import net.grilledham.iceball.registry.IceballEntities;
import net.grilledham.iceball.registry.IceballItems;
//? if < 1.21.11 {
/*import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.item.component.DyedItemColor;
*///?}

@Environment(EnvType.CLIENT)
public class ClientEntrypoint implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		IceballClient.initClient();
		IceballItems.initClient(FabricItemRegistry.getInstance());
		IceballEntities.initClient(FabricEntityRegistry.getInstance());
		
		//? if < 1.21.11 {
		/*ColorProviderRegistry.ITEM.register((stack, tintIndex) -> DyedItemColor.getOrDefault(stack, 0xFF88DD88), IceballItems.BOUNCY_BALL_ITEM);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> DyedItemColor.getOrDefault(stack, 0xFF88DD88), IceballItems.BIG_BOUNCY_BALL_ITEM);
		*///?}
	}
}
