//~ identifier
package net.grilledham.iceball.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.grilledham.iceball.registry.IceballEntities;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

//? if >= 1.21.11 {
import net.grilledham.iceball.client.entity.state.BigBouncyBallEntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state./*? if >= 26.1 >>+ '.'*/level.CameraRenderState;
//?} else {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
*///?}

public class BigBouncyBallRenderer extends EntityRenderer<BigBouncyBallEntity/*? if >= 1.21.11 >> '>'*/, BigBouncyBallEntityRenderState> {
	
	private final Identifier texture;
	private final BigBouncyBallModel model;
	
	public BigBouncyBallRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		texture = Identifier.fromNamespaceAndPath("iceball", "textures/entity/big_bouncy_ball.png");
		model = new BigBouncyBallModel(ctx.bakeLayer(IceballEntities.BIG_BOUNCY_BALL_MODEL_LAYER));
	}
	
	//? if >= 1.21.11 {
	@Override
	public void submit(BigBouncyBallEntityRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
		matrices.pushPose();
		matrices.mulPose(Axis.YP.rotationDegrees(180.0f - renderState.yaw));
		float h = renderState.damageWobbleTicks;
		float j = renderState.damageWobbleStrength;
		if (h > 0.0f) {
			matrices.mulPose(Axis.XP.rotationDegrees(Mth.sin(h) * h * j / 10.0f * renderState.damageWobbleSide));
		}
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.mulPose(Axis.YP.rotationDegrees(90.0f));
		queue.submitModel(model, renderState, matrices, model.renderType(texture), renderState.lightCoords, OverlayTexture.NO_OVERLAY, renderState.ballColor, null, renderState.outlineColor, null);
		matrices.popPose();
		super.submit(renderState, matrices, queue, cameraState);
	}
	
	@Override
	public BigBouncyBallEntityRenderState createRenderState() {
		return new BigBouncyBallEntityRenderState();
	}
	
	@Override
	public void extractRenderState(BigBouncyBallEntity entity, BigBouncyBallEntityRenderState state, float tickDelta) {
		super.extractRenderState(entity, state, tickDelta);
		state.yaw = entity.getYRot(tickDelta);
		state.damageWobbleTicks = entity.getDamageWobbleTicks() - tickDelta;
		state.damageWobbleStrength = Math.max(entity.getDamageWobbleStrength() - tickDelta, 0);
		state.damageWobbleSide = entity.getDamageWobbleSide();
		state.ballColor = entity.getBallColor();
		state.smallBounceAnimationState.copyFrom(entity.smallBounceAnimationState);
		state.bigBounceAnimationState.copyFrom(entity.bigBounceAnimationState);
	}
	//?} else {
	/*@Override
	public void render(BigBouncyBallEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
		matrices.pushPose();
		matrices.translate(0.0f, 0.0f, 0.0f);
		matrices.mulPose(Axis.YP.rotationDegrees(180.0f - yaw));
		float h = (float)entity.getDamageWobbleTicks() - tickDelta;
		float j = entity.getDamageWobbleStrength() - tickDelta;
		if (j < 0.0f) {
			j = 0.0f;
		}
		if (h > 0.0f) {
			matrices.mulPose(Axis.XP.rotationDegrees(Mth.sin(h) * h * j / 10.0f * (float)entity.getDamageWobbleSide()));
		}
		Identifier identifier = this.getTextureLocation(entity);
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.mulPose(Axis.YP.rotationDegrees(90.0f));
		model.setupAnim(entity, 0, 0.0f, entity.tickCount + tickDelta, 0.0f, 0.0f);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.renderType(identifier));
		model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, entity.getBallColor());
		if (!entity.isUnderWater()) {
			VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderType.waterMask());
			if (model instanceof WaterPatchModel modelWithWaterPatch) {
				modelWithWaterPatch.waterPatch().render(matrices, vertexConsumer2, light, OverlayTexture.NO_OVERLAY);
			}
		}
		matrices.popPose();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}
	
	@Override
	public Identifier getTextureLocation(BigBouncyBallEntity entity) {
		return texture;
	}
	*///?}
}
