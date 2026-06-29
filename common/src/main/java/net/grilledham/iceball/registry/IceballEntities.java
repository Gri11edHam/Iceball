//~ identifier
package net.grilledham.iceball.registry;

import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.client.entity.renderer.BigBouncyBallRenderer;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class IceballEntities {
	
	public static EntityType<BigBouncyBallEntity> BIG_BOUNCY_BALL_ENTITY;
	
	public static void init(EntityRegistry entityRegistry) {
		BIG_BOUNCY_BALL_ENTITY = entityRegistry.register(
				"big_bouncy_ball",
				EntityType.Builder.of((EntityType.EntityFactory<BigBouncyBallEntity>)BigBouncyBallEntity::new, MobCategory.MISC)
						.sized(1.75f, 1.75f)
						.eyeHeight(0.875f)
						.clientTrackingRange(10)
		);
	}
	
	public static ModelLayerLocation BIG_BOUNCY_BALL_MODEL_LAYER;
	
	public static void initClient(EntityRegistry entityRegistry) {
		BIG_BOUNCY_BALL_MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath("iceball", "big_bouncy_ball"), "main");
		EntityRenderers.register(BIG_BOUNCY_BALL_ENTITY, BigBouncyBallRenderer::new);
		
		entityRegistry.registerModelLayer(BIG_BOUNCY_BALL_MODEL_LAYER, BigBouncyBallModel::getTexturedModelData);
	}
}
