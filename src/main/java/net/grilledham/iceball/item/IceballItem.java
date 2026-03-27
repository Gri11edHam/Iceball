package net.grilledham.iceball.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.grilledham.iceball.entity.IceballEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class IceballItem extends Item implements ProjectileItem {
	
	private final int damage;
	private final int cooldown;
	private final BiFunction<IceballEntity, HitResult, Boolean> onCollide;
	private final Consumer<IceballEntity> onTick;
	private final List<ResourceKey<Enchantment>> primaryEnchants;
	private final List<ResourceKey<Enchantment>> acceptableEnchants;
	
	IceballItem(Item.Properties settings, int damage, int cooldown, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick, List<ResourceKey<Enchantment>> primaryEnchants, List<ResourceKey<Enchantment>> acceptableEnchants) {
		super(settings);
		this.damage = damage;
		this.cooldown = cooldown;
		this.onCollide = onCollide;
		this.onTick = onTick;
		this.primaryEnchants = primaryEnchants;
		this.acceptableEnchants = acceptableEnchants;
	}
	
	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		user.getCooldowns().addCooldown(itemStack, cooldown);
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
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
		switch(context) {
			case PRIMARY -> {
				for(ResourceKey<Enchantment> key : primaryEnchants) {
					if(enchantment.is(key)) {
						return true;
					}
				}
				return super.canBeEnchantedWith(stack, enchantment, context);
			}
			case ACCEPTABLE -> {
				for(ResourceKey<Enchantment> key : acceptableEnchants) {
					if(enchantment.is(key)) {
						return true;
					}
				}
				return super.canBeEnchantedWith(stack, enchantment, context);
			}
		}
		return super.canBeEnchantedWith(stack, enchantment, context);
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
		private List<ResourceKey<Enchantment>> primaryEnchants = new ArrayList<>();
		private List<ResourceKey<Enchantment>> acceptableEnchants = new ArrayList<>();
		
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
		
		@SafeVarargs
		public final Builder primaryEnchants(ResourceKey<Enchantment>... enchants) {
			this.primaryEnchants = Arrays.asList(enchants);
			return this;
		}
		
		@SafeVarargs
		public final Builder acceptableEnchants(ResourceKey<Enchantment>... enchants) {
			this.acceptableEnchants = Arrays.asList(enchants);
			return this;
		}
		
		public Function<Item.Properties, IceballItem> build() {
			return settings -> new IceballItem(settings, damage, cooldown, onCollide, onTick, primaryEnchants, acceptableEnchants);
		}
	}
}
