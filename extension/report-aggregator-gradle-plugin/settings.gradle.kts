val projectName: String by settings

rootProject.name = projectName

pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        id("org.jetbrains.kotlin.jvm").version(kotlinVersion)
        id("org.jetbrains.kotlin.kapt").version(kotlinVersion)
        id("com.gradle.plugin-publish").version("2.0.0")
    }
}
