package net.grilledham.iceball.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.client.entity.renderer.BigBouncyBallRenderer;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityRegistry {
	
	public static final EntityType<BigBouncyBallEntity> BIG_BOUNCY_BALL_ENTITY = register(
			"big_bouncy_ball",
			EntityType.Builder.create((EntityType.EntityFactory<BigBouncyBallEntity>)BigBouncyBallEntity::new, SpawnGroup.MISC)
					.dimensions(1.75f, 1.75f)
					.eyeHeight(0.875f)
					.maxTrackingRange(10)
	);
	
	public static void init() {}
	
	@Environment(EnvType.CLIENT)
	public static EntityModelLayer BIG_BOUNCY_BALL_MODEL_LAYER;
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		BIG_BOUNCY_BALL_MODEL_LAYER = new EntityModelLayer(Identifier.of("iceball", "big_bouncy_ball"), "main");
		EntityRendererRegistry.register(BIG_BOUNCY_BALL_ENTITY, BigBouncyBallRenderer::new);
		
		EntityModelLayerRegistry.registerModelLayer(BIG_BOUNCY_BALL_MODEL_LAYER, BigBouncyBallModel::getTexturedModelData);
	}
	
	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		return Registry.register(Registries.ENTITY_TYPE, Identifier.of("iceball", id), type.build(id));
	}
}
