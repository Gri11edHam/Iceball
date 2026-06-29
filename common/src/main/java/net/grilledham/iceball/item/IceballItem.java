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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class IceballItem extends Item implements ProjectileItem {
	
	private final int damage;
	private final int cooldown;
	private final BiFunction<IceballEntity, HitResult, Boolean> onCollide;
	private final Consumer<IceballEntity> onTick;
	
	IceballItem(Item.Properties settings, int damage, int cooldown, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick) {
		super(settings);
		this.damage = damage;
		this.cooldown = cooldown;
		this.onCollide = onCollide;
		this.onTick = onTick;
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
			IceballEntity iceballEntity = new IceballEntity(world, user, itemStack, damage, onCollide, onTick);
			iceballEntity.setItem(itemStack);
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
		IceballEntity iceballEntity = new IceballEntity(world, pos, stack, damage, onCollide, onTick);
		iceballEntity.setItem(stack);
		return iceballEntity;
	}
	
	public static class Builder {
		private int damage = 1;
		private int cooldown = 0;
		private BiFunction<IceballEntity, HitResult, Boolean> onCollide = (ball, hitResult) -> true;
		private Consumer<IceballEntity> onTick = (ball) -> {};
		
		public Builder damage(int damage) {
			this.damage = damage;
			return this;
		}
		
		public Builder cooldown(int cooldown) {
			this.cooldown = cooldown;
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
		
		public Function<Item.Properties, IceballItem> build() {
			return settings -> new IceballItem(settings, damage, cooldown, onCollide, onTick);
		}
	}
}
