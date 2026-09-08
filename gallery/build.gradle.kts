plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.combat.plugin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "vision.combat.c4.ds.sample.gallery"

    defaultConfig {
        applicationId = "vision.combat.c4.ds.sample.gallery"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Room now arrives transitively via the SDK on the compile classpath — the host provides it,
    // same as Compose/coroutines. Only the annotation processor is declared here.
    ksp(libs.androidx.room.compiler)
}
