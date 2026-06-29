plugins {
    id("multiloader-loader")
    id("fabric-loom-compat")
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.23"
}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }
}

stonecutter {
    // Configure Stonecutter
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")

    if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
        mappings(loom.layered {
            officialMojangMappings()
            commonMod.depOrNull("parchment")?.let { parchmentVersion ->
                parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
            }
        })
    }

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mc}")
}

loom {
    accessWidenerPath = common.project.file("../../src/main/resources/accesswideners/${commonMod.mc}-${mod.id}.accesswidener")

    runs {
        getByName("client") {
            client()
            ideConfigFolder.set("Fabric")
            configName = "Fabric Client"
            runDir = "../../../run/client"
            ideConfigGenerated(true)
        }
        getByName("server") {
            server()
            ideConfigFolder.set("Fabric")
            configName = "Fabric Server"
            runDir = "../../../run/server"
            ideConfigGenerated(true)
        }
    }

    if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
        mixin {
            useLegacyMixinAp = false
            defaultRefmapName = "${mod.id}.refmap.json"
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    val awFile = project(":common").file("src/main/resources/accesswideners/${commonMod.mc}-${mod.id}.accesswidener")

    from(awFile.parentFile) {
        include(awFile.name)
        rename(awFile.name, "${mod.id}.accesswidener")
        into("")
    }
}
