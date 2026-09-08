@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Temporary: resolves the vision.combat.c4.ds plugin from a local publish. Replace with the
        // Nexus maven-sdk repository once the plugin is published there.
        mavenLocal()
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "c4ds-tool-samples"

include(":gallery")
include(":isolation")
