import java.util.Properties

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}

val rootProps = Properties().apply {
    rootDir.parentFile.resolve("gradle.properties").inputStream().use(::load)
}

dependencies {
    implementation("dev.kikugie:stonecutter:${rootProps.getProperty("sc-version")}")
}
