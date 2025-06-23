package net.grilledham.iceball.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class IceballEntity extends ThrownItemEntity {
	
	private final int damage;
	private final BiFunction<IceballEntity, HitResult, Boolean> onCollide;
	private final Consumer<IceballEntity> onTick;
	public boolean shouldDamageOwner = true;
	
	public IceballEntity(World world, LivingEntity owner, ItemStack stack, int damage, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick) {
		super(EntityType.SNOWBALL, owner, world, stack);
		this.damage = damage;
		this.onCollide = onCollide;
		this.onTick = onTick;
	}
	
	public IceballEntity(World world, Position pos, ItemStack stack, int damage, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick) {
		super(EntityType.SNOWBALL, pos.getX(), pos.getY(), pos.getZ(), world, stack);
		this.damage = damage;
		this.onCollide = onCollide;
		this.onTick = onTick;
	}
	
	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}
	
	private ParticleEffect getParticleParameters() {
		ItemStack itemStack = this.getStack();
		return itemStack.isEmpty() || itemStack.isOf(this.getDefaultItem()) ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack);
	}
	
	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
			ParticleEffect particleEffect = this.getParticleParameters();
			for (int i = 0; i < 8; ++i) {
				this.getWorld().addParticleClient(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		onTick.accept(this);
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		if(!shouldDamageOwner && isOwner(entity)) {
			return;
		}
		int i = entity instanceof BlazeEntity ? damage + 3 : damage;
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			entity.damage(serverWorld, entity.getWorld().getDamageSources().thrown(this, this.getOwner()), (float)i);
		}
	}
	
	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if(onCollide.apply(this, hitResult)) {
			if (!this.getWorld().isClient) {
				this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
				this.discard();
			}
		}
	}
}
