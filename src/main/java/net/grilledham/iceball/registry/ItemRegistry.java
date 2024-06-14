package net.grilledham.iceball.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

public class ItemRegistry {
	
	public static final IceballItem ICEBALL_ITEM = new IceballItem(new Item.Settings().maxCount(16).rarity(Rarity.COMMON), 1, 0);
	public static final IceballItem PACKED_ICEBALL_ITEM = new IceballItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON), 5, 5);
	public static final IceballItem BLUE_ICEBALL_ITEM = new IceballItem(new Item.Settings().maxCount(16).rarity(Rarity.RARE), 10, 10);
	public static final IceballItem BOOMBALL_ITEM = new IceballItem(new Item.Settings().maxCount(16).rarity(Rarity.EPIC), 0, 20, ball -> ball.getWorld().createExplosion(ball, ball.getDamageSources().explosion(ball, ball.getOwner()), new ExplosionBehavior(), ball.getPos(), 8, false, World.ExplosionSourceType.MOB));
	public static final IceballItem SPIKEBALL_ITEM = new IceballItem(new Item.Settings().maxCount(16).rarity(Rarity.EPIC), 50, 0);
	
	public static void init() {
		register("iceball", ICEBALL_ITEM);
		register("packed_iceball", PACKED_ICEBALL_ITEM);
		register("blue_iceball", BLUE_ICEBALL_ITEM);
		register("boomball", BOOMBALL_ITEM);
		register("spikeball", SPIKEBALL_ITEM);
	}
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		registerClient(ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(Items.SNOWBALL), new ItemGroupData(ItemGroups.INGREDIENTS).after(Items.SNOWBALL));
		registerClient(PACKED_ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(ICEBALL_ITEM), new ItemGroupData(ItemGroups.INGREDIENTS).after(ICEBALL_ITEM));
		registerClient(BLUE_ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(PACKED_ICEBALL_ITEM), new ItemGroupData(ItemGroups.INGREDIENTS).after(PACKED_ICEBALL_ITEM));
		registerClient(BOOMBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(BLUE_ICEBALL_ITEM));
		registerClient(SPIKEBALL_ITEM, new ItemGroupData(ItemGroups.OPERATOR).operatorOnly());
	}
	
	private static void register(String id, Item item) {
		Registry.register(Registries.ITEM, Identifier.of("iceball", id), item);
	}
	
	@Environment(EnvType.CLIENT)
	private static void registerClient(Item item, ItemGroupData... groups) {
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
		private final RegistryKey<ItemGroup> group;
		private boolean operatorOnly = false;
		
		public ItemGroupData(RegistryKey<ItemGroup> group) {
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
		
		public RegistryKey<ItemGroup> group() {
			return group;
		}
		
		public boolean isOperatorOnly() {
			return operatorOnly;
		}
	}
}
