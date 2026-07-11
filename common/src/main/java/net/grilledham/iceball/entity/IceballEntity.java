package net.grilledham.iceball.entity;

import net.grilledham.iceball.registry.IceballItems;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile./*? if >= 1.21.11 >>+ '.'*/throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >= 26.1
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class IceballEntity extends ThrowableItemProjectile {
	
	private final int damage;
	private final BiFunction<IceballEntity, HitResult, Boolean> onCollide;
	private final Consumer<IceballEntity> onTick;
	private final ParticleOptions trailParticle;
	private final Vec3 trailVelocity;
	public boolean shouldDamageOwner = true;
	
	public IceballEntity(EntityType<IceballEntity> entityType, Level world, LivingEntity owner, ItemStack stack, int damage, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick, ParticleOptions trailParticle, Vec3 trailVelocity) {
		//? if >= 26.2 {
		super(entityType, owner, world, stack);
		//?} else
		//super(entityType, owner, world/*? if >= 1.21.11 >> ')'*/, stack);
		this.damage = damage;
		this.onCollide = onCollide;
		this.onTick = onTick;
		this.trailParticle = trailParticle;
		this.trailVelocity = trailVelocity;
	}
	
	public IceballEntity(EntityType<IceballEntity> entityType, Level world, Position pos, ItemStack stack, int damage, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick, ParticleOptions trailParticle, Vec3 trailVelocity) {
		//? if >= 26.2 {
		super(entityType, pos.x(), pos.y(), pos.z(), world, stack);
		//?} else
		//super(entityType, pos.x(), pos.y(), pos.z(), world/*? if >= 1.21.11 >> ')'*/, stack);
		this.damage = damage;
		this.onCollide = onCollide;
		this.onTick = onTick;
		this.trailParticle = trailParticle;
		this.trailVelocity = trailVelocity;
	}
	
	@Override
	protected Item getDefaultItem() {
		return IceballItems.ICEBALL_ITEM;
	}
	
	private ParticleOptions getParticleParameters() {
		ItemStack itemStack = this.getItem();
		//? if >= 26.1 {
		return itemStack.isEmpty() || itemStack.is(this.getDefaultItem()) ? new ItemParticleOption(ParticleTypes.ITEM, getDefaultItem()) : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(itemStack));
		//?} else
		//return itemStack.isEmpty() || itemStack.is(this.getDefaultItem()) ? new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(getDefaultItem())) : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
	}
	
	@Override
	public void handleEntityEvent(byte status) {
		if (status == EntityEvent.DEATH) {
			ParticleOptions particleEffect = this.getParticleParameters();
			for (int i = 0; i < 8; ++i) {
				this.level().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		onTick.accept(this);
		if(trailParticle != null) {
			double dx = this.getDeltaMovement().x();
			double dy = this.getDeltaMovement().y();
			double dz = this.getDeltaMovement().z();
			for(int i = 0; i < 4; i++) {
				this.level().addParticle(trailParticle, this.getX() - dx * i * 0.25, this.getY() - dy * i * 0.25, this.getZ() - dz * i * 0.25, trailVelocity.x(), trailVelocity.y(), trailVelocity.z());
			}
		}
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		if(!shouldDamageOwner && ownedBy(entity)) {
			return;
		}
		int i = entity instanceof Blaze ? damage + 3 : damage;
		if (this.level() instanceof ServerLevel serverWorld) {
			//? if >= 1.21.11 {
			entity.hurtServer(serverWorld, serverWorld.damageSources().thrown(this, this.getOwner()), (float)i);
			//?} else
			//entity.hurt(serverWorld.damageSources().thrown(this, this.getOwner()), (float)i);
		}
	}
	
	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if(onCollide.apply(this, hitResult)) {
			if (!this.level().isClientSide()) {
				this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
				this.discard();
			}
		}
	}
	
	public static class Builder {
		private int damage = 1;
		private BiFunction<IceballEntity, HitResult, Boolean> onCollide = (ball, hitResult) -> true;
		private Consumer<IceballEntity> onTick = (ball) -> {};
		private ParticleOptions trailParticle = null;
		private Vec3 trailVelocity = new Vec3(0, 0, 0);
		
		public Builder damage(int damage) {
			this.damage = damage;
			return this;
		}
		
		public Builder onCollide(BiFunction<IceballEntity, HitResult, Boolean> onCollide) {
			this.onCollide = onCollide;
			return this;
		}
		
		public Builder onTick(Consumer<IceballEntity> onTick) {
			this.onTick = onTick;
			return this;
		}
		
		public Builder trail(ParticleOptions trailParticle, Vec3 trailVelocity) {
			this.trailParticle = trailParticle;
			this.trailVelocity = trailVelocity;
			return this;
		}
		
		public Builder trail(ParticleOptions trailParticle) {
			return this.trail(trailParticle, this.trailVelocity);
		}
		
		public EntityType.EntityFactory<IceballEntity> build() {
			return (entityType, level) -> new IceballEntity(entityType, level, new Vec3(0, 0, 0), ItemStack.EMPTY, damage, onCollide, onTick, trailParticle, trailVelocity);
		}
	}
}
