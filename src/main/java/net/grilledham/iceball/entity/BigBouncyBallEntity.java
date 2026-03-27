package net.grilledham.iceball.entity;

import net.grilledham.iceball.registry.EntityRegistry;
import net.grilledham.iceball.registry.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BigBouncyBallEntity extends VehicleEntity implements Leashable, PlayerRideableJumping {
	
	private static final double SQRT_2 = Math.sqrt(2);
	
	protected static final EntityDataAccessor<Integer> BALL_COLOR = SynchedEntityData.defineId(BigBouncyBallEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Byte> ANIMATIONS = SynchedEntityData.defineId(BigBouncyBallEntity.class, EntityDataSerializers.BYTE);
	
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
	private int lerpTicks;
	private Leashable.LeashData leashData;
	
	public BigBouncyBallEntity(EntityType<? extends BigBouncyBallEntity> type, Level world) {
		super(type, world);
	}
	
	public BigBouncyBallEntity(Level world, double x, double y, double z) {
		this(EntityRegistry.BIG_BOUNCY_BALL_ENTITY, world);
		setPos(x, y, z);
		xOld = x;
		yOld = y;
		zOld = z;
	}
	
	public void destroy(ServerLevel world, Item selfAsItem) {
		this.kill(world);
		if (!world.getGameRules().get(GameRules.ENTITY_DROPS)) {
			return;
		}
		ItemStack itemStack = new ItemStack(selfAsItem);
		itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
		if(getBallColor() != 0xFF88DD88) {
			itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(getBallColor() & 0xFFFFFF));
		}
		this.spawnAtLocation(world, itemStack);
	}
	
	@Override
	protected Item getDropItem() {
		return ItemRegistry.BIG_BOUNCY_BALL_ITEM;
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(BALL_COLOR, 0xFF88DD88);
		builder.define(ANIMATIONS, (byte)0);
	}
	
	public void setDamageWobbleTicks(int damageWobbleTicks) {
		this.entityData.set(DATA_ID_HURT, damageWobbleTicks);
	}
	
	public void setDamageWobbleSide(int damageWobbleSide) {
		this.entityData.set(DATA_ID_HURTDIR, damageWobbleSide);
	}
	
	public void setDamageWobbleStrength(float damageWobbleStrength) {
		this.entityData.set(DATA_ID_DAMAGE, damageWobbleStrength);
	}
	
	public void setSmallBounce(boolean smallBounce) {
		if(smallBounce) {
			this.entityData.set(ANIMATIONS, (byte)(this.entityData.get(ANIMATIONS) | 0b00000001));
		} else {
			this.entityData.set(ANIMATIONS, (byte)(this.entityData.get(ANIMATIONS) & 0b00000010));
		}
	}
	
	public void setBigBounce(boolean bigBounce) {
		if(bigBounce) {
			this.entityData.set(ANIMATIONS, (byte)(this.entityData.get(ANIMATIONS) | 0b00000010));
		} else {
			this.entityData.set(ANIMATIONS, (byte)(this.entityData.get(ANIMATIONS) & 0b00000001));
		}
	}
	
	public float getDamageWobbleStrength() {
		return this.entityData.get(DATA_ID_DAMAGE);
	}
	
	public int getDamageWobbleTicks() {
		return this.entityData.get(DATA_ID_HURT);
	}
	
	public int getDamageWobbleSide() {
		return this.entityData.get(DATA_ID_HURTDIR);
	}
	
	public boolean isSmallBounce() {
		return (this.entityData.get(ANIMATIONS) & 0b00000001) > 0;
	}
	
	public boolean isBigBounce() {
		return (this.entityData.get(ANIMATIONS) & 0b00000010) > 0;
	}
	
	@Override
	public boolean canCollideWith(Entity entity) {
		return Boat.canVehicleCollide(this, entity);
	}
	
	@Override
	public boolean canBeCollidedWith(@Nullable Entity entity) {
		return true;
	}
	
	@Override
	public boolean isPushable() {
		return true;
	}
	
	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		if(smallBounceAnimationState.isStarted()) {
			return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor).subtract(0, Math.sin(Math.PI * (smallBounceAnimationState.getTimeInMillis(tickCount) / 250f)) / 4, 0);
		}
		if(bigBounceAnimationState.isStarted()) {
			if(bigBounceAnimationState.getTimeInMillis(tickCount) >= 750) {
				return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor);
			} else if(bigBounceAnimationState.getTimeInMillis(tickCount) >= 500) {
				return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor).add(0, Math.sin(Math.PI * ((bigBounceAnimationState.getTimeInMillis(tickCount) - 500) / 250f)), 0);
			} else {
				return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor).subtract(0, (bigBounceAnimationState.getTimeInMillis(tickCount) / 500f), 0);
			}
		}
		return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor);
	}
	
	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}
	
	@Override
	public void tick() {
		if(isAlive()) {
			tickCount++;
		}
		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}
		if (this.getDamageWobbleStrength() > 0.0f) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
		}
		super.tick();
		updatePositionAndRotation: {
			if (this.isLocalInstanceAuthoritative()) {
				this.lerpTicks = 0;
				this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
			}
			if (this.lerpTicks <= 0) {
				break updatePositionAndRotation;
			}
			this.lerpPositionAndRotationStep(this.lerpTicks, this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
			--this.lerpTicks;
		}
		if (this.isLocalInstanceAuthoritative()) {
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
				} else if(onGround()) {
					chargingTicks++;
					if(chargingTicks >= 10) {
						chargingJump = false;
						float jumpStrength = this.jumpStrength >= 90 ? 1.0f : 0.4f + 0.4f * (float)this.jumpStrength / 90.0f;
						Vec3 velocity = getDeltaMovement();
						double dx = jumpStrength * 2.5 * Math.cos(Math.toRadians(getYRot() + 90));
						double dz = jumpStrength * 2.5 * Math.sin(Math.toRadians(getYRot() + 90));
						setDeltaMovement(velocity.x + dx, velocity.y + jumpStrength * 2.2, velocity.z + dz);
					}
				}
				
				double dx = forward * Math.cos(Math.toRadians(getYRot() + 90));
				double dz = forward * Math.sin(Math.toRadians(getYRot() + 90));
				dx -= strafe * Math.cos(Math.toRadians(getYRot()));
				dz -= strafe * Math.sin(Math.toRadians(getYRot()));
				if(strafe != 0 && forward != 0) {
					dx /= SQRT_2;
					dz /= SQRT_2;
				}
				dx *= 0.7;
				dz *= 0.7;
				
				if(onGround()) {
					setDeltaMovement(getDeltaMovement().x * 0.5 + dx * 0.5, getDeltaMovement().y, getDeltaMovement().z() * 0.5 + dz * 0.5);
				} else {
					setDeltaMovement(getDeltaMovement().x * 0.95 + dx * 0.05, getDeltaMovement().y, getDeltaMovement().z() * 0.95 + dz * 0.05);
				}
				
				double d = this.getGravity();
				Vec3 vec3d = this.getDeltaMovement();
				double vy = Math.max(vec3d.y, -2);
				this.setDeltaMovement(vec3d.x, vy + d, vec3d.z);
			}
			this.move(MoverType.SELF, this.getDeltaMovement());
		} else {
			this.setDeltaMovement(Vec3.ZERO);
		}
		this.applyEffectsFromBlocks();
		List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2f, -0.01f, 0.2f), EntitySelector.pushableBy(this));
		if (!list.isEmpty()) {
			boolean bl = !this.level().isClientSide() && !(this.getControllingPassenger() instanceof Player);
			for (Entity entity : list) {
				if (entity.hasPassenger(this)) continue;
				if (bl && this.getPassengers().isEmpty() && !entity.isPassenger() && entity instanceof LivingEntity && !(entity instanceof Player)) {
					entity.startRiding(this);
					continue;
				}
				this.push(entity);
			}
		}
		
		if(hasControllingPassenger() && isBigBounce() && (onGround() || animationTicks >= 10)) {
			smallBounceAnimationState.stop();
			bigBounceAnimationState.startIfStopped(tickCount);
			if(bigBounceAnimationState.getTimeInMillis(tickCount) >= 1000) {
				setBigBounce(false);
			}
		} else {
			bigBounceAnimationState.stop();
			if(isSmallBounce()) {
				smallBounceAnimationState.startIfStopped(tickCount);
			} else {
				smallBounceAnimationState.stop();
			}
		}
		if(isBigBounce() && (onGround() || animationTicks >= 10)) {
			animationTicks++;
			if(animationTicks >= 20) {
				animationTicks = 0;
				setBigBounce(false);
			}
		}
		Vec3 prevPos = new Vec3(xOld, 0, zOld);
		setSmallBounce(prevPos.distanceTo(position().multiply(1, 0, 1)) > 0.1 && onGround());
	}
	
	public void setInputs(boolean moveForward, boolean moveBack, boolean moveLeft, boolean moveRight) {
		this.moveForward = moveForward;
		this.moveBack = moveBack;
		this.moveLeft = moveLeft;
		this.moveRight = moveRight;
	}
	
	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		InteractionResult superInteraction = super.interact(player, hand, location);
		if (superInteraction != InteractionResult.PASS) {
			return superInteraction;
		} else {
			return player.isSecondaryUseActive() || !this.level().isClientSide() && !player.startRiding(this)
					? InteractionResult.PASS
					: InteractionResult.SUCCESS;
		}
	}
	
	@Override
	protected double getDefaultGravity() {
		return -0.2;
	}
	
	@Override
	public float maxUpStep() {
		return 1.0f;
	}
	
	@Override
	public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		Entity entity = this.getFirstPassenger();
		return entity instanceof LivingEntity ? (LivingEntity)entity : super.getControllingPassenger();
	}
	
	@Override
	public void onPassengerTurned(Entity passenger) {
		setYRot(passenger.getYHeadRot());
	}
	
	@Override
	protected void readAdditionalSaveData(ValueInput nbt) {
		this.readLeashData(nbt);
		setBallColor(nbt.getIntOr("Color", 0xFF88DD88));
	}
	
	@Override
	protected void addAdditionalSaveData(ValueOutput nbt) {
		this.writeLeashData(nbt, this.leashData);
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
	
	public int getBallColor() {
		return entityData.get(BALL_COLOR);
	}
	
	public void setBallColor(int rgb) {
		entityData.set(BALL_COLOR, 0xFF000000 | rgb);
	}
	
	@Override
	public void onPlayerJump(int strength) {
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
	public void handleStartJump(int strength) {
		chargingJump = true;
		chargingTicks = 0;
		animationTicks = 0;
		setBigBounce(true);
	}
	
	@Override
	public void handleStopJump() {
	}
}
