//? if >= 1.21.11 {
package net.grilledham.iceball.client.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class BigBouncyBallEntityRenderState extends EntityRenderState {
	public float yaw;
	public float damageWobbleTicks;
	public float damageWobbleStrength;
	public float damageWobbleSide;
	public int ballColor;
	public final AnimationState smallBounceAnimationState = new AnimationState();
	public final AnimationState bigBounceAnimationState = new AnimationState();
}
//?}