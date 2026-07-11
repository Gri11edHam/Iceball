//~ identifier
package net.grilledham.iceball.registry;

import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.client.entity.renderer.BigBouncyBallRenderer;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.entity.IceballEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
//? if >= 21.1
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class IceballEntities {
	
	public static EntityType<IceballEntity> ICEBALL_ENTITY;
	public static EntityType<IceballEntity> PACKED_ICEBALL_ENTITY;
	public static EntityType<IceballEntity> BLUE_ICEBALL_ENTITY;
	public static EntityType<IceballEntity> BOOMBALL_ENTITY;
	public static EntityType<IceballEntity> SPIKEBALL_ENTITY;
	public static EntityType<IceballEntity> BOUNCY_BALL_ENTITY;
	public static EntityType<BigBouncyBallEntity> BIG_BOUNCY_BALL_ENTITY;
	public static EntityType<IceballEntity> LIGHTNING_BALL_ENTITY;
	public static EntityType<IceballEntity> CHARGED_LIGHTNING_BALL_ENTITY;
	public static EntityType<IceballEntity> FIREBALL_ENTITY;
	public static EntityType<IceballEntity> FLAMING_FIREBALL_ENTITY;
	
	public static void init(EntityRegistry entityRegistry) {
		ICEBALL_ENTITY = entityRegistry.register("iceball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(1)
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		PACKED_ICEBALL_ENTITY = entityRegistry.register("packed_iceball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(5)
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		BLUE_ICEBALL_ENTITY = entityRegistry.register("blue_iceball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(10)
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		BOOMBALL_ENTITY = entityRegistry.register("boomball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(0)
								.onCollide((ball, hitResult) -> {
									ball.level().explode(ball, ball.damageSources().explosion(ball, ball.getOwner()), new ExplosionDamageCalculator(), ball.position(), 8, false, Level.ExplosionInteraction.MOB);
									return true;
								})
								.trail(ParticleTypes.FLAME)
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		SPIKEBALL_ENTITY = entityRegistry.register("spikeball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(30)
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
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		BOUNCY_BALL_ENTITY = entityRegistry.register("bouncy_ball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(1)
								.onCollide((ball, hitResult) -> {
									if(hitResult instanceof BlockHitResult bhr) {
										Vec3 velocity = ball.getDeltaMovement().add(ball.getDeltaMovement().multiply(new Vec3(bhr.getDirection().step().absolute().mul(-1.7f))));
										ball.shoot(velocity.x(), velocity.y(), velocity.z(), (float)(ball.getDeltaMovement().length() * 0.8), 0);
										//? if >= 26.1 {
										ParticleOptions particleEffect = new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(ball.getItem()));
										//?} else
										//ParticleOptions particleEffect = new ItemParticleOption(ParticleTypes.ITEM, ball.getItem());
										for (int i = 0; i < 8; ++i) {
											ball.level().addParticle(particleEffect, ball.getX(), ball.getY(), ball.getZ(), 0.0, 0.0, 0.0);
										}
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
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		BIG_BOUNCY_BALL_ENTITY = entityRegistry.register(
				"big_bouncy_ball",
				EntityType.Builder.of((EntityType.EntityFactory<BigBouncyBallEntity>)BigBouncyBallEntity::new, MobCategory.MISC)
						.sized(1.75f, 1.75f)
						.eyeHeight(0.875f)
						.clientTrackingRange(10)
		);
		LIGHTNING_BALL_ENTITY = entityRegistry.register("lightning_ball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(1)
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
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		CHARGED_LIGHTNING_BALL_ENTITY = entityRegistry.register("charged_lightning_ball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(0)
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
													ItemEntity itemEntity = new ItemEntity(ball.level(), ball.getX(), ball.getY(), ball.getZ(), ball.getItem().transmuteCopy(IceballItems.LIGHTNING_BALL_ITEM));
													itemEntity.setInvulnerable(true);
													itemEntity.setDefaultPickUpDelay();
													ball.level().addFreshEntity(itemEntity);
												}
											}
										} else {
											ItemEntity itemEntity = new ItemEntity(ball.level(), ball.getX(), ball.getY(), ball.getZ(), ball.getItem().transmuteCopy(IceballItems.LIGHTNING_BALL_ITEM));
											itemEntity.setInvulnerable(true);
											itemEntity.setDefaultPickUpDelay();
											ball.level().addFreshEntity(itemEntity);
										}
									}
									return true;
								})
								.trail(ParticleTypes.ELECTRIC_SPARK)
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		FIREBALL_ENTITY = entityRegistry.register("fireball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(1)
								.onCollide((ball, hitResult) -> {
									if(ball.level() instanceof ServerLevel world) {
										if(hitResult instanceof EntityHitResult ehr) {
											ehr.getEntity().igniteForSeconds(2);
										}
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
								.onTick(ball -> {
									if(ball.isOnFire()) {
										ball.setItem(ball.getItem().transmuteCopy(IceballItems.FLAMING_FIREBALL_ITEM));
										ball.clearFire();
									}
								})
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
		FLAMING_FIREBALL_ENTITY = entityRegistry.register("flaming_fireball", EntityType.Builder.of(new IceballEntity.Builder()
								.damage(0)
								.onCollide((ball, hitResult) -> {
									if(ball.level() instanceof ServerLevel world) {
										world.getEntities(ball, ball.getBoundingBox().inflate(8))
												.stream()
												.filter(entity -> !entity.fireImmune() && entity.distanceTo(ball) < 8)
												.forEach(entity -> entity.igniteForSeconds(20));
										world.explode(ball, ball.damageSources().explosion(
														ball,
														ball.getOwner()),
												new SimpleExplosionDamageCalculator(true, false, Optional.of(0.0F), Optional.empty()),
												ball.position(),
												8,
												true,
												Level.ExplosionInteraction.NONE);
										if(ball.getOwner() != null) {
											if(ball.getOwner().isAlwaysTicking()) {
												Player owner = (Player)ball.getOwner();
												if(!owner.isCreative()) {
													ItemEntity itemEntity = new ItemEntity(ball.level(), ball.getX(), ball.getY(), ball.getZ(), ball.getItem().transmuteCopy(IceballItems.FIREBALL_ITEM));
													itemEntity.setInvulnerable(true);
													itemEntity.setDefaultPickUpDelay();
													world.addFreshEntity(itemEntity);
												}
											}
										} else {
											ItemEntity itemEntity = new ItemEntity(ball.level(), ball.getX(), ball.getY(), ball.getZ(), ball.getItem().transmuteCopy(IceballItems.FIREBALL_ITEM));
											itemEntity.setInvulnerable(true);
											itemEntity.setDefaultPickUpDelay();
											world.addFreshEntity(itemEntity);
										}
									}
									return true;
								})
								.trail(ParticleTypes.FLAME)
								.build(),
						MobCategory.MISC)
				.sized(0.25f, 0.25f)
				.updateInterval(20)
				.clientTrackingRange(4)
		);
	}
	
	public static ModelLayerLocation BIG_BOUNCY_BALL_MODEL_LAYER;
	
	public static void initClient(EntityRegistry entityRegistry) {
		BIG_BOUNCY_BALL_MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath("iceball", "big_bouncy_ball"), "main");
		
		EntityRenderers.register(ICEBALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(PACKED_ICEBALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(BLUE_ICEBALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(BOOMBALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(SPIKEBALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(BOUNCY_BALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(BIG_BOUNCY_BALL_ENTITY, BigBouncyBallRenderer::new);
		EntityRenderers.register(LIGHTNING_BALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(CHARGED_LIGHTNING_BALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(FIREBALL_ENTITY, ThrownItemRenderer::new);
		EntityRenderers.register(FLAMING_FIREBALL_ENTITY, ThrownItemRenderer::new);
		
		entityRegistry.registerModelLayer(BIG_BOUNCY_BALL_MODEL_LAYER, BigBouncyBallModel::getTexturedModelData);
	}
}
