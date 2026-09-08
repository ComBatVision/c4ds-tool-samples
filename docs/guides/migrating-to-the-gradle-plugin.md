# Migrating to the c4ds Gradle plugin

**[← README](../../README.md)** · **[Getting started](getting-started.md)** · **[Plugin isolation](plugin-isolation.md)**

From SDK `0.5.5` the build recipe a plugin needs ships as a Gradle plugin, `vision.combat.c4.ds`,
published alongside the SDK at the SDK's version. Applying it replaces every piece of build
configuration that used to be copied by hand.

This matters beyond tidiness: the hand-written recipe was **wrong in every sample in this repo**. The
`exclude` below is on `runtimeOnly`, which is not a resolvable configuration, so it did nothing —
every sample was packaging its own kotlin-stdlib. That is a release-only crash, invisible in debug
builds. See [Getting started § Release builds and obfuscation](getting-started.md#release-builds-and-obfuscation).

## settings.gradle.kts

Add the SDK repository to `pluginManagement` so Gradle can resolve the plugin itself:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://nexus.combat.vision/repository/maven-sdk/")
            credentials {
                username = providers.gradleProperty("c4ds_sdk_username").get()
                password = providers.gradleProperty("c4ds_sdk_password").get()
            }
        }
    }
}
```

## build.gradle.kts

**Before**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_17 } }

android {
    namespace = "com.example.mytools"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.example.mytools"
        minSdk = 26
        targetSdk = 37
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
}

configurations {
    getByName("runtimeOnly") {                                   // does nothing — see above
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
}

dependencies {
    compileOnly(libs.combat.ds.sdk)
    runtimeOnly(libs.combat.ds.sdk.runtime)
    coreLibraryDesugaring(libs.android.tools.desugar)
}
```

**After**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.combat.plugin)
}

android {
    namespace = "com.example.mytools"
    defaultConfig {
        applicationId = "com.example.mytools"
    }
}
```

Keep only what is specific to your plugin — version code/name, signing, your own dependencies, and
any KSP or serialization plugins you use.

## proguard-rules.pro

Delete its contents. The plugin and the SDK's consumer rules now supply the `ToolDescriptor` and
ViewModel keeps, `-repackageclasses` generated from your real `applicationId`, the host-provided
library exclusions, and the `-dontwarn` for the map types the SDK exposes but does not publish. All
three samples in this repo have an empty rules file.

Delete `-repackageclasses` in particular: a hand-written value silently rots when `applicationId`
changes, and the generated one cannot.

## Version catalog

```toml
[versions]
combat-ds-sdk = "0.5.5"
agp = "9.3.1"          # the version the host app is built with

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
combat-plugin = { id = "vision.combat.c4.ds", version.ref = "combat-ds-sdk" }
```

Drop the `kotlin`, `desugar-jdk-libs` and `combat-ds-sdk` **library** entries — the plugin brings the
SDK dependencies, and the Kotlin, Compose, Java and SDK levels the host was built with.

## Multi-module plugins

Apply the same plugin next to `com.android.library` in library modules. A library module is not a
plugin: it is merged into the APK that consumes it, so it gets the SDK compile-time contract and
none of the boundary treatment, which belongs to the APK module.

## Verify

```bash
./gradlew :yourModule:assembleRelease
```

`verifyReleaseC4dsBoundary` runs afterwards and fails the build if your APK packages a library the
host already provides, or declares a class name the host's own R8 run can produce. A clean run
prints:

```
c4ds boundary check: no duplicated host libraries, no shared class names.
```

Test a **release** build — debug builds are not minified, so none of this is observable in them.
