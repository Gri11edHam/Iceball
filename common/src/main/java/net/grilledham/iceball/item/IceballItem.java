package net.grilledham.iceball.item;

import net.grilledham.iceball.entity.IceballEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
//? if >= 1.21.11 {
import net.minecraft.world.InteractionResult;
//?} else
//import net.minecraft.world.InteractionResultHolder;
//? if >= 1.21.11
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class IceballItem extends Item implements ProjectileItem {
	
	private final EntityType<IceballEntity> entityType;
	private final int cooldown;
	
	IceballItem(Item.Properties settings, EntityType<IceballEntity> entityType, int cooldown) {
		super(settings);
		this.entityType = entityType;
		this.cooldown = cooldown;
	}
	
	@Override
	//? if >= 1.21.11 {
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
	//?} else
	//public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		user.getCooldowns().addCooldown(itemStack/*? if < 1.21.11 >> ','*//*.getItem()*/, cooldown);
		if (!world.isClientSide()) {
			IceballEntity iceballEntity = entityType.create(world/*? if >= 1.21.11 >> ')'*/, EntitySpawnReason.TRIGGERED);
			iceballEntity.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
			iceballEntity.setItem(itemStack);
			iceballEntity.setOwner(user);
			iceballEntity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.5F, 1.0F);
			world.addFreshEntity(iceballEntity);
		}
		
		user.awardStat(Stats.ITEM_USED.get(this));
		if (!user.isCreative()) {
			itemStack.shrink(1);
		}
		
		//? if >= 1.21.11 {
		return InteractionResult.SUCCESS;
		//?} else
		//return InteractionResultHolder.success(itemStack);
	}
	
	@Override
	public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
		IceballEntity iceballEntity = entityType.create(world/*? if >= 1.21.11 >> ')'*/, EntitySpawnReason.TRIGGERED);
		iceballEntity.setPos(pos.x(), pos.y(), pos.z());
		iceballEntity.setItem(stack);
		return iceballEntity;
	}
	
	public static class Builder {
		private final EntityType<IceballEntity> entityType;
		private int cooldown = 0;
		
		public Builder(EntityType<IceballEntity> entityType) {
			this.entityType = entityType;
		}
		
		public Builder cooldown(int cooldown) {
			this.cooldown = cooldown;
			return this;
		}
		
		public Function<Properties, IceballItem> build() {
			return settings -> new IceballItem(settings, entityType, cooldown);
		}
	}
}
