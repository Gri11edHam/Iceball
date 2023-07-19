package net.grilledham.iceball.item;

import net.grilledham.iceball.entity.IceballEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class IceballItem extends Item {
	
	private final int damage;
	private final int cooldown;
	private final Consumer<IceballEntity> onCollide;
	
	public IceballItem(Settings settings, int damage, int cooldown) {
		this(settings, damage, cooldown, ball -> {});
	}
	
	public IceballItem(Settings settings, int damage, int cooldown, Consumer<IceballEntity> onCollide) {
		super(settings);
		this.damage = damage;
		this.cooldown = cooldown;
		this.onCollide = onCollide;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		user.getItemCooldownManager().set(this, cooldown);
		if (!world.isClient) {
			IceballEntity iceballEntity = new IceballEntity(world, user, damage, onCollide);
			iceballEntity.setItem(itemStack);
			iceballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
			world.spawnEntity(iceballEntity);
		}
		
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}
		
		return TypedActionResult.success(itemStack, world.isClient());
	}
}
