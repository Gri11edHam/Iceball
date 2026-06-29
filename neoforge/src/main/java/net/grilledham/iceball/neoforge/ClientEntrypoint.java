package net.grilledham.iceball.neoforge;

import net.grilledham.iceball.client.IceballClient;
import net.grilledham.iceball.neoforge.registry.NeoforgeEntityRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
//? if < 1.21.11 {
/*import net.grilledham.iceball.registry.IceballItems;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
*///?}

public class ClientEntrypoint {
	
	public ClientEntrypoint(IEventBus modEventBus, IEventBus eventBus) {
		modEventBus.register(this);
		
		IceballClient.initClient();
	}
	
	@SubscribeEvent
	public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		NeoforgeEntityRegistry.getInstance().registerLayers(event);
	}
	
	//? if < 1.21.11 {
	/*@SubscribeEvent
	public void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> DyedItemColor.getOrDefault(stack, 0xFF88DD88), IceballItems.BOUNCY_BALL_ITEM);
		event.register((stack, tintIndex) -> DyedItemColor.getOrDefault(stack, 0xFF88DD88), IceballItems.BIG_BOUNCY_BALL_ITEM);
	}
	*///?}
}
