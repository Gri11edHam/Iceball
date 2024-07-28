package net.grilledham.iceball.client.entity.renderer;

import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.registry.EntityRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.ModelWithWaterPatch;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BigBouncyBallRenderer extends EntityRenderer<BigBouncyBallEntity> {
	
	private final BigBouncyBallModel model;
	
	public BigBouncyBallRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
		model = new BigBouncyBallModel(ctx.getPart(EntityRegistry.BIG_BOUNCY_BALL_MODEL_LAYER));
	}
	
	@Override
	public void render(BigBouncyBallEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(0.0f, 1.5f, 0.0f);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - yaw));
		float h = (float)entity.getDamageWobbleTicks() - tickDelta;
		float j = entity.getDamageWobbleStrength() - tickDelta;
		if (j < 0.0f) {
			j = 0.0f;
		}
		if (h > 0.0f) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(h) * h * j / 10.0f * (float)entity.getDamageWobbleSide()));
		}
		Identifier identifier = this.getTexture(entity);
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
		model.setAngles(entity, 0, 0.0f, entity.age + tickDelta, 0.0f, 0.0f);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(identifier));
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, entity.getBallColor());
		if (!entity.isSubmergedInWater()) {
			VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getWaterMask());
			if (model instanceof ModelWithWaterPatch modelWithWaterPatch) {
				modelWithWaterPatch.getWaterPatch().render(matrices, vertexConsumer2, light, OverlayTexture.DEFAULT_UV);
			}
		}
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}
	
	@Override
	public Identifier getTexture(BigBouncyBallEntity entity) {
		return Identifier.of("iceball", "textures/entity/big_bouncy_ball.png");
	}
}
