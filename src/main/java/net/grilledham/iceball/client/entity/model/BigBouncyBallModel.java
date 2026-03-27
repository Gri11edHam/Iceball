package net.grilledham.iceball.client.entity.model;

import net.grilledham.iceball.client.entity.animation.BigBouncyBallModelAnimation;
import net.grilledham.iceball.client.entity.state.BigBouncyBallEntityRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BigBouncyBallModel extends EntityModel<BigBouncyBallEntityRenderState> {
	
	private final KeyframeAnimation smallBounceAnimation;
	private final KeyframeAnimation bigBounceAnimation;
	
	public BigBouncyBallModel(ModelPart root) {
		super(root);
		this.smallBounceAnimation = BigBouncyBallModelAnimation.SMALL_BOUNCE.bake(root);
		this.bigBounceAnimation = BigBouncyBallModelAnimation.BIG_BOUNCE.bake(root);
	}
	
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -26.0F, -12.0F, 24.0F, 24.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(72, 24).addBox(-12.0F, -2.0F, -12.0F, 24.0F, 2.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(0, 48).addBox(-12.0F, -28.0F, -12.0F, 24.0F, 2.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(104, 98).addBox(-12.0F, -26.0F, -14.0F, 24.0F, 24.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(52, 98).addBox(-12.0F, -26.0F, 12.0F, 24.0F, 24.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 74).addBox(-14.0F, -26.0F, -12.0F, 2.0F, 24.0F, 24.0F, new CubeDeformation(0.0F))
				.texOffs(72, 50).addBox(12.0F, -26.0F, -12.0F, 2.0F, 24.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 256, 256);
	}
	
	@Override
	public void setupAnim(BigBouncyBallEntityRenderState state) {
		root.getAllParts().forEach(ModelPart::resetPose);
		smallBounceAnimation.apply(state.smallBounceAnimationState, state.ageInTicks);
		bigBounceAnimation.apply(state.bigBounceAnimationState, state.ageInTicks);
	}
}
