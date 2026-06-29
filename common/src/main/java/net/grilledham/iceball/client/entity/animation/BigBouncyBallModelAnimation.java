package net.grilledham.iceball.client.entity.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class BigBouncyBallModelAnimation {
	public static final AnimationDefinition SMALL_BOUNCE = AnimationDefinition.Builder.withLength(0.5F).looping()
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.375F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.125F, KeyframeAnimations.scaleVec(1.1F, 0.8F, 1.1F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.scaleVec(0.9F, 1.1F, 0.9F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.375F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
	
	public static final AnimationDefinition BIG_BOUNCE = AnimationDefinition.Builder.withLength(1.0F)
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.8F, 0.4F, 1.8F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.625F, KeyframeAnimations.scaleVec(0.6F, 1.4F, 0.6F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.75F, KeyframeAnimations.scaleVec(1.1F, 0.8F, 1.1F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.875F, KeyframeAnimations.scaleVec(0.9F, 1.1F, 0.9F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
}
