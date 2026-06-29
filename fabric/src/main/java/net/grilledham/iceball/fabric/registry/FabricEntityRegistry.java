package net.grilledham.iceball.fabric.registry;

import net.fabricmc.fabric.api.client.rendering.v1./*$ model_registry >> ';'*/ModelLayerRegistry;
import net.grilledham.iceball.registry.EntityRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

public class FabricEntityRegistry implements EntityRegistry {
	
	private static FabricEntityRegistry INSTANCE = null;
	
	public static FabricEntityRegistry getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new FabricEntityRegistry();
		}
		return INSTANCE;
	}
	
	@Override
	public void registerModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definitionSupplier) {
		/*$ model_registry >> '.'*/ModelLayerRegistry.registerModelLayer(location, definitionSupplier::get);
	}
}
