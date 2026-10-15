plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.combat.plugin)
}

android {
    namespace = "__NAMESPACE__"

    defaultConfig {
        applicationId = "__NAMESPACE__"
        versionCode = 1
        versionName = "1.0"
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
