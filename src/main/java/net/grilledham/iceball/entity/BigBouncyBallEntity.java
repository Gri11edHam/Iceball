package net.grilledham.iceball.entity;

import net.grilledham.iceball.registry.EntityRegistry;
import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BigBouncyBallEntity extends Entity implements Leashable, JumpingMount {
	
	private static final double SQRT_2 = Math.sqrt(2);
	
	protected static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(BigBouncyBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(BigBouncyBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(BigBouncyBallEntity.class, TrackedDataHandlerRegistry.FLOAT);
	protected static final TrackedData<Integer> BALL_COLOR = DataTracker.registerData(BigBouncyBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Byte> ANIMATIONS = DataTracker.registerData(BigBouncyBallEntity.class, TrackedDataHandlerRegistry.BYTE);
	
	public final AnimationState smallBounceAnimationState = new AnimationState();
	public final AnimationState bigBounceAnimationState = new AnimationState();
	
	private boolean moveForward = false;
	private boolean moveBack = false;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean chargingJump = false;
	private int jumpStrength = 0;
	private int chargingTicks = 0;
	private int animationTicks = 0;
	private double x;
	private double y;
	private double z;
	private float ballYaw;
	private float ballPitch;
	private int lerpTicks;
	private Leashable.LeashData leashData;
	
	public BigBouncyBallEntity(EntityType<? extends BigBouncyBallEntity> type, World world) {
		super(type, world);
	}
	
	public BigBouncyBallEntity(World world, double x, double y, double z) {
		this(EntityRegistry.BIG_BOUNCY_BALL_ENTITY, world);
		setPosition(x, y, z);
		prevX = x;
		prevY = y;
		prevZ = z;
	}
	
	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		boolean bl;
		if (this.getWorld().isClient || this.isRemoved()) {
			return true;
		}
		if (this.isAlwaysInvulnerableTo(source)) {
			return false;
		}
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.scheduleVelocityUpdate();
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
		this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
		bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
		if (!bl && this.getDamageWobbleStrength() > 40.0f || this.shouldAlwaysKill(source)) {
			this.killAndDropSelf(world, source);
		} else if (bl) {
			this.discard();
		}
		return true;
	}
	
	boolean shouldAlwaysKill(DamageSource source) {
		return false;
	}
	
	public void killAndDropItem(ServerWorld world, Item selfAsItem) {
		this.kill(world);
		if (!world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			return;
		}
		ItemStack itemStack = new ItemStack(selfAsItem);
		itemStack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
		if(getBallColor() != 0xFF88DD88) {
			itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(getBallColor() & 0xFFFFFF, true));
		}
		this.dropStack(world, itemStack);
	}
	
	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(DAMAGE_WOBBLE_TICKS, 0);
		builder.add(DAMAGE_WOBBLE_SIDE, 1);
		builder.add(DAMAGE_WOBBLE_STRENGTH, 0.0f);
		builder.add(BALL_COLOR, 0xFF88DD88);
		builder.add(ANIMATIONS, (byte)0);
	}
	
	public void setDamageWobbleTicks(int damageWobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, damageWobbleTicks);
	}
	
	public void setDamageWobbleSide(int damageWobbleSide) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, damageWobbleSide);
	}
	
	public void setDamageWobbleStrength(float damageWobbleStrength) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, damageWobbleStrength);
	}
	
	public void setSmallBounce(boolean smallBounce) {
		if(smallBounce) {
			this.dataTracker.set(ANIMATIONS, (byte)(this.dataTracker.get(ANIMATIONS) | 0b00000001));
		} else {
			this.dataTracker.set(ANIMATIONS, (byte)(this.dataTracker.get(ANIMATIONS) & 0b00000010));
		}
	}
	
	public void setBigBounce(boolean bigBounce) {
		if(bigBounce) {
			this.dataTracker.set(ANIMATIONS, (byte)(this.dataTracker.get(ANIMATIONS) | 0b00000010));
		} else {
			this.dataTracker.set(ANIMATIONS, (byte)(this.dataTracker.get(ANIMATIONS) & 0b00000001));
		}
	}
	
	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}
	
	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}
	
	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}
	
	public boolean isSmallBounce() {
		return (this.dataTracker.get(ANIMATIONS) & 0b00000001) > 0;
	}
	
	public boolean isBigBounce() {
		return (this.dataTracker.get(ANIMATIONS) & 0b00000010) > 0;
	}
	
	protected void killAndDropSelf(ServerWorld world, DamageSource source) {
		this.killAndDropItem(world, this.asItem());
	}
	
	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.ballYaw = yaw;
		this.ballPitch = pitch;
		lerpTicks = interpolationSteps;
	}
	
	@Override
	public boolean collidesWith(Entity other) {
		return BoatEntity.canCollide(this, other);
	}
	
	@Override
	public boolean isCollidable() {
		return true;
	}
	
	@Override
	public boolean isPushable() {
		return true;
	}
	
	@Override
	protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		if(smallBounceAnimationState.isRunning()) {
			return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor).subtract(0, Math.sin(Math.PI * (smallBounceAnimationState.getTimeInMilliseconds(age) / 250f)) / 4, 0);
		}
		if(bigBounceAnimationState.isRunning()) {
			if(bigBounceAnimationState.getTimeInMilliseconds(age) >= 750) {
				return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
			} else if(bigBounceAnimationState.getTimeInMilliseconds(age) >= 500) {
				return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor).add(0, Math.sin(Math.PI * ((bigBounceAnimationState.getTimeInMilliseconds(age) - 500) / 250f)), 0);
			} else {
				return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor).subtract(0, (bigBounceAnimationState.getTimeInMilliseconds(age) / 500f), 0);
			}
		}
		return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
	}
	
	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}
	
	@Override
	public void tick() {
		if(isAlive()) {
			age++;
		}
		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}
		if (this.getDamageWobbleStrength() > 0.0f) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
		}
		super.tick();
		updatePositionAndRotation: {
			if (this.isLogicalSideForUpdatingMovement()) {
				this.lerpTicks = 0;
				this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
			}
			if (this.lerpTicks <= 0) {
				break updatePositionAndRotation;
			}
			this.lerpPosAndRotation(this.lerpTicks, this.x, this.y, this.z, this.ballYaw, this.ballPitch);
			--this.lerpTicks;
		}
		if (this.isLogicalSideForUpdatingMovement()) {
//			this.updateVelocity();
			{
				float forward = 0;
				float strafe = 0;
				if(!chargingJump) {
					if(moveForward) {
						forward++;
					}
					if(moveBack) {
						forward--;
					}
					if(moveLeft) {
						strafe--;
					}
					if(moveRight) {
						strafe++;
					}
				} else if(isOnGround()) {
					chargingTicks++;
					if(chargingTicks >= 10) {
						chargingJump = false;
						float jumpStrength = this.jumpStrength >= 90 ? 1.0f : 0.4f + 0.4f * (float)this.jumpStrength / 90.0f;
						Vec3d velocity = getVelocity();
						double dx = jumpStrength * 2.5 * Math.cos(Math.toRadians(getYaw() + 90));
						double dz = jumpStrength * 2.5 * Math.sin(Math.toRadians(getYaw() + 90));
						setVelocity(velocity.x + dx, velocity.y + jumpStrength * 2.2, velocity.z + dz);
					}
				}
				
				double dx = forward * Math.cos(Math.toRadians(getYaw() + 90));
				double dz = forward * Math.sin(Math.toRadians(getYaw() + 90));
				dx -= strafe * Math.cos(Math.toRadians(getYaw()));
				dz -= strafe * Math.sin(Math.toRadians(getYaw()));
				if(strafe != 0 && forward != 0) {
					dx /= SQRT_2;
					dz /= SQRT_2;
				}
				dx *= 0.7;
				dz *= 0.7;
				
				if(isOnGround()) {
					setVelocity(getVelocity().x * 0.5 + dx * 0.5, getVelocity().y, getVelocity().getZ() * 0.5 + dz * 0.5);
				} else {
					setVelocity(getVelocity().x * 0.95 + dx * 0.05, getVelocity().y, getVelocity().getZ() * 0.95 + dz * 0.05);
				}
				
				double d = this.getFinalGravity();
				Vec3d vec3d = this.getVelocity();
				double vy = Math.max(vec3d.y, -2);
				this.setVelocity(vec3d.x, vy + d, vec3d.z);
			}
			this.move(MovementType.SELF, this.getVelocity());
		} else {
			this.setVelocity(Vec3d.ZERO);
		}
		this.tickBlockCollision();
		List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.2f, -0.01f, 0.2f), EntityPredicates.canBePushedBy(this));
		if (!list.isEmpty()) {
			boolean bl = !this.getWorld().isClient && !(this.getControllingPassenger() instanceof PlayerEntity);
			for (Entity entity : list) {
				if (entity.hasPassenger(this)) continue;
				if (bl && this.getPassengerList().isEmpty() && !entity.hasVehicle() && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity) && !(entity instanceof PlayerEntity)) {
					entity.startRiding(this);
					continue;
				}
				this.pushAwayFrom(entity);
			}
		}
		
		if(hasPassengers() && isBigBounce() && (isOnGround() || animationTicks >= 10)) {
			smallBounceAnimationState.stop();
			bigBounceAnimationState.startIfNotRunning(age);
			if(bigBounceAnimationState.getTimeInMilliseconds(age) >= 1000) {
				setBigBounce(false);
			}
		} else {
			bigBounceAnimationState.stop();
			if(isSmallBounce()) {
				smallBounceAnimationState.startIfNotRunning(age);
			} else {
				smallBounceAnimationState.stop();
			}
		}
		if(isBigBounce() && (isOnGround() || animationTicks >= 10)) {
			animationTicks++;
			if(animationTicks >= 20) {
				animationTicks = 0;
				setBigBounce(false);
			}
		}
		Vec3d prevPos = new Vec3d(prevX, 0, prevZ);
		setSmallBounce(prevPos.distanceTo(getPos().multiply(1, 0, 1)) > 0.1 && isOnGround());
	}
	
	public void setInputs(boolean moveForward, boolean moveBack, boolean moveLeft, boolean moveRight) {
		this.moveForward = moveForward;
		this.moveBack = moveBack;
		this.moveLeft = moveLeft;
		this.moveRight = moveRight;
	}
	
	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		ActionResult actionResult = super.interact(player, hand);
		if (actionResult != ActionResult.PASS) {
			return actionResult;
		}
		if (player.shouldCancelInteraction()) {
			return ActionResult.PASS;
		}
		if (!this.getWorld().isClient) {
			return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
		}
		return ActionResult.PASS;
	}
	
	@Override
	protected double getGravity() {
		return -0.2;
	}
	
	@Override
	public float getStepHeight() {
		return 1.0f;
	}
	
	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		Entity entity = this.getFirstPassenger();
		return entity instanceof LivingEntity ? (LivingEntity)entity : super.getControllingPassenger();
	}
	
	@Override
	public void onPassengerLookAround(Entity passenger) {
		setYaw(passenger.getHeadYaw());
	}
	
	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.leashData = this.readLeashDataFromNbt(nbt);
		if(nbt.contains("Color")) {
			setBallColor(nbt.getInt("Color"));
		}
	}
	
	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		this.writeLeashDataToNbt(nbt, this.leashData);
		nbt.putInt("Color", getBallColor());
	}
	
	@Nullable
	@Override
	public LeashData getLeashData() {
		return this.leashData;
	}
	
	@Override
	public void setLeashData(@Nullable Leashable.LeashData leashData) {
		this.leashData = leashData;
	}
	
	public Item asItem() {
		return ItemRegistry.BIG_BOUNCY_BALL_ITEM;
	}
	
	public int getBallColor() {
		return dataTracker.get(BALL_COLOR);
	}
	
	public void setBallColor(int rgb) {
		dataTracker.set(BALL_COLOR, 0xFF000000 | rgb);
	}
	
	@Override
	public void setJumpStrength(int strength) {
		chargingJump = true;
		chargingTicks = 0;
		animationTicks = 0;
		setBigBounce(true);
		jumpStrength = strength;
	}
	
	@Override
	public boolean canJump() {
		return getControllingPassenger() != null;
	}
	
	@Override
	public void startJumping(int height) {
		chargingJump = true;
		chargingTicks = 0;
		animationTicks = 0;
		setBigBounce(true);
	}
	
	@Override
	public void stopJumping() {
	}
}
