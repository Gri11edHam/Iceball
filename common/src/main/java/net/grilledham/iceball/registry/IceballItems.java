//~ identifier
package net.grilledham.iceball.registry;

import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.item.BigBouncyBallItem;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IceballItems {
	
	public static IceballItem ICEBALL_ITEM;
	public static IceballItem PACKED_ICEBALL_ITEM;
	public static IceballItem BLUE_ICEBALL_ITEM;
	public static IceballItem BOOMBALL_ITEM;
	public static IceballItem SPIKEBALL_ITEM;
	public static Item MEATBALL_ITEM;
	public static Item COOKED_MEATBALL_ITEM;
	public static IceballItem BOUNCY_BALL_ITEM;
	public static BigBouncyBallItem BIG_BOUNCY_BALL_ITEM;
	public static IceballItem LIGHTNING_BALL_ITEM;
	public static IceballItem CHARGED_LIGHTNING_BALL_ITEM;
	
	public static void init(ItemRegistry itemRegistry) {
		ICEBALL_ITEM = itemRegistry.register("iceball", new IceballItem.Builder()
						.damage(1)
						.cooldown(0)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		PACKED_ICEBALL_ITEM = itemRegistry.register("packed_iceball", new IceballItem.Builder()
						.damage(5)
						.cooldown(5)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
		);
		BLUE_ICEBALL_ITEM = itemRegistry.register("blue_iceball", new IceballItem.Builder()
						.damage(10)
						.cooldown(10)
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.RARE)
		);
		BOOMBALL_ITEM = itemRegistry.register("boomball", new IceballItem.Builder()
						.damage(0)
						.cooldown(20)
						.onCollide((ball, hitResult) -> {
							ball.level().explode(ball, ball.damageSources().explosion(ball, ball.getOwner()), new ExplosionDamageCalculator(), ball.position(), 8, false, Level.ExplosionInteraction.MOB);
							return true;
						})
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)
		);
		SPIKEBALL_ITEM = itemRegistry.register("spikeball", new IceballItem.Builder()
						.damage(30)
						.cooldown(20)
						.onCollide((ball, hitResult) -> {
							ball.shouldDamageOwner = false;
							if(ball.getOwner() == null) {
								if(ball.level() instanceof ServerLevel world) {
									ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
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
													ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
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
								if(ball.level() instanceof ServerLevel world) {
									if(ball.getOwner().getBoundingBox().intersects(ball.getBoundingBox())) {
										if(ball.getOwner().isAlwaysTicking()) {
											Player owner = (Player)ball.getOwner();
											if(!owner.isCreative()) {
												ball.getItem().hurtAndBreak(1, owner, owner.getEquipmentSlotForItem(ball.getItem()));
												if(!owner.addItem(ball.getItem())) {
													ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
												}
												world.broadcastEntityEvent(ball, EntityEvent.DEATH);
												ball.discard();
											}
										}
									}
								}
							}
						})
						.build(),
				new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(100)
		);
		MEATBALL_ITEM = itemRegistry.register("meatball", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.3f).build()));
		COOKED_MEATBALL_ITEM = itemRegistry.register("cooked_meatball", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.0f).build()));
		BOUNCY_BALL_ITEM = itemRegistry.register("bouncy_ball", new IceballItem.Builder()
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
												ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
											}
										}
									} else {
										ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
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
												ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
											}
										}
									} else {
										ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
									}
								}
								return true;
							}
							return false;
						})
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		BIG_BOUNCY_BALL_ITEM = itemRegistry.register("big_bouncy_ball", BigBouncyBallItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
		LIGHTNING_BALL_ITEM = itemRegistry.register("lightning_ball", new IceballItem.Builder()
						.damage(1)
						.cooldown(0)
						.onCollide((ball, hitResult) -> {
							if(ball.level() instanceof ServerLevel world) {
								if(ball.getOwner() != null) {
									if(ball.getOwner().isAlwaysTicking()) {
										Player owner = (Player)ball.getOwner();
										if(!owner.isCreative()) {
											ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
										}
									}
								} else {
									ball.spawnAtLocation(/*? if >= 1.21.11 >>+ ' '*/world, ball.getItem());
								}
							}
							return true;
						})
						.build(),
				new Item.Properties().stacksTo(16).rarity(Rarity.COMMON)
		);
		CHARGED_LIGHTNING_BALL_ITEM = itemRegistry.register("charged_lightning_ball", new IceballItem.Builder()
						.damage(0)
						.cooldown(5)
						.onCollide((ball, hitResult) -> {
							//? if >= 26.2 {
							LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, ball.level());
							//?} else
							//LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, ball.level());
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
				BigBouncyBallEntity bigBouncyBallEntity = IceballEntities.BIG_BOUNCY_BALL_ENTITY.spawn(serverWorld, EntityType.appendCustomEntityStackConfig(bigBouncyBall -> bigBouncyBall.setYRot(direction.toYRot()), serverWorld, dispensed, null), blockPos, EntitySpawnReason.DISPENSER, false, false);
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
	
	public static void initClient(ItemRegistry itemRegistry) {
		itemRegistry.registerClient(ICEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(Items.SNOWBALL), new ItemRegistry.ItemGroupData(CreativeModeTabs.INGREDIENTS).after(Items.SNOWBALL));
		itemRegistry.registerClient(PACKED_ICEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(ICEBALL_ITEM), new ItemRegistry.ItemGroupData(CreativeModeTabs.INGREDIENTS).after(ICEBALL_ITEM));
		itemRegistry.registerClient(BLUE_ICEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(PACKED_ICEBALL_ITEM), new ItemRegistry.ItemGroupData(CreativeModeTabs.INGREDIENTS).after(PACKED_ICEBALL_ITEM));
		itemRegistry.registerClient(BOOMBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(BLUE_ICEBALL_ITEM));
		itemRegistry.registerClient(LIGHTNING_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(BOOMBALL_ITEM));
		itemRegistry.registerClient(CHARGED_LIGHTNING_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(LIGHTNING_BALL_ITEM));
		itemRegistry.registerClient(SPIKEBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(CHARGED_LIGHTNING_BALL_ITEM));
		itemRegistry.registerClient(MEATBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.FOOD_AND_DRINKS).after(Items.COOKED_RABBIT));
		itemRegistry.registerClient(COOKED_MEATBALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.FOOD_AND_DRINKS).after(MEATBALL_ITEM));
		itemRegistry.registerClient(BOUNCY_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.COMBAT).after(SPIKEBALL_ITEM));
		itemRegistry.registerClient(BIG_BOUNCY_BALL_ITEM, new ItemRegistry.ItemGroupData(CreativeModeTabs.TOOLS_AND_UTILITIES).after(Items.BAMBOO_CHEST_RAFT));
	}
}
