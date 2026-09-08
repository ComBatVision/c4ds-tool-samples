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
        // The c4ds build-conventions plugin is published alongside the SDK.
        maven {
            url = uri("https://nexus.combat.vision/repository/maven-sdk/")
            credentials {
                username = providers.gradleProperty("c4ds_sdk_username").get()
                password = providers.gradleProperty("c4ds_sdk_password").get()
            }
        }
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
