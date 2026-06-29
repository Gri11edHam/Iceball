//~ identifier

package net.grilledham.iceball.neoforge.registry;

import net.grilledham.iceball.registry.EntityRegistry;
import net.grilledham.iceball.registry.IceballEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class NeoforgeEntityRegistry implements EntityRegistry {
	
	private static NeoforgeEntityRegistry INSTANCE = null;
	
	private final HashMap<ModelLayerLocation, Supplier<LayerDefinition>> registryMap = new HashMap<>();
	
	private RegisterEvent.RegisterHelper<EntityType<?>> registry;
	
	public static NeoforgeEntityRegistry getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new NeoforgeEntityRegistry();
		}
		return INSTANCE;
	}
	
	@Override
	public <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.tryBuild("iceball", id));
		EntityType<T> entityType = type.build(/*~ if >= 1.21.11 'id' -> 'key' >> ')'*/key);
		registry.register(key, entityType);
		return entityType;
	}
	
	@Override
	public void registerModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definitionSupplier) {
		registryMap.put(location, definitionSupplier);
	}
	
	public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		IceballEntities.initClient(this);
		registryMap.keySet().forEach(location -> event.registerLayerDefinition(location, registryMap.get(location)));
	}
	
	public void register(RegisterEvent event) {
		event.register(BuiltInRegistries.ENTITY_TYPE.key(), registry -> {
			this.registry = registry;
			IceballEntities.init(this);
		});
	}
}
