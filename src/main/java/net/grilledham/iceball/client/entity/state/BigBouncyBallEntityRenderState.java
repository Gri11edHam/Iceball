package net.grilledham.iceball.client.entity.state;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.AnimationState;

public class BigBouncyBallEntityRenderState extends EntityRenderState {
	public float yaw;
	public float damageWobbleTicks;
	public float damageWobbleStrength;
	public float damageWobbleSide;
	public int ballColor;
	public final AnimationState smallBounceAnimationState = new AnimationState();
	public final AnimationState bigBounceAnimationState = new AnimationState();
}
