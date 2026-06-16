package net.grilledham.iceball.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.item.BigBouncyBallItem;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class ItemRegistry {
	
	public static final IceballItem ICEBALL_ITEM = register("iceball", new IceballItem.Builder()
			.damage(1)
			.cooldown(0)
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
	);
	public static final IceballItem PACKED_ICEBALL_ITEM = register("packed_iceball", new IceballItem.Builder()
			.damage(5)
			.cooldown(5)
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
	);
	public static final IceballItem BLUE_ICEBALL_ITEM = register("blue_iceball", new IceballItem.Builder()
			.damage(10)
			.cooldown(10)
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.RARE)
	);
	public static final IceballItem BOOMBALL_ITEM = register("boomball", new IceballItem.Builder()
			.damage(0)
			.cooldown(20)
			.onCollide((ball, hitResult) -> {
				ball.level().explode(ball, ball.damageSources().explosion(ball, ball.getOwner()), new ExplosionDamageCalculator(), ball.position(), 8, false, Level.ExplosionInteraction.MOB);
				return true;
			})
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)
	);
	public static final IceballItem SPIKEBALL_ITEM = register("spikeball", new IceballItem.Builder()
			.damage(30)
			.cooldown(20)
			.acceptableEnchants(Enchantments.MENDING, Enchantments.UNBREAKING)
			.onCollide((ball, hitResult) -> {
				ball.shouldDamageOwner = false;
				if(ball.getOwner() == null) {
					if(ball.level() instanceof ServerLevel world) {
						ball.spawnAtLocation(world, ball.getItem());
					}
					return true;
				}
				if(hitResult.getType() == HitResult.Type.ENTITY) {
					EntityHitResult ehr = (EntityHitResult)hitResult;
					if(ehr.getEntity() == ball.getOwner()) {
						if(ball.level() instanceof ServerLevel world) {
							if(ball.getOwner().isAlwaysTicking()) {
								Player owner = (Player)ball.getOwner();
								if(!owner.isCreative()) {
									ball.getItem().hurtAndBreak(1, owner, owner.getEquipmentSlotForItem(ball.getItem()));
									if(!owner.addItem(ball.getItem())) {
										ball.spawnAtLocation(world, ball.getItem());
									}
								}
							}
						}
						return true;
					}
				}
				if(hitResult instanceof BlockHitResult bhr) {
					Vec3 velocity = ball.getDeltaMovement().add(ball.getDeltaMovement().multiply(new Vec3(bhr.getDirection().step().absolute().mul(-1.9f))));
					ball.shoot(velocity.x(), velocity.y(), velocity.z(), (float)(ball.getDeltaMovement().length()), 0);
				}
				return false;
			})
			.onTick(ball -> {
				ball.shouldDamageOwner = false;
				if(ball.getOwner() != null) {
					Vec3 direction = ball.getOwner().position().subtract(ball.position()).normalize();
					ball.push(direction.x() * 0.1, direction.y() * 0.1, direction.z() * 0.1);
				}
			})
			.build(),
			new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(100)
	);
	public static final Item MEATBALL_ITEM = register("meatball", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.3f).build()));
	public static final Item COOKED_MEATBALL_ITEM = register("cooked_meatball", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.0f).build()));
	public static final IceballItem BOUNCY_BALL_ITEM = register("bouncy_ball", new IceballItem.Builder()
			.damage(1)
			.cooldown(0)
			.onCollide((ball, hitResult) -> {
				if(hitResult instanceof BlockHitResult bhr) {
					Vec3 velocity = ball.getDeltaMovement().add(ball.getDeltaMovement().multiply(new Vec3(bhr.getDirection().step().absolute().mul(-1.7f))));
					ball.shoot(velocity.x(), velocity.y(), velocity.z(), (float)(ball.getDeltaMovement().length() * 0.8), 0);
				} else if(hitResult instanceof EntityHitResult) {
					if(ball.level() instanceof ServerLevel world) {
						if(ball.getOwner() != null) {
							if(ball.getOwner().isAlwaysTicking()) {
								Player owner = (Player)ball.getOwner();
								if(!owner.isCreative()) {
									ball.spawnAtLocation(world, ball.getItem());
								}
							}
						} else {
							ball.spawnAtLocation(world, ball.getItem());
						}
					}
					return true;
				}
				if(ball.getDeltaMovement().length() < 0.1) {
					if(ball.level() instanceof ServerLevel world) {
						if(ball.getOwner() != null) {
							if(ball.getOwner().isAlwaysTicking()) {
								Player owner = (Player)ball.getOwner();
								if(!owner.isCreative()) {
									ball.spawnAtLocation(world, ball.getItem());
								}
							}
						} else {
							ball.spawnAtLocation(world, ball.getItem());
						}
					}
					return true;
				}
				return false;
			})
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
	);
	public static final BigBouncyBallItem BIG_BOUNCY_BALL_ITEM = register("big_bouncy_ball", BigBouncyBallItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	public static final IceballItem LIGHTNING_BALL_ITEM = register("lightning_ball", new IceballItem.Builder()
			.damage(1)
			.cooldown(0)
			.onCollide((ball, hitResult) -> {
				if(ball.level() instanceof ServerLevel world) {
					if(ball.getOwner() != null) {
						if(ball.getOwner().isAlwaysTicking()) {
							Player owner = (Player)ball.getOwner();
							if(!owner.isCreative()) {
								ball.spawnAtLocation(world, ball.getItem());
							}
						}
					} else {
						ball.spawnAtLocation(world, ball.getItem());
					}
				}
				return true;
			})
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
	);
	public static final IceballItem CHARGED_LIGHTNING_BALL_ITEM = register("charged_lightning_ball", new IceballItem.Builder()
			.damage(0)
			.cooldown(5)
			.onCollide((ball, hitResult) -> {
				LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, ball.level());
				lightning.setPos(ball.position());
				ball.level().addFreshEntity(lightning);
				if(!ball.level().isClientSide()) {
					if(ball.getOwner() != null) {
						if(ball.getOwner().isAlwaysTicking()) {
							Player owner = (Player)ball.getOwner();
							if(!owner.isCreative()) {
								ItemEntity itemEntity = new ItemEntity(ball.level(), ball.getX(), ball.getY(), ball.getZ(), ball.getItem().transmuteCopy(LIGHTNING_BALL_ITEM));
								itemEntity.setInvulnerable(true);
								itemEntity.setDefaultPickUpDelay();
								ball.level().addFreshEntity(itemEntity);
							}
						}
					} else {
						ItemEntity itemEntity = new ItemEntity(ball.level(), ball.getX(), ball.getY(), ball.getZ(), ball.getItem().transmuteCopy(LIGHTNING_BALL_ITEM));
						itemEntity.setInvulnerable(true);
						itemEntity.setDefaultPickUpDelay();
						ball.level().addFreshEntity(itemEntity);
					}
				}
				return true;
			})
			.build(),
			new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
	);
	
	public static void init() {
		DispenserBlock.registerProjectileBehavior(ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(PACKED_ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BLUE_ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BOOMBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(SPIKEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BOUNCY_BALL_ITEM);
		DispenserBlock.registerBehavior(BIG_BOUNCY_BALL_ITEM, new DispenseItemBehavior() {
			@Override
			public ItemStack dispense(BlockSource source, ItemStack dispensed) {
				Direction direction = source.state().getValue(DispenserBlock.FACING);
				BlockPos blockPos = source.pos().relative(direction);
				ServerLevel serverWorld = source.level();
				BigBouncyBallEntity bigBouncyBallEntity = EntityRegistry.BIG_BOUNCY_BALL_ENTITY.spawn(serverWorld, EntityType.appendCustomEntityStackConfig(bigBouncyBall -> bigBouncyBall.setYRot(direction.toYRot()), serverWorld, dispensed, null), blockPos, EntitySpawnReason.DISPENSER, false, false);
				if(bigBouncyBallEntity != null) {
					bigBouncyBallEntity.setBallColor(DyedItemColor.getOrDefault(dispensed, 0xFF88DD88));
					dispensed.shrink(1);
				}
				return dispensed;
			}
		});
		DispenserBlock.registerProjectileBehavior(LIGHTNING_BALL_ITEM);
		DispenserBlock.registerProjectileBehavior(CHARGED_LIGHTNING_BALL_ITEM);
	}
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		registerClient(ICEBALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(Items.SNOWBALL), new ItemGroupData(CreativeModeTabs.INGREDIENTS).after(Items.SNOWBALL));
		registerClient(PACKED_ICEBALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(ICEBALL_ITEM), new ItemGroupData(CreativeModeTabs.INGREDIENTS).after(ICEBALL_ITEM));
		registerClient(BLUE_ICEBALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(PACKED_ICEBALL_ITEM), new ItemGroupData(CreativeModeTabs.INGREDIENTS).after(PACKED_ICEBALL_ITEM));
		registerClient(BOOMBALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(BLUE_ICEBALL_ITEM));
		registerClient(LIGHTNING_BALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(BOOMBALL_ITEM));
		registerClient(CHARGED_LIGHTNING_BALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(LIGHTNING_BALL_ITEM));
		registerClient(SPIKEBALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(CHARGED_LIGHTNING_BALL_ITEM));
		registerClient(MEATBALL_ITEM, new ItemGroupData(CreativeModeTabs.FOOD_AND_DRINKS).after(Items.COOKED_RABBIT));
		registerClient(COOKED_MEATBALL_ITEM, new ItemGroupData(CreativeModeTabs.FOOD_AND_DRINKS).after(MEATBALL_ITEM));
		registerClient(BOUNCY_BALL_ITEM, new ItemGroupData(CreativeModeTabs.COMBAT).after(SPIKEBALL_ITEM));
		registerClient(BIG_BOUNCY_BALL_ITEM, new ItemGroupData(CreativeModeTabs.TOOLS_AND_UTILITIES).after(Items.BAMBOO_CHEST_RAFT));
	}
	
	private static <T extends Item> T register(String id, Function<Item.Properties, T> factory, Item.Properties settings) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.tryBuild("iceball", id));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(settings.setId(key)));
	}
	
	@Environment(EnvType.CLIENT)
	private static void registerClient(Item item, ItemGroupData... groups) {
		for(ItemGroupData group : groups) {
			if(group.after() != null) {
				CreativeModeTabEvents.modifyOutputEvent(group.group()).register(
						group.isOperatorOnly() ?
								entries -> { if(Minecraft.getInstance().options.operatorItemsTab().get()) entries.insertAfter(group.after(), item); } :
								entries -> entries.insertAfter(group.after(), item)
				);
			} else {
				CreativeModeTabEvents.modifyOutputEvent(group.group()).register(
						group.isOperatorOnly() ?
								entries -> { if(Minecraft.getInstance().options.operatorItemsTab().get()) entries.accept(item); } :
								entries -> entries.accept(item)
				);
			}
		}
	}
	
	private static class ItemGroupData {
		
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
