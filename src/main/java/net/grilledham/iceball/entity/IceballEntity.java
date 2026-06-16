package net.grilledham.iceball.entity;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class IceballEntity extends ThrowableItemProjectile {
	
	private final int damage;
	private final BiFunction<IceballEntity, HitResult, Boolean> onCollide;
	private final Consumer<IceballEntity> onTick;
	public boolean shouldDamageOwner = true;
	
	public IceballEntity(Level world, LivingEntity owner, ItemStack stack, int damage, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick) {
		super(EntityTypes.SNOWBALL, owner, world, stack);
		this.damage = damage;
		this.onCollide = onCollide;
		this.onTick = onTick;
	}
	
	public IceballEntity(Level world, Position pos, ItemStack stack, int damage, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick) {
		super(EntityTypes.SNOWBALL, pos.x(), pos.y(), pos.z(), world, stack);
		this.damage = damage;
		this.onCollide = onCollide;
		this.onTick = onTick;
	}
	
	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}
	
	private ParticleOptions getParticleParameters() {
		ItemStack itemStack = this.getItem();
		return itemStack.isEmpty() || itemStack.is(this.getDefaultItem()) ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(itemStack));
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
	}
	
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		if(!shouldDamageOwner && ownedBy(entity)) {
			return;
		}
		int i = entity instanceof Blaze ? damage + 3 : damage;
		if (this.level() instanceof ServerLevel serverWorld) {
			entity.hurtServer(serverWorld, entity.level().damageSources().thrown(this, this.getOwner()), (float)i);
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
}
