package net.grilledham.iceball.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.grilledham.iceball.entity.IceballEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

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
	private final List<RegistryKey<Enchantment>> primaryEnchants;
	private final List<RegistryKey<Enchantment>> acceptableEnchants;
	
	IceballItem(Item.Settings settings, int damage, int cooldown, BiFunction<IceballEntity, HitResult, Boolean> onCollide, Consumer<IceballEntity> onTick, List<RegistryKey<Enchantment>> primaryEnchants, List<RegistryKey<Enchantment>> acceptableEnchants) {
		super(settings);
		this.damage = damage;
		this.cooldown = cooldown;
		this.onCollide = onCollide;
		this.onTick = onTick;
		this.primaryEnchants = primaryEnchants;
		this.acceptableEnchants = acceptableEnchants;
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		user.getItemCooldownManager().set(itemStack, cooldown);
		if (!world.isClient()) {
			IceballEntity iceballEntity = new IceballEntity(world, user, itemStack, damage, onCollide, onTick);
			iceballEntity.setItem(itemStack);
			iceballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
			world.spawnEntity(iceballEntity);
		}
		
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}
		
		return ActionResult.SUCCESS;
	}
	
	@Override
	public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
		switch(context) {
			case PRIMARY -> {
				for(RegistryKey<Enchantment> key : primaryEnchants) {
					if(enchantment.matchesKey(key)) {
						return true;
					}
				}
				return super.canBeEnchantedWith(stack, enchantment, context);
			}
			case ACCEPTABLE -> {
				for(RegistryKey<Enchantment> key : acceptableEnchants) {
					if(enchantment.matchesKey(key)) {
						return true;
					}
				}
				return super.canBeEnchantedWith(stack, enchantment, context);
			}
		}
		return super.canBeEnchantedWith(stack, enchantment, context);
	}
	
	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
		IceballEntity iceballEntity = new IceballEntity(world, pos, stack, damage, onCollide, onTick);
		iceballEntity.setItem(stack);
		return iceballEntity;
	}
	
	public static class Builder {
		private int damage = 1;
		private int cooldown = 0;
		private BiFunction<IceballEntity, HitResult, Boolean> onCollide = (ball, hitResult) -> true;
		private Consumer<IceballEntity> onTick = (ball) -> {};
		private List<RegistryKey<Enchantment>> primaryEnchants = new ArrayList<>();
		private List<RegistryKey<Enchantment>> acceptableEnchants = new ArrayList<>();
		
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
		public final Builder primaryEnchants(RegistryKey<Enchantment>... enchants) {
			this.primaryEnchants = Arrays.asList(enchants);
			return this;
		}
		
		@SafeVarargs
		public final Builder acceptableEnchants(RegistryKey<Enchantment>... enchants) {
			this.acceptableEnchants = Arrays.asList(enchants);
			return this;
		}
		
		public Function<Item.Settings, IceballItem> build() {
			return settings -> new IceballItem(settings, damage, cooldown, onCollide, onTick, primaryEnchants, acceptableEnchants);
		}
	}
}
