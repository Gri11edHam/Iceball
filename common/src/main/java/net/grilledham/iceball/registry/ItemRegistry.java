//~ identifier
package net.grilledham.iceball.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

public interface ItemRegistry {
	
	default <T extends Item> T register(String id, Function<Item.Properties, T> factory, Item.Properties settings) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.tryBuild("iceball", id));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(settings/*? if >= 1.21.11 >>+ ')'*/.setId(key)));
	}
	
	void registerClient(Item item, ItemGroupData... groups);
	
	class ItemGroupData {
		
		private ItemLike after = null;
		private final ResourceKey<CreativeModeTab> group;
		private boolean operatorOnly = false;
		
		public ItemGroupData(ResourceKey<CreativeModeTab> group) {
			this.group = group;
		}
		
		public ItemGroupData after(ItemLike after) {
			this.after = after;
			return this;
		}
		
		public ItemGroupData operatorOnly() {
			this.operatorOnly = true;
			return this;
		}
		
		public ItemLike after() {
			return after;
		}
		
		public ResourceKey<CreativeModeTab> group() {
			return group;
		}
		
		public boolean isOperatorOnly() {
			return operatorOnly;
		}
	}
}
