pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        val kotlinVersion: String by System.getProperties()
        kotlin("jvm").version(kotlinVersion)
    }
}