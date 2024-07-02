package net.grilledham.iceball.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

public class ItemRegistry {
	
	public static final IceballItem ICEBALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.COMMON))
			.damage(1)
			.cooldown(0)
			.build();
	public static final IceballItem PACKED_ICEBALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON))
			.damage(5)
			.cooldown(5)
			.build();
	public static final IceballItem BLUE_ICEBALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.RARE))
			.damage(10)
			.cooldown(10)
			.build();
	public static final IceballItem BOOMBALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.EPIC))
			.damage(0)
			.cooldown(20)
			.onCollide((ball, hitResult) -> {
				ball.getWorld().createExplosion(ball, ball.getDamageSources().explosion(ball, ball.getOwner()), new ExplosionBehavior(), ball.getPos(), 8, false, World.ExplosionSourceType.MOB);
				return true;
			})
			.build();
	public static final IceballItem SPIKEBALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).maxDamage(100))
			.damage(30)
			.cooldown(20)
			.acceptableEnchants(Enchantments.MENDING, Enchantments.UNBREAKING)
			.onCollide((ball, hitResult) -> {
				ball.shouldDamageOwner = false;
				if(ball.getOwner() == null) {
					return true;
				}
				if(hitResult.getType() == HitResult.Type.ENTITY) {
					EntityHitResult ehr = (EntityHitResult)hitResult;
					if(ehr.getEntity() == ball.getOwner()) {
						if(ball.getOwner().isPlayer()) {
							PlayerEntity owner = (PlayerEntity)ball.getOwner();
							if(!owner.isInCreativeMode()) {
								ball.getStack().damage(1, owner, owner.getPreferredEquipmentSlot(ball.getStack()));
								if(!owner.giveItemStack(ball.getStack())) {
									ball.dropStack(ball.getStack());
								}
							}
						}
						return true;
					}
				}
				Vec3d direction = ball.getOwner().getPos().subtract(ball.getPos()).normalize().add(0, 0.2, 0);
				ball.setVelocity(direction.getX(), direction.getY(), direction.getZ(), 1f, 0);
				return false;
			})
			.build();
	
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
		registerClient(SPIKEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(BOOMBALL_ITEM));
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
