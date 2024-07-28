package net.grilledham.iceball.client.entity.animation;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class BigBouncyBallModelAnimation {
	
	public static final Animation SMALL_BOUNCE = Animation.Builder.create(0.5F).looping()
			.addBoneAnimation("root", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, 4.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("root", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.1F, 0.8F, 1.1F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(0.9F, 1.1F, 0.9F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();
	
	public static final Animation CHARGE_BOUNCE = Animation.Builder.create(0.5F)
			.addBoneAnimation("root", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.8F, 0.4F, 1.8F), Transformation.Interpolations.LINEAR)
			))
			.build();
	
	public static final Animation BIG_BOUNCE = Animation.Builder.create(0.5F)
			.addBoneAnimation("root", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.8F, 0.4F, 1.8F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(0.6F, 1.4F, 0.6F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.1F, 0.8F, 1.1F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(0.9F, 1.1F, 0.9F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();
}
