import org.gradle.kotlin.dsl.exclude

plugins {
    id("java")
    id("idea")
    id("java-library")
}

version = "${loader}-${commonMod.version}+mc${stonecutterBuild.current.version}"

base {
    archivesName = commonMod.id
}

java {
    val javaVersion = commonProject.prop("java.version")!!.toInt()

    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepositories(
            maven("https://maven.parchmentmc.org") { name = "ParchmentMC" },
            maven("https://maven.neoforged.net/releases") { name = "NeoForge" },
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
        name = "DevAuth"
        content {
            includeGroup("me.djtheredstoner")
        }
    }
}

tasks {
    processResources {
        val expandProps = mapOf(
            "javaVersion" to commonMod.propOrNull("java.version"),
            "modId" to commonMod.id,
            "modName" to commonMod.name,
            "modDescription" to commonMod.description,
            "modVersion" to commonMod.version,
            "modLicense" to commonMod.license,
            "modHomepage" to commonMod.homePage,
            "modSources" to commonMod.github,
            "modIssues" to commonMod.github + "/issues",
            "mcVersion" to commonMod.propOrNull("mc-version"),
            "mcCompatFabric" to commonMod.propOrNull("mc-compat-fabric"),
            "mcCompatNeoforge" to commonMod.propOrNull("mc-compat-neoforge"),
            "fabricLoaderVersion" to commonMod.depOrNull("fabric-loader"),
            "fabricApiVersion" to commonMod.depOrNull("fabric-api"),
            "neoForgeVersion" to commonMod.depOrNull("neoforge"),
        ).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

        val jsonExpandProps = expandProps.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
            expand(expandProps)
        }

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
            expand(jsonExpandProps)
        }

        inputs.properties(expandProps)
    }
}

tasks.named("processResources") {
    dependsOn(":common:${commonMod.propOrNull("mc-version")}:stonecutterGenerate")
}
