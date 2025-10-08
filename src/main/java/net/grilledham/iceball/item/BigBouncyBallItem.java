package net.grilledham.iceball.item;

import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.function.Predicate;

public class BigBouncyBallItem extends Item {
	
	private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::canHit);

	public BigBouncyBallItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult hitResult = BoatItem.raycast(world, user, RaycastContext.FluidHandling.ANY);
		if (((HitResult)hitResult).getType() == HitResult.Type.MISS) {
			return ActionResult.PASS;
		}
		Vec3d vec3d = user.getRotationVec(1.0f);
		List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().stretch(vec3d.multiply(5.0)).expand(1.0), RIDERS);
		if (!list.isEmpty()) {
			Vec3d vec3d2 = user.getEyePos();
			for (Entity entity : list) {
				Box box = entity.getBoundingBox().expand(entity.getTargetingMargin());
				if (!box.contains(vec3d2)) continue;
				return ActionResult.PASS;
			}
		}
		if (((HitResult)hitResult).getType() == HitResult.Type.BLOCK) {
			BigBouncyBallEntity bigBall = this.createEntity(world, hitResult, itemStack, user);
			bigBall.setYaw(user.getYaw());
			bigBall.setBallColor(DyedColorComponent.getColor(itemStack, 0xFF88DD88));
			if (!world.isSpaceEmpty(bigBall, bigBall.getBoundingBox())) {
				return ActionResult.FAIL;
			}
			if (!world.isClient()) {
				world.spawnEntity(bigBall);
				world.emitGameEvent(user, GameEvent.ENTITY_PLACE, hitResult.getPos());
				itemStack.decrementUnlessCreative(1, user);
			}
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	private BigBouncyBallEntity createEntity(World world, HitResult hitResult, ItemStack stack, PlayerEntity player) {
		Vec3d pos = hitResult.getPos();
		BigBouncyBallEntity bigBall = new BigBouncyBallEntity(world, pos.x, pos.y, pos.z);
		bigBall.setBallColor(DyedColorComponent.getColor(stack, 0xFF88DD88));
		if (world instanceof ServerWorld serverWorld) {
			EntityType.copier(serverWorld, stack, player).accept(bigBall);
		}
		return bigBall;
	}
}
