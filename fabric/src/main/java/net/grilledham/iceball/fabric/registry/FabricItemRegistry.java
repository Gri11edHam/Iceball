package net.grilledham.iceball.fabric.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//? if >= 26.1 {
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
//?} else
//import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;

public class FabricItemRegistry implements ItemRegistry {
	
	private static FabricItemRegistry INSTANCE = null;
	
	public static FabricItemRegistry getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new FabricItemRegistry();
		}
		return INSTANCE;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void registerClient(Item item, net.grilledham.iceball.registry.ItemRegistry.ItemGroupData... groups) {
		for(net.grilledham.iceball.registry.ItemRegistry.ItemGroupData group : groups) {
			//~ if >= 26.1 'ItemGroupEvents.modifyEntries' -> 'CreativeModeTabEvents.modifyOutput' {
			//~ if >= 26.1 'addAfter' -> 'insertAfter' {
			if(group.after() != null) {
				CreativeModeTabEvents.modifyOutputEvent(group.group()).register(
						group.isOperatorOnly() ?
								entries -> {
									if(Minecraft.getInstance().options.operatorItemsTab().get())
										entries.insertAfter(group.after(), item);
								} :
								entries -> entries.insertAfter(group.after(), item)
				);
			} else {
				CreativeModeTabEvents.modifyOutputEvent(group.group()).register(
						group.isOperatorOnly() ?
								entries -> {
									if(Minecraft.getInstance().options.operatorItemsTab().get()) entries.accept(item);
								} :
								entries -> entries.accept(item)
				);
			}
			//~}
			//~}
		}
	}
}
