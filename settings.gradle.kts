pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.neoforged.net/releases/")
		maven("https://maven.minecraftforge.net")
		maven("https://maven.kikugie.dev/snapshots")
		maven("https://maven.kikugie.dev/releases")
		maven("https://api.modrinth.com/maven")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version providers.gradleProperty("sc-version").get()
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val commonVersions = providers.gradleProperty("sc-enabled-common-versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val fabricVersions = providers.gradleProperty("sc-enabled-fabric-versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val neoforgeVersions = providers.gradleProperty("sc-enabled-neoforge-versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val dists = mapOf(
		"common" to commonVersions,
		"fabric" to fabricVersions,
		"neoforge" to neoforgeVersions
)
val uniqueVersions = dists.values.flatten().distinct()

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	create(rootProject) {
		versions(*uniqueVersions.toTypedArray())

		dists.forEach { (branchName, branchVersions) ->
				branch(branchName) {
					versions(*branchVersions.toTypedArray())
				}
		}

		providers.gradleProperty("sc-vcs-version").orNull?.let { mainVersion -> vcsVersion = mainVersion }
	}
}

rootProject.name = "Iceball"
