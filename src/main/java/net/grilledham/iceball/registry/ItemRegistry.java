package net.grilledham.iceball.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.item.BigBouncyBallItem;
import net.grilledham.iceball.item.IceballItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
					ball.dropStack(ball.getStack());
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
	public static final Item MEATBALL_ITEM = new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(5).saturationModifier(0.3f).build()));
	public static final Item COOKED_MEATBALL_ITEM = new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(10).saturationModifier(1.0f).build()));
	public static final IceballItem BOUNCY_BALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.COMMON))
			.damage(1)
			.cooldown(0)
			.onCollide((ball, hitResult) -> {
				if(hitResult instanceof BlockHitResult bhr) {
					Vec3d velocity = ball.getVelocity().add(ball.getVelocity().multiply(new Vec3d(bhr.getSide().getUnitVector().absolute().mul(-1.7f))));
					ball.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ(), (float)(ball.getVelocity().length() * 0.8), 0);
				} else if(hitResult instanceof EntityHitResult) {
					if(ball.getOwner() != null) {
						if(ball.getOwner().isPlayer()) {
							PlayerEntity owner = (PlayerEntity)ball.getOwner();
							if(!owner.isInCreativeMode()) {
								ball.dropStack(ball.getStack());
							}
						}
					} else {
						ball.dropStack(ball.getStack());
					}
					return true;
				}
				if(ball.getVelocity().length() < 0.1) {
					if(ball.getOwner() != null) {
						if(ball.getOwner().isPlayer()) {
							PlayerEntity owner = (PlayerEntity)ball.getOwner();
							if(!owner.isInCreativeMode()) {
								ball.dropStack(ball.getStack());
							}
						}
					} else {
						ball.dropStack(ball.getStack());
					}
					return true;
				}
				return false;
			})
			.build();
	public static final BigBouncyBallItem BIG_BOUNCY_BALL_ITEM = new BigBouncyBallItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
	public static final IceballItem LIGHTNING_BALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.COMMON))
			.damage(1)
			.cooldown(0)
			.onCollide((ball, hitResult) -> {
				if(ball.getOwner() != null) {
					if(ball.getOwner().isPlayer()) {
						PlayerEntity owner = (PlayerEntity)ball.getOwner();
						if(!owner.isInCreativeMode()) {
							ball.dropStack(ball.getStack());
						}
					}
				} else {
					ball.dropStack(ball.getStack());
				}
				return true;
			})
			.build();
	public static final IceballItem CHARGED_LIGHTNING_BALL_ITEM = new IceballItem.Builder()
			.settings(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON))
			.damage(0)
			.cooldown(5)
			.onCollide((ball, hitResult) -> {
				LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, ball.getWorld());
				lightning.setPosition(ball.getPos());
				ball.getWorld().spawnEntity(lightning);
				if(!ball.getWorld().isClient) {
					if(ball.getOwner() != null) {
						if(ball.getOwner().isPlayer()) {
							PlayerEntity owner = (PlayerEntity)ball.getOwner();
							if(!owner.isInCreativeMode()) {
								ItemEntity itemEntity = new ItemEntity(ball.getWorld(), ball.getX(), ball.getY(), ball.getZ(), ball.getStack().withItem(LIGHTNING_BALL_ITEM));
								itemEntity.setInvulnerable(true);
								itemEntity.setToDefaultPickupDelay();
								ball.getWorld().spawnEntity(itemEntity);
							}
						}
					} else {
						ItemEntity itemEntity = new ItemEntity(ball.getWorld(), ball.getX(), ball.getY(), ball.getZ(), ball.getStack().withItem(LIGHTNING_BALL_ITEM));
						itemEntity.setInvulnerable(true);
						itemEntity.setToDefaultPickupDelay();
						ball.getWorld().spawnEntity(itemEntity);
					}
				}
				return true;
			})
			.build();
	
	public static void init() {
		register("iceball", ICEBALL_ITEM);
		register("packed_iceball", PACKED_ICEBALL_ITEM);
		register("blue_iceball", BLUE_ICEBALL_ITEM);
		register("boomball", BOOMBALL_ITEM);
		register("spikeball", SPIKEBALL_ITEM);
		register("meatball", MEATBALL_ITEM);
		register("cooked_meatball", COOKED_MEATBALL_ITEM);
		register("bouncy_ball", BOUNCY_BALL_ITEM);
		register("big_bouncy_ball", BIG_BOUNCY_BALL_ITEM);
		register("lightning_ball", LIGHTNING_BALL_ITEM);
		register("charged_lightning_ball", CHARGED_LIGHTNING_BALL_ITEM);
		
		DispenserBlock.registerProjectileBehavior(ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(PACKED_ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BLUE_ICEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BOOMBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(SPIKEBALL_ITEM);
		DispenserBlock.registerProjectileBehavior(BOUNCY_BALL_ITEM);
		DispenserBlock.registerBehavior(BIG_BOUNCY_BALL_ITEM, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.state().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.pos().offset(direction);
				ServerWorld serverWorld = pointer.world();
				BigBouncyBallEntity bigBouncyBallEntity = EntityRegistry.BIG_BOUNCY_BALL_ENTITY.spawn(serverWorld, EntityType.copier(bigBouncyBall -> bigBouncyBall.setYaw(direction.asRotation()), serverWorld, stack, null), blockPos, SpawnReason.DISPENSER, false, false);
				if(bigBouncyBallEntity != null) {
					bigBouncyBallEntity.setBallColor(DyedColorComponent.getColor(stack, 0xFF88DD88));
					stack.decrement(1);
				}
				return stack;
			}
		});
		DispenserBlock.registerProjectileBehavior(LIGHTNING_BALL_ITEM);
		DispenserBlock.registerProjectileBehavior(CHARGED_LIGHTNING_BALL_ITEM);
	}
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		registerClient(ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(Items.SNOWBALL), new ItemGroupData(ItemGroups.INGREDIENTS).after(Items.SNOWBALL));
		registerClient(PACKED_ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(ICEBALL_ITEM), new ItemGroupData(ItemGroups.INGREDIENTS).after(ICEBALL_ITEM));
		registerClient(BLUE_ICEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(PACKED_ICEBALL_ITEM), new ItemGroupData(ItemGroups.INGREDIENTS).after(PACKED_ICEBALL_ITEM));
		registerClient(BOOMBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(BLUE_ICEBALL_ITEM));
		registerClient(LIGHTNING_BALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(BOOMBALL_ITEM));
		registerClient(CHARGED_LIGHTNING_BALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(LIGHTNING_BALL_ITEM));
		registerClient(SPIKEBALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(CHARGED_LIGHTNING_BALL_ITEM));
		registerClient(MEATBALL_ITEM, new ItemGroupData(ItemGroups.FOOD_AND_DRINK).after(Items.COOKED_RABBIT));
		registerClient(COOKED_MEATBALL_ITEM, new ItemGroupData(ItemGroups.FOOD_AND_DRINK).after(MEATBALL_ITEM));
		registerClient(BOUNCY_BALL_ITEM, new ItemGroupData(ItemGroups.COMBAT).after(SPIKEBALL_ITEM));
		registerClient(BIG_BOUNCY_BALL_ITEM, new ItemGroupData(ItemGroups.TOOLS).after(Items.BAMBOO_CHEST_RAFT));
		
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> DyedColorComponent.getColor(stack, 0xFF88DD88), BOUNCY_BALL_ITEM);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> DyedColorComponent.getColor(stack, 0xFF88DD88), BIG_BOUNCY_BALL_ITEM);
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
