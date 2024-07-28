package net.grilledham.iceball.client.entity.model;

import net.grilledham.iceball.client.entity.animation.BigBouncyBallModelAnimation;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class BigBouncyBallModel extends SinglePartEntityModel<BigBouncyBallEntity> {
	
	private final ModelPart root;
	
	public BigBouncyBallModel(ModelPart root) {
		this.root = root.getChild("root");
	}
	
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -26.0F, -12.0F, 24.0F, 24.0F, 24.0F, new Dilation(0.0F))
				.uv(72, 24).cuboid(-12.0F, -2.0F, -12.0F, 24.0F, 2.0F, 24.0F, new Dilation(0.0F))
				.uv(0, 48).cuboid(-12.0F, -28.0F, -12.0F, 24.0F, 2.0F, 24.0F, new Dilation(0.0F))
				.uv(104, 98).cuboid(-12.0F, -26.0F, -14.0F, 24.0F, 24.0F, 2.0F, new Dilation(0.0F))
				.uv(52, 98).cuboid(-12.0F, -26.0F, 12.0F, 24.0F, 24.0F, 2.0F, new Dilation(0.0F))
				.uv(0, 74).cuboid(-14.0F, -26.0F, -12.0F, 2.0F, 24.0F, 24.0F, new Dilation(0.0F))
				.uv(72, 50).cuboid(12.0F, -26.0F, -12.0F, 2.0F, 24.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 256, 256);
	}
	
	@Override
	public void setAngles(BigBouncyBallEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		root.traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(entity.smallBounceAnimationState, BigBouncyBallModelAnimation.SMALL_BOUNCE, ageInTicks);
		this.updateAnimation(entity.chargeBounceAnimationState, BigBouncyBallModelAnimation.CHARGE_BOUNCE, ageInTicks);
		this.updateAnimation(entity.bigBounceAnimationState, BigBouncyBallModelAnimation.BIG_BOUNCE, ageInTicks);
	}
	
	@Override
	public ModelPart getPart() {
		return root;
	}
}
