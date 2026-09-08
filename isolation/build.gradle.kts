plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.combat.plugin)
}

android {
    namespace = "vision.combat.c4.ds.sample.isolation"

    defaultConfig {
        applicationId = "vision.combat.c4.ds.sample.isolation"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Limit to the two most common ABIs to keep build times reasonable.
        // arm64-v8a covers production devices; x86_64 covers emulators.
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Wire CMake so Gradle knows where to find the JNI sources.
    // NDK and CMake must be installed (sdkmanager "ndk;version" "cmake;version").
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    // AGP 8+ requires jniLibs.useLegacyPackaging instead of android:extractNativeLibs in
    // the manifest. This ensures the .so is extracted to nativeLibraryDir at install time
    // so the host's PathClassLoader (libPath = nativeLibraryDir) resolves libisolation_jni.so
    // via System.loadLibrary("isolation_jni").
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}
