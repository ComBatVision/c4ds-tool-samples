import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

android {
    namespace = "vision.combat.c4.ds.tool.sample.overlay"
    compileSdk = 35

    defaultConfig {
        applicationId = "vision.combat.c4.ds.tool.sample.overlay"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

// exclude kotlin-stdlib from runtimeOnly configuration as it is provided by the host app
configurations {
    getByName("runtimeOnly") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
}

dependencies {
    compileOnly(libs.combat.ds.sdk)
    runtimeOnly(libs.combat.ds.sdk.runtine)

    coreLibraryDesugaring(libs.android.tools.desugar)
}
