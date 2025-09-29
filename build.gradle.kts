// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://nexus.combat.vision/repository/maven-sdk/")
            credentials {
                username = System.getProperty("c4ds_sdk_username") ?: rootProject.properties["c4ds_sdk_username"].toString()
                password = System.getProperty("c4ds_sdk_password") ?: rootProject.properties["c4ds_sdk_password"].toString()
            }
        }
    }
}
