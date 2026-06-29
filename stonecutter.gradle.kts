plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.140" apply false
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
}

stonecutter {
    parameters {
        filters.exclude("**/*.accesswidener")

        swaps["model_registry"] = when {
            eval(current.version, ">= 26.1") -> "ModelLayerRegistry"
            else -> "EntityModelLayerRegistry"
        }
        replacements.string(current.parsed >= "1.21.11", "identifier") {
            replace("ResourceLocation", "Identifier")
            replace("MobSpawnType", "EntitySpawnReason")
        }
    }
}

stonecutter active "26.2"