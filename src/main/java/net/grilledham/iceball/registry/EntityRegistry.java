package net.grilledham.iceball.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.grilledham.iceball.client.entity.model.BigBouncyBallModel;
import net.grilledham.iceball.client.entity.renderer.BigBouncyBallRenderer;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityRegistry {
	
	public static final EntityType<BigBouncyBallEntity> BIG_BOUNCY_BALL_ENTITY = register(
			"big_bouncy_ball",
			EntityType.Builder.of((EntityType.EntityFactory<BigBouncyBallEntity>)BigBouncyBallEntity::new, MobCategory.MISC)
					.sized(1.75f, 1.75f)
					.eyeHeight(0.875f)
					.clientTrackingRange(10)
	);
	
	public static void init() {}
	
	@Environment(EnvType.CLIENT)
	public static ModelLayerLocation BIG_BOUNCY_BALL_MODEL_LAYER;
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		BIG_BOUNCY_BALL_MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath("iceball", "big_bouncy_ball"), "main");
		EntityRenderers.register(BIG_BOUNCY_BALL_ENTITY, BigBouncyBallRenderer::new);
		
		ModelLayerRegistry.registerModelLayer(BIG_BOUNCY_BALL_MODEL_LAYER, BigBouncyBallModel::getTexturedModelData);
	}
	
	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.tryBuild("iceball", id));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
	}
}
