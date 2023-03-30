package net.grilledham.iceball.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemRegistry {
	
	public static final IceballItem ICEBALL_ITEM = new IceballItem(new FabricItemSettings().maxCount(16).rarity(Rarity.COMMON), 1, 0);
	public static final IceballItem PACKED_ICEBALL_ITEM = new IceballItem(new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON), 5, 5);
	public static final IceballItem BLUE_ICEBALL_ITEM = new IceballItem(new FabricItemSettings().maxCount(16).rarity(Rarity.RARE), 10, 10);
	public static final IceballItem SPIKEBALL_ITEM = new IceballItem(new FabricItemSettings().maxCount(16).rarity(Rarity.EPIC), 50, 0);
	
	public static void init() {
		register("iceball", ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(Items.SNOWBALL), new ItemGroupData(ItemGroups.INGREDIENTS).after(Items.SNOWBALL));
		register("packed_iceball", PACKED_ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(ICEBALL_ITEM), new ItemGroupData(ItemGroups.INGREDIENTS).after(ICEBALL_ITEM));
		register("blue_iceball", BLUE_ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(PACKED_ICEBALL_ITEM), new ItemGroupData(ItemGroups.INGREDIENTS).after(PACKED_ICEBALL_ITEM));
		register("spikeball", SPIKEBALL_ITEM, new ItemGroupData(ItemGroups.OPERATOR).operatorOnly());
	}
	
	private static void register(String id, Item item, ItemGroupData... groups) {
		Registry.register(Registries.ITEM, new Identifier("iceball", id), item);
		for(ItemGroupData group : groups) {
			if(group.after() != null) {
				ItemGroupEvents.modifyEntriesEvent(group.group()).register(
						group.isOperatorOnly() ?
								entries -> { if(MinecraftClient.getInstance().options.getOperatorItemsTab().getValue()) entries.addAfter(group.after(), item); } :
								entries -> entries.addAfter(group.after(), item)
				);
			} else {
				ItemGroupEvents.modifyEntriesEvent(group.group()).register(
						group.isOperatorOnly() ?
								entries -> { if(MinecraftClient.getInstance().options.getOperatorItemsTab().getValue()) entries.add(item); } :
								entries -> entries.add(item)
				);
			}
		}
	}
	
	private static class ItemGroupData {
		
		private ItemConvertible after = null;
		private final ItemGroup group;
		private boolean operatorOnly = false;
		
		public ItemGroupData(ItemGroup group) {
			this.group = group;
		}
		
		public ItemGroupData after(ItemConvertible after) {
			this.after = after;
			return this;
		}
		
		public ItemGroupData operatorOnly() {
			this.operatorOnly = true;
			return this;
		}
		
		public ItemConvertible after() {
			return after;
		}
		
		public ItemGroup group() {
			return group;
		}
		
		public boolean isOperatorOnly() {
			return operatorOnly;
		}
	}
}
