package net.grilledham.iceball.neoforge;

import net.grilledham.iceball.Iceball;
import net.grilledham.iceball.neoforge.registry.NeoforgeEntityRegistry;
import net.grilledham.iceball.neoforge.registry.NeoforgeItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod("iceball")
public class Entrypoint {
	
	public Entrypoint(ModContainer modContainer, IEventBus modEventBus) {
		IEventBus eventBus = NeoForge.EVENT_BUS;
		
		modEventBus.register(this);
		
		Iceball.init();
		
		if(FMLEnvironment./*~ if >= 1.21.11 'dist' -> 'getDist()' >> '.'*/getDist().isClient()) {
			new ClientEntrypoint(modEventBus, eventBus);
		}
	}
	
	@SubscribeEvent
	public void register(RegisterEvent event) {
		NeoforgeItemRegistry.getInstance().register(event);
		NeoforgeEntityRegistry.getInstance().register(event);
	}
	
	@SubscribeEvent
	public void buildTabContents(BuildCreativeModeTabContentsEvent event) {
		NeoforgeItemRegistry.getInstance().buildTabContents(event);
	}
}
