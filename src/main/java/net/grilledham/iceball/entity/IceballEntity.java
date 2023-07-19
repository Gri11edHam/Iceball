package net.grilledham.iceball.entity;

import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class IceballEntity extends SnowballEntity {
	
	private final int damage;
	private final Consumer<IceballEntity> onCollide;
	
	public IceballEntity(World world, LivingEntity owner, int damage, Consumer<IceballEntity> onCollide) {
		super(world, owner);
		this.damage = damage;
		this.onCollide = onCollide;
	}
	
	@Override
	protected Item getDefaultItem() {
		return ItemRegistry.ICEBALL_ITEM;
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		int i = entity instanceof BlazeEntity ? damage + 3 : damage;
		entity.damage(entity.getWorld().getDamageSources().thrown(this, this.getOwner()), (float)i);
	}
	
	@Override
	protected void onCollision(HitResult hitResult) {
		onCollide.accept(this);
		super.onCollision(hitResult);
	}
}
