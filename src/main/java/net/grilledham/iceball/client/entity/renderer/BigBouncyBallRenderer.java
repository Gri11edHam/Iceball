package net.grilledham.iceball.client.entity.renderer;

import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.client.entity.state.BigBouncyBallEntityRenderState;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.registry.EntityRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BigBouncyBallRenderer extends EntityRenderer<BigBouncyBallEntity, BigBouncyBallEntityRenderState> {
	
	private final Identifier texture;
	private final BigBouncyBallModel model;
	
	public BigBouncyBallRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		texture = Identifier.of("iceball", "textures/entity/big_bouncy_ball.png");
		model = new BigBouncyBallModel(ctx.getPart(EntityRegistry.BIG_BOUNCY_BALL_MODEL_LAYER));
	}
	
	@Override
	public void render(BigBouncyBallEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(0.0f, 1.5f, 0.0f);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - state.yaw));
		float h = state.damageWobbleTicks;
		float j = state.damageWobbleStrength;
		if (h > 0.0f) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(h) * h * j / 10.0f * state.damageWobbleSide));
		}
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
		model.setAngles(state);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(texture));
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, state.ballColor);
		matrices.pop();
		super.render(state, matrices, vertexConsumers, light);
	}
	
	@Override
	public BigBouncyBallEntityRenderState createRenderState() {
		return new BigBouncyBallEntityRenderState();
	}
	
	@Override
	public void updateRenderState(BigBouncyBallEntity entity, BigBouncyBallEntityRenderState state, float tickDelta) {
		super.updateRenderState(entity, state, tickDelta);
		state.yaw = entity.getYaw(tickDelta);
		state.damageWobbleTicks = entity.getDamageWobbleTicks() - tickDelta;
		state.damageWobbleStrength = Math.max(entity.getDamageWobbleStrength() - tickDelta, 0);
		state.damageWobbleSide = entity.getDamageWobbleSide();
		state.ballColor = entity.getBallColor();
		state.smallBounceAnimationState.copyFrom(entity.smallBounceAnimationState);
		state.bigBounceAnimationState.copyFrom(entity.bigBounceAnimationState);
	}
}
