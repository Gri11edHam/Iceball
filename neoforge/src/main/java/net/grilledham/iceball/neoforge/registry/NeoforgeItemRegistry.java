//~ identifier
package net.grilledham.iceball.neoforge.registry;

import net.grilledham.iceball.registry.IceballItems;
import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class NeoforgeItemRegistry implements ItemRegistry {
	
	private static NeoforgeItemRegistry INSTANCE = null;
	
	private final HashMap<ResourceKey<CreativeModeTab>, List<Consumer<BuildCreativeModeTabContentsEvent>>> registryEvents = new HashMap<>();
	
	private RegisterEvent.RegisterHelper<Item> registry;
	
	public static NeoforgeItemRegistry getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new NeoforgeItemRegistry();
		}
		return INSTANCE;
	}
	
	@Override
	public <T extends Item> T register(String id, Function<Item.Properties, T> factory, Item.Properties settings) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.tryBuild("iceball", id));
		//? >= 1.21.11
		settings.setId(key);
		T item = factory.apply(settings);
		registry.register(key, item);
		return item;
	}
	
	@Override
	public void registerClient(Item item, ItemGroupData... groups) {
		for(ItemGroupData group : groups) {
			if(!registryEvents.containsKey(group.group())) registryEvents.put(group.group(), new ArrayList<>());
			if(group.after() != null) {
				registryEvents.get(group.group()).add(
						group.isOperatorOnly() ?
								entries -> {
									if(Minecraft.getInstance().options.operatorItemsTab().get())
										entries.insertAfter(new ItemStack(group.after()), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
								} :
								entries -> entries.insertAfter(new ItemStack(group.after()), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)
				);
			} else {
				registryEvents.get(group.group()).add(
						group.isOperatorOnly() ?
								entries -> {
									if(Minecraft.getInstance().options.operatorItemsTab().get()) entries.accept(item);
								} :
								entries -> entries.accept(item)
				);
			}
		}
	}
	
	public void buildTabContents(BuildCreativeModeTabContentsEvent event) {
		if(registryEvents.containsKey(event.getTabKey())) {
			registryEvents.get(event.getTabKey()).forEach(entry -> entry.accept(event));
		}
	}
	
	public void register(RegisterEvent event) {
		event.register(BuiltInRegistries.ITEM.key(), registry -> {
			this.registry = registry;
			IceballItems.init(this);
			IceballItems.initClient(this);
		});
	}
}
