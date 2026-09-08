# Getting started

**[← README](../../README.md)** · **[Samples guidebook](samples-catalog.md)** · **[Plugin isolation](plugin-isolation.md)**

This guide covers prerequisites, Gradle setup, tool integration, and how to build and install the sample APKs from Android Studio.

---

## Requirements

To build and run these samples you need:

1. **[ComBat 4 Dismounted Soldier](https://play.google.com/store/apps/details?id=vision.combat.c4.ds)** installed on a device or emulator.
2. **Maven SDK repository access** — request credentials by writing to [support@combat.vision](mailto:support@combat.vision).
3. **Matching SDK version** — this repo pins `c4ds-sdk` in [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml). Check [Nexus](https://nexus.combat.vision/#browse/browse:maven-sdk:vision%2Fcombat%2Fc4ds-sdk) for the latest release (login with the same credentials).
4. **Binary-compatible toolchain** — use the same Kotlin version as the host app:

   ```
   kotlin = "2.4.0"
   ```

   Compose is provided by Kotlin's bundled K2 Compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`);
   this repo does not pin a separate Compose runtime version. Mismatching the Kotlin version can cause
   subtle runtime failures in plugin UI.

5. **NDK + CMake** — required only when building `:isolation` (native `.so` smoke test).

| Item | This repo |
|---|---|
| Host app | ComBat 4 DS |
| SDK | `c4ds-sdk` `0.5.1` (see `gradle/libs.versions.toml`) |
| Kotlin | `2.4.0` |
| AGP | `9.2.1` |
| JVM | 17 |

---

## Tool screen layout

The host app divides the screen into **tool components**. Your tool declares which components it needs; the host renders them in fixed regions:

| Component | Region | Typical use |
|---|---|---|
| **Overlay** | Red — over the main map | Heads-up display, cursor readouts |
| **Status** | Blue — bottom strip | Non-critical info; can be expandable |
| **Window** | Green — side/bottom panel | Full screens with optional nested navigation |
| **MapWindow** | Variant of Window | Embedded secondary map inside the panel |
| **Underlay** | Full area under the main map | AR and background layers |
| **End bar** | Yellow — right edge of map | Custom action/toggle/menu buttons via the `endBar { }` DSL |

<img width="1000" alt="Tool component screen layout" src="https://github.com/user-attachments/assets/bdf78782-9206-4cf8-9c1b-c5328eb6e3f8" />

Every sample in `:gallery` targets one or more of these regions. Open **Sample Gallery** from the host launcher, then use **Launch** on any card to try it.

---

## Gradle setup

### 1. Nexus credentials

Add to `~/.gradle/gradle.properties` (or the project root `gradle.properties`):

```properties
c4ds_sdk_username=<username>
c4ds_sdk_password=<password>
```

### 2. Maven repository

The root [`build.gradle.kts`](../../build.gradle.kts) already configures the SDK repository for all subprojects:

```kotlin
subprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://nexus.combat.vision/repository/maven-sdk/")
            credentials {
                username = System.getProperty("c4ds_sdk_username")
                    ?: rootProject.properties["c4ds_sdk_username"].toString()
                password = System.getProperty("c4ds_sdk_password")
                    ?: rootProject.properties["c4ds_sdk_password"].toString()
            }
        }
        mavenLocal()
    }
}
```

When starting a **new** tool project, copy this block into your root `build.gradle.kts`.

### 3. Build conventions plugin

Apply `vision.combat.c4.ds` alongside the Android plugin. It is published with the SDK, at the
SDK's version:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.combat.plugin)
}
```

That is all a plugin module needs. The conventions supply:

- `compileOnly(c4ds-sdk)` — SDK types and Compose APIs at compile time — and
  `runtimeOnly(c4ds-sdk-runtime)`, the minimal runtime shim the host loads when your tool activates;
- `compileSdk` / `minSdk` / `targetSdk` / Java 17, and the host's Kotlin and Compose versions, so
  your bytecode stays binary-compatible without you pinning anything;
- the release build type: minification, resource shrinking, and a generated
  `-repackageclasses <your applicationId>.obf`;
- exclusion of host-provided libraries from the **runtime** classpath, and a
  `verify<Variant>C4dsBoundary` check that fails the release build if a duplicate slips through.

The last two are explained under *Release builds and obfuscation* below.

If your plugin is split across several Gradle modules, apply the same conventions plugin next to
`com.android.library` in the library modules. A library module is not a plugin: it is merged into
the APK that consumes it, so it gets the SDK compile-time contract and none of the boundary
treatment, which belongs to the APK module.

The host app supplies Kotlin stdlib, Compose, and most AndroidX artifacts. Do **not** bundle duplicate runtime libraries in your plugin APK.

For the concrete list — which libraries the SDK re-exports, at which versions, and which common ones
it does *not* — see [Host-provided libraries](../reference/host-provided-libraries.md).

---

## Integration guide

External tools are plain Android **Application** modules with **no Activity**. The host discovers tools via manifest metadata.

### Step 1 — Subclass `AbstractTool`

Example: [`WindowSingleScreenTool.kt`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/window/singlescreen/WindowSingleScreenTool.kt)

```kotlin
internal class WindowSingleScreenTool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractTool(toolContext, toolDescriptor, parentDI, params) {

    override val window: ToolComponent.Window by requiredComponent {
        WindowSingleScreenWindow()
    }
}
```

Declare only the components your tool needs (`window`, `overlay`, `status`, `mapWindow`, etc.).

### Step 2 — Subclass `ToolDescriptor`

Example: [`WindowSingleScreenToolDescriptor.kt`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/window/singlescreen/WindowSingleScreenToolDescriptor.kt)

```kotlin
class WindowSingleScreenToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    override val nameResId: Int = R.string.window_single_screen_tool_name
    override val iconResId: Int = R.drawable.ic_window
    override val categories: List<String> = emptyList()  // hidden; launched from the hub via ToolManager

    override fun createTool(
        toolContext: ToolContext,
        di: DI,
        params: ToolParams?,
    ): AbstractTool = WindowSingleScreenTool(toolContext, this, di, params)
}
```

- `nameResId` / `iconResId` — shown in the host **Tools** list (when `categories` includes `CATEGORY_LAUNCHER`).
- `createTool` — factory invoked by the host when the tool is activated.

### Step 3 — Register descriptors in `combat_tools.xml`

Example: [`gallery/src/main/res/xml/combat_tools.xml`](../../gallery/src/main/res/xml/combat_tools.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<combat-tools>
    <tool-descriptor name="vision.combat.c4.ds.sample.gallery.window.singlescreen.WindowSingleScreenToolDescriptor" />
</combat-tools>
```

List every `ToolDescriptor` FQCN your APK provides.

### Step 4 — Declare metadata in `AndroidManifest.xml`

```xml
<application ...>
    <meta-data
        android:name="vision.combat.c4.ds.sdk.DECLARED_TOOLS"
        android:resource="@xml/combat_tools" />
</application>
```

### Step 5 — Build and install

See [Building from Android Studio](#building-from-android-studio) below.

### Hub-only launcher visibility

In this repo, only **Sample Gallery** (`CatalogToolDescriptor`) sets `categories = listOf(CATEGORY_LAUNCHER)`. All other gallery tools use `categories = emptyList()` and are opened from the hub via `ToolManager.activate<T>()`. Copy this pattern when you want a single entry point for multiple tools in one APK.

---

## Building from Android Studio

Your module does **not** need an Activity. To run/debug:

1. Select **Nothing** under **Launch options**.
2. Enable **Always install with package manager** — otherwise the host may not see updated APKs.

<img width="500" alt="Run configuration: Nothing launch, Always install with package manager" src="https://github.com/user-attachments/assets/726d2066-f4d9-48d7-9aea-a1b14727e427" />

### Command line

From the repo root:

```bash
./gradlew :gallery:assembleRelease :isolation:assembleRelease
adb install -r gallery/build/outputs/apk/release/gallery-release.apk
adb install -r isolation/build/outputs/apk/release/isolation-release.apk
```

After install, ComBat 4 refreshes the **Tools** list. You usually see changes immediately after tool re-activation; if not, force-stop and restart the host app.

<img width="1000" alt="Installed tools in ComBat 4 launcher" src="https://github.com/user-attachments/assets/845f8c0c-d798-48d1-87c2-5a2cb39d95ed" />

### Release builds and obfuscation

Every APK on the device — the host and each installed plugin — is built and obfuscated
**independently**. In a release build, R8 renames your tool's classes to short, generic names
(e.g. `a.a`, `a.b`). Because R8 runs per-APK with no visibility into other APKs, the host and
every plugin tend to produce the **same** short obfuscated names.

At runtime, the host loads a plugin's classes with a **parent-first classloader**: the host's
own classloader is consulted before the plugin's. If the host happens to already have a class
named `a.a` (from its own obfuscated code), that class wins over the plugin's same-named `a.a` —
even though they're unrelated classes with unrelated fields. The plugin's class is never loaded,
and you get a runtime `NoSuchFieldError` (or similar linkage failure) the first time the plugin
code executes. In practice this is often invisible until then: the tool just silently disappears
from the **Tools** list, or fails when activated.

**Fix: give R8 a unique repackage target per app.** The conventions plugin generates this for you,
from the module's real `applicationId`:

```proguard
-repackageclasses <your.applicationId>.obf
```

It is generated rather than hand-written precisely because the value **must** be unique per APK —
copying another app's value defeats the fix, and a hand-written one silently rots when the
`applicationId` changes.

**That's the only custom rule you need — don't add a blanket `-keep`.** The c4ds SDK ships
its own *consumer* ProGuard rules, which every plugin inherits automatically just by
depending on the SDK. Those rules already keep:

- every `ToolDescriptor` subclass and its `(Context)` constructor — this is how the host
  finds and instantiates the class named in your `combat_tools.xml`;
- ViewModel constructors — needed for `diViewModel()` / reflection-based creation;
- `R$*` classes — needed for resource ID lookups.

That's everything the host touches by name. You do **not** need to `-keep` your descriptors,
ViewModels, or `R` classes yourself, and you should avoid a blanket rule like:

```proguard
# Don't do this — it defeats shrinking/obfuscation for your entire module.
-keep class <your.package>.** { *; }
```

A rule this broad silently disables R8 shrinking and obfuscation for every class in your
module, not just the ones the host needs — including code that has nothing to do with the
plugin contract. Keep your `proguard-rules.pro` limited to `-repackageclasses` (above) plus
any narrow, module-specific rules your own dependencies require (e.g. a `-dontwarn` for an
optional reference that isn't on your compile classpath).

**`-repackageclasses` alone isn't enough — don't bundle host-provided libraries either.**
Even a legitimate `implementation` dependency can transitively drag a library the host
already provides (Kotlin stdlib, Compose, AndroidX, **Coroutines**) into your APK. Once that
copy gets obfuscated and repackaged under your app's `-repackageclasses` prefix, its classes
no longer match the host's own copies of the same library, and you get a runtime
`IncompatibleClassChangeError` (or similar linkage failure) the first time that code path
runs — not at build time.

This repo's `:gallery` uses Room, and Room is **host-provided**: the c4ds SDK's data module
exposes it (`api(androidx.room.runtime)` / `room-ktx`), so a plugin gets Room through
`compileOnly(c4ds-sdk)` plus its own `ksp(androidx.room.compiler)` for code generation — it
does **not** declare `implementation(androidx.room.runtime)`. Nothing Room-related is bundled
into the plugin APK; at runtime it links against the host's Room, exactly like Compose,
Coroutines, and AndroidX.

The hazard remains for any *other* third-party `implementation` dependency you add — it may
transitively pull in something the host already provides. Check what actually ends up on the
release runtime classpath:

```bash
./gradlew :yourModule:dependencies --configuration releaseRuntimeClasspath
```

The conventions plugin already excludes the usual suspects — Kotlin stdlib, coroutines and
kotlinx-serialization — from the **runtime** classpath only, leaving the compile classpath intact.
(A blanket `configurations.all { exclude(...) }` would strip the SDK's own `compileOnly` copy too
and break compilation; scoping to `…RuntimeClasspath` is what makes it safe.)

For anything else the host ships that your dependency graph drags in, declare it and the plugin
excludes it the same way:

```kotlin
combat {
    hostProvidedLibraries.add("com.example:some-library")
    hostProvidedPackages.add("com.example.somelibrary.")
}
```

You do not have to find these by hand. `verify<Variant>C4dsBoundary` runs after `assembleRelease`, reads your
APK's own `mapping.txt`, and fails the build naming every class that came from a host-provided
package or that shares a name the host's R8 run can also produce. A duplicate that would have been a
crash on a user's device becomes a build error instead.

**Kodein, kotlinx-serialization, and Room keeps are already covered — you don't need to add
them.** `compileOnly` dependencies normally don't bring their library's own consumer ProGuard
rules with them: a host-provided library on `compileOnly` (Kotlin stdlib, Compose, AndroidX,
Coroutines, Room, Kodein, …) never becomes part of your APK's dependency graph at the packaging
step where those consumer rules would normally be merged in. Concretely, without help:
**Kodein-DI reads a type's generic `Signature` at runtime** to resolve typed bindings
(`instance()`, `factory()`, etc. — the standard DI pattern this SDK uses), but R8 strips
`Signature` by default; **kotlinx-serialization** needs each `@Serializable` class's
`Companion`/`serializer()` kept to look up its serializer at runtime; and **Room** needs each
generated `<Database>_Impl` reachable by name.

The c4ds SDK's runtime module (`c4ds-sdk-runtime`, pulled in via `runtimeOnly`) ships consumer
ProGuard rules that cover exactly these hazards — the `Signature` attribute, the Kodein
type-token classes, the kotlinx-serialization companion/`serializer()` keeps, and Room's
generated-database keeps — and every plugin inherits them automatically, the same way it
inherits the `ToolDescriptor`/ViewModel/`R$*` keeps described above. You do **not** need to add
any `-keepattributes Signature`, Kodein, serialization, or Room keeps to your own
`proguard-rules.pro`.

This is the general shape of the hazard, though: whenever a *different* library is
host-provided (`compileOnly`) and needs its own runtime keep rules that aren't covered by the
SDK's centralized set, declare them in your own `proguard-rules.pro` — for example, `:gallery`
adds a `-dontwarn` for an optional reference (`InputListener`) that isn't on its compile
classpath.

### Manifest components run in your plugin's process

Everything above about `compileOnly` assumes your tool's own code — the `ToolDescriptor`,
`AbstractTool`, ViewModels, Composables. The host loads **that** code into **its own process**
via a parent-first classloader, so SDK/Compose/AndroidX/Coroutines are already present at
runtime and stay `compileOnly`.

That reasoning does **not** extend to anything you declare as a manifest component —
a `<provider>`, `<service>`, or `<receiver>` in your `AndroidManifest.xml`. Android instantiates
those in **your plugin's own process**, using **your APK's own classloader**, which has no
visibility into the host's classes:

| What | Runs in | Classloader | Library scope |
|---|---|---|---|
| Tool code (`ToolDescriptor`, `AbstractTool`, ViewModels, Composables) | Host process | Host's parent-first classloader | `compileOnly` — host provides SDK/Compose/AndroidX at runtime |
| Manifest components (`<provider>`, `<service>`, `<receiver>`) | **Your plugin's own process** | **Your APK's own classloader** | **`implementation`** — must be bundled; the host cannot supply these classes |

Get this backwards and you'll see it fail the same way a real third-party plugin once did: it
declared `androidx.core.content.FileProvider` in its manifest but scoped `androidx.core`
`compileOnly` ("the host provides it"). The class was never in the plugin APK, so the plugin's
own process crashed on bind with `ClassNotFoundException: androidx.core.content.FileProvider` —
taking the *whole plugin* down with it (it shows up in the host as "incompatible" / `Tools: 0`,
not just a missing provider).

**The rule generalizes past `androidx.core`.** Kotlin's stdlib is host-provided too — it's
`compileOnly`/excluded from your APK — so a manifest component that touches a non-inlined stdlib
helper (`runCatching`/`Result`, `InputStream.use { }`, `.copyTo()`, and similar `kotlin.io.*` /
`kotlin.jvm.internal.*` classes) throws `NoClassDefFoundError` at process bind — the same failure
shape as the `androidx.core` case. Keep manifest-component code to framework classes plus whatever
you've explicitly bundled. (Your *tool* code has no such limit — it runs in the host process, where
the full Kotlin stdlib is present.) If you legitimately own a manifest component — a `<receiver>`
or `<service>` your plugin needs — apply this rule to it: bundle whatever library defines it.

### Never declare your own `FileProvider` to share or open a file

A plugin must **not** declare its own `androidx.core.content.FileProvider` (or any other
`ContentProvider`) to share or open a file with another app. Bundling the provider's library
(per the table above) is necessary but **not sufficient** — there is a second, independent
blocker that bundling can never fix:

- Your tool *code* runs in the **host's** process (that's the whole point of `compileOnly` in the
  table above), not your plugin's own process.
- A `content://` Uri can only be granted (`FLAG_GRANT_READ_URI_PERMISSION`) by the process that
  **owns** the provider backing it.
- The host does not own your plugin's provider — different UID, different process — so a grant
  the host process attempts on your plugin's Uri is silently dropped, and the app you handed the
  Uri to gets a `SecurityException` when it tries to open it.

Bundling `androidx.core` fixes the classload crash above, but the grant still fails. There is no
manifest-component fix for this — the grant has to come from a process that owns a provider the
*host* can act on, which means it has to be the host's own provider.

**Use the host's `ShareManager` instead**, obtained via `LocalShareManager` in Compose:

- `ShareManager.getShareableUri(file: File): Uri?` mints a **host-owned** `content://` Uri for
  `file` — a Uri the host process (where your tool code runs) can validly grant — or returns
  `null` if `file` is outside the host FileProvider's configured roots. Use this when you need to
  build your own intent (e.g. `ACTION_VIEW` to open a file in an external viewer). See the
  **`openwith`** sample (`:gallery`, Section 12) for the full pattern.
- `ShareManager.shareFile(file, intentType)` / `shareFiles(files, intentType)` do the same Uri
  minting for you and launch the OS share sheet directly. See the **`hostservices`** sample
  (`:gallery`, Section 12).

Either way, write the file under one of the host FileProvider's configured roots — e.g.
`File(toolContext.cacheDir, "export")` — not the cache dir root itself, which isn't covered by
any declared path and makes `getShareableUri`/`shareFile` fail closed (`null`, no fallback to a
`file://` Uri, which would throw `FileUriExposedException` if passed to another app).

### Using the Sample Gallery

1. Install `:gallery` (required) and optionally `:isolation` (for cross-APK native sample).
2. Open ComBat 4 → **Tools** → **Sample Gallery**.
3. Browse samples by section; tap **Launch** or **Details** on any card.
4. The **Native / Cross-APK** card is enabled only when `:isolation` is installed.

See the [Samples guidebook](samples-catalog.md) for per-sample screenshots, SDK APIs, and verification steps.
