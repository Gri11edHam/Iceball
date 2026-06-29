plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.23"
}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }

    accessConverter.register("main") {
        add("accesswideners/${commonMod.mc}-${mod.id}.accesswidener")
    }
}

stonecutter {
    // Configure Stonecutter
}

neoForge {
    enable {
        version = commonMod.dep("neoforge")
    }
}

dependencies {}

neoForge {
    val at = project.file("build/resources/main/META-INF/accesstransformer.cfg")

    accessTransformers.from(at.absolutePath)
    validateAccessTransformers = true

    runs {
        register("client") {
            client()
            ideFolderName = "NeoForge"
            ideName = "NeoForge Client (${project.path})"
            gameDirectory = rootProject.file("run/client")
        }
        register("server") {
            server()
            ideFolderName = "NeoForge"
            ideName = "NeoForge Server (${project.path})"
            gameDirectory = rootProject.file("run/server")
        }
    }

    commonMod.depOrNull("parchment")?.let {
        parchment {
            mappingsVersion = it
            minecraftVersion = commonMod.mc
        }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks {
    processResources {
        exclude("${mod.id}.accesswidener")
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(":neoforge:${commonMod.propOrNull("mc-version")}:processResources")
}
