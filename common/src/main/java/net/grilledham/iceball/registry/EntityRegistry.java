//~ identifier
package net.grilledham.iceball.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public interface EntityRegistry {
	
	default <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.tryBuild("iceball", id));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(/*~ if >= 1.21.11 'id' -> 'key' >> ')'*/key));
	}
	
	void registerModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definitionSupplier);
}
