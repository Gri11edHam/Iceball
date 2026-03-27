package net.grilledham.iceball.item;

import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BigBouncyBallItem extends Item {
	
	public BigBouncyBallItem(Properties settings) {
		super(settings);
	}
	
	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		HitResult hitResult = BoatItem.getPlayerPOVHitResult(world, user, ClipContext.Fluid.ANY);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return InteractionResult.PASS;
		}
		Vec3 vec3d = user.getViewVector(1.0F);
		List<Entity> list = world.getEntities(user, user.getBoundingBox().expandTowards(vec3d.scale(5.0)).inflate(1.0), EntitySelector.CAN_BE_PICKED);
		if (!list.isEmpty()) {
			Vec3 vec3d2 = user.getEyePosition();
			for (Entity entity : list) {
				AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
				if (!box.contains(vec3d2)) continue;
				return InteractionResult.PASS;
			}
		}
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BigBouncyBallEntity bigBall = this.createEntity(world, hitResult, itemStack, user);
			bigBall.setYRot(user.getYHeadRot());
			bigBall.setBallColor(DyedItemColor.getOrDefault(itemStack, 0xFF88DD88));
			if (!world.noCollision(bigBall, bigBall.getBoundingBox())) {
				return InteractionResult.FAIL;
			}
			if (!world.isClientSide()) {
				world.addFreshEntity(bigBall);
				world.gameEvent(user, GameEvent.ENTITY_PLACE, hitResult.getLocation());
				itemStack.consume(1, user);
			}
			user.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
	
	private BigBouncyBallEntity createEntity(Level world, HitResult hitResult, ItemStack stack, Player player) {
		Vec3 pos = hitResult.getLocation();
		BigBouncyBallEntity bigBall = new BigBouncyBallEntity(world, pos.x, pos.y, pos.z);
		bigBall.setBallColor(DyedItemColor.getOrDefault(stack, 0xFF88DD88));
		if (world instanceof ServerLevel serverWorld) {
			EntityType.createDefaultStackConfig(serverWorld, stack, player).accept(bigBall);
		}
		return bigBall;
	}
}
