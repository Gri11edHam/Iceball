package net.grilledham.iceball.entity;

import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class IceballEntity extends SnowballEntity {
	
	private final int damage;
	
	public IceballEntity(World world, LivingEntity owner, int damage) {
		super(world, owner);
		this.damage = damage;
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
}
