# Host-provided libraries

**[← AGENTS.md](../../AGENTS.md)**

`compileOnly(c4ds-sdk)` does more than give you SDK types: the SDK re-exports a large slice of its
own dependency graph with `api(...)`, and the host ships every one of those libraries in its APK.
So you can `import` and use them directly — no `implementation` line, nothing added to your APK.

This doc is the **catalog**: what is on that list, at which version. The *rules* around it live
elsewhere — see [Getting started — Module dependencies](../guides/getting-started.md#3-module-dependencies)
for the dependency block, [Getting started — Release builds and obfuscation](../guides/getting-started.md#release-builds-and-obfuscation)
for why bundling a host-provided library breaks R8 builds, and
[Review rules — Gradle dependency configuration](../review-rules.md#gradle-dependency-configuration)
for what a reviewer checks.

> **Versions below are the ones `c4ds-sdk 0.5.1` resolves** (the version this repo pins in
> [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml)). They move with the SDK — regenerate
> the list for your own SDK version with the command in [Checking it yourself](#checking-it-yourself)
> rather than copying these numbers into your build.

---

## The rule

| You want to use…                                    | Declare                                                        |
|-----------------------------------------------------|----------------------------------------------------------------|
| Anything in the tables below                        | **nothing** — it arrives via `compileOnly(c4ds-sdk)`            |
| An annotation processor for one of them (e.g. Room) | `ksp(...)` only — the runtime still comes from the host         |
| An HTTP client                                      | **nothing** — use the host's Ktor, see [Networking](#networking-and-serialization) |
| A library genuinely not on the list                 | `implementation(...)`, and read the R8/keep-rules notes first   |

Adding a host-provided library as `implementation` puts a second copy of its classes in your APK.
In a release build R8's `-repackageclasses` then renames *your* copy, and at runtime you get two
incompatible versions of the same class loaded through two classloaders.

---

## Kotlin and kotlinx

| Library                                       | Version  |
|-----------------------------------------------|----------|
| `org.jetbrains.kotlin:kotlin-stdlib`          | 2.4.10   |
| `org.jetbrains.kotlin:kotlin-reflect`         | 2.4.10   |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core` | 1.11.0 |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.11.0 |
| `org.jetbrains.kotlinx:kotlinx-serialization-protobuf` | 1.11.0 |
| `org.jetbrains.kotlinx:kotlinx-datetime`      | 0.8.0    |

## Compose and Compose-adjacent AndroidX

The host is on **Material 2** (`androidx.compose.material`). Material 3 is *not* provided — see
[What is not provided](#what-is-not-provided).

| Library                                          | Version |
|--------------------------------------------------|---------|
| `androidx.compose.runtime:runtime`               | 1.11.4  |
| `androidx.compose.ui:ui`                         | 1.11.4  |
| `androidx.compose.ui:ui-util`                    | 1.11.4  |
| `androidx.compose.ui:ui-tooling-preview`         | 1.11.4  |
| `androidx.compose.foundation:foundation`         | 1.11.4  |
| `androidx.compose.material:material`             | 1.11.4  |
| `androidx.compose.material:material-icons-extended` | 1.7.8 |
| `androidx.activity:activity-compose`             | 1.13.0  |
| `androidx.navigation:navigation-compose`         | 2.9.8   |
| `androidx.lifecycle:lifecycle-viewmodel`         | 2.11.0  |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.11.0  |
| `androidx.lifecycle:lifecycle-runtime-compose`   | 2.11.0  |
| `androidx.lifecycle:lifecycle-runtime-ktx`       | 2.11.0  |
| `com.google.accompanist:accompanist-permissions` | 0.37.3  |

## Other AndroidX

| Library                       | Version | Note                                                        |
|-------------------------------|---------|-------------------------------------------------------------|
| `androidx.core:core`          | 1.19.0  | `core-ktx` 1.18.0 comes along transitively                   |
| `androidx.appcompat:appcompat`| 1.7.1   |                                                             |
| `androidx.room:room-runtime`  | 2.8.4   | runtime only — add your own `ksp(androidx.room.compiler)`    |
| `androidx.room:room-ktx`      | 2.8.4   |                                                             |

## Dependency injection

| Library                                            | Version |
|----------------------------------------------------|---------|
| `org.kodein.di:kodein-di`                          | 7.32.0  |
| `org.kodein.di:kodein-di-framework-android-x-compose` | 7.32.0 |

## Networking and serialization

**Ktor is the network client for plugins — use it and nothing else.** Enough of it is provided for a
full client, so a plugin needs no HTTP dependency of its own. See the **Network Requests** sample in
the [samples guidebook](../guides/samples-catalog.md#section-10-data-management), which builds its
own `HttpClient` out of these without adding a single dependency.

| Library                                  | Version |
|------------------------------------------|---------|
| `io.ktor:ktor-client-core`               | 3.5.1   |
| `io.ktor:ktor-client-android`            | 3.5.1   |
| `io.ktor:ktor-client-content-negotiation`| 3.5.1   |
| `io.ktor:ktor-client-logging`            | 3.5.1   |
| `io.ktor:ktor-serialization-kotlinx-json`| 3.5.1   |

## Images and media

| Library                        | Version |
|--------------------------------|---------|
| `io.coil-kt.coil3:coil-compose`| 3.5.0   |
| `io.coil-kt.coil3:coil-core`   | 3.5.0   |
| `io.coil-kt.coil3:coil-video`  | 3.5.0   |

## Map, model, and military symbology

| Library                                       | Version  |
|-----------------------------------------------|----------|
| `earth.worldwind:worldwind`                   | 2.0.7    |
| `vision.combat:c4model`                       | 1.15.8   |
| `vision.combat:c4unit`                        | 1.15.8   |
| `vision.combat:c4view-symbol`                 | 1.15.8   |
| `io.github.missioncommand:mil-sym-android`    | 2.11.2   |

`io.matthewnelson.kmp-file:file` 0.6.1 is also on the api graph, but **none of its 41 classes are in
the host APK** — do not use it. It is a KMP file abstraction that is mostly inline/`expect`-actual,
so much of it compiles away, but anything that does emit a reference will fail at runtime.

---

## The declared surface vs. the whole classpath

The tables above are the SDK's **declared** `api` surface — the part treated as a contract. Their
own transitive dependencies are on your compile classpath too, 235 modules in total for 0.5.1.

**They compile, but not all of them survive to runtime.** The host is minified with
`-repackageclasses`, so a class that is not kept by name is renamed to
`vision.combat.c4.ds.app.obf.*` — and a plugin referencing the original name gets
`ClassNotFoundException`. Debug builds are not minified, so this **only shows up in release**, which
is exactly when it is most expensive to find.

The lists below were produced by comparing every class in each resolved artifact against the classes
actually present in a minified host APK, so they are measured rather than inferred.

### Also resolvable (transitive, verified present in the host)

Usable, but not part of the declared contract — a deeper transitive can change version or disappear
in the next SDK release without that counting as a breaking change.

| Library | Version | | Library | Version |
|---|---|---|---|---|
| `androidx.activity:activity` | 1.13.0 | | `androidx.appcompat:appcompat-resources` | 1.7.1 |
| `androidx.annotation:annotation` | 1.10.0 | | `androidx.arch.core:core-runtime` | 2.2.0 |
| `androidx.collection:collection` | 1.5.0 | | `androidx.core:core-ktx` | 1.18.0 |
| `androidx.fragment:fragment` | 1.5.4 | | `androidx.lifecycle:lifecycle-livedata` | 2.11.0 |
| `androidx.loader:loader` | 1.0.0 | | `androidx.room:room-common` | 2.8.4 |
| `androidx.sqlite:sqlite` | 2.6.2 | | `androidx.savedstate:savedstate` | 1.4.0 |
| `androidx.vectordrawable:vectordrawable` | 1.1.0 | | `androidx.viewpager:viewpager` | 1.0.0 |
| `org.jetbrains.kotlinx:kotlinx-io-core` | 0.9.0 | | `org.jetbrains.kotlinx:kotlinx-serialization-core` | 1.11.0 |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | 1.11.0 | | `org.slf4j:slf4j-api` | 2.0.18 |

### Not resolvable — do not use

On your compile classpath, absent or renamed in the host. Naming any of these compiles cleanly and
fails at runtime in release:

| Library | Version | Why |
|---|---|---|
| `com.squareup.okio:okio` | 3.17.0 | renamed |
| `com.caverock:androidsvg-aar` | 1.4 | renamed |
| `dev.icerock.moko:resources` | 0.26.4 | renamed |
| `app.softwork:kotlinx-uuid-core` | 0.1.7 | renamed |
| `io.matthewnelson.kmp-file:file` | 0.6.1 | absent from the host APK |
| `org.gavaghan:geodesy` | 1.1.3 | absent from the host APK |
| `org.jetbrains:annotations` | 23.0.0 | absent (annotations, shrunk away) |
| `org.jspecify:jspecify` | 1.0.0 | absent (annotations, shrunk away) |
| `com.google.guava:listenablefuture` | 1.0 | absent from the host APK |

The `okio` row is the subtle one: parts of Coil's own API take okio types (`DiskCache` takes an
`okio.Path`, `ImageSource` wraps a `BufferedSource`), so a plugin can reach a renamed class *through*
a kept one. Basic `AsyncImage(model = …)` stays clear of it; custom fetchers, decoders and
disk-cache configuration do not.

Beyond that, a deeper transitive can change version or disappear entirely in the next SDK release
without that being a breaking change. Stick to the declared surface; if you lean on something
deeper, pin the SDK version you tested against **and verify against a minified host**, not just a
debug one.

## Present in the host, but not on the api graph

Two libraries are resolvable at runtime without being re-exported by the SDK, because the SDK's
consumer ProGuard rules keep their names so a plugin can reach them parent-first.

| Library                        | Version | Status |
|--------------------------------|---------|--------|
| `org.slf4j:slf4j-api`          | 2.0.18  | usable; already on your compile classpath via Ktor, so no dependency line needed |
| `com.squareup.okhttp3:okhttp`  | 5.4.0   | **compatibility only — do not use in new plugins** |

**OkHttp is not a supported choice for new work.** Its name is kept because an existing external
plugin already depends on it, not because plugins are meant to. **Ktor is the single network client
for plugins** — it is on the api graph, needs no dependency line, and is what the Network Requests
sample demonstrates. Nothing in this repository uses OkHttp, deliberately.

If you are maintaining that pre-existing plugin, the only workable form is `compileOnly` at exactly
the host's pinned version: `implementation` would bundle a second copy for R8 to repackage, which is
the precise failure the host's keep rule exists to prevent.

## What is not provided

Common libraries that are **not** on the classpath, and that you must declare yourself if you need
them (with the bundling caveats above):

- **Compose Material 3** (`androidx.compose.material3`) — the host UI and every SDK component is
  Material 2. Mixing an M3 copy of your own into host-composed surfaces is asking for theme and
  popup-context surprises; see [Resource & isolation — Material widgets across the window boundary](resource-and-isolation.md#material-widgets-across-the-window-boundary).
- **`androidx.room:room-compiler`** — a build-time KSP processor, never a runtime class. Declare
  `ksp(androidx.room.compiler)` yourself; the runtime stays host-provided.
- **`com.android.tools:desugar_jdk_libs`** — supplied by the `vision.combat.c4.ds` conventions plugin, which enables core library desugaring and declares the dependency for you.
- **Retrofit / Moshi / Gson / OkHttp** — Ktor + kotlinx-serialization are the provided stack, and
  the only one plugins should use. OkHttp resolves at runtime for legacy reasons but is not a
  supported choice; see [Present in the host, but not on the api graph](#present-in-the-host-but-not-on-the-api-graph).
- **DataStore, WorkManager, CameraX**, and most other AndroidX artifacts outside the tables above.
- **Test libraries** (JUnit 5, MockK, Turbine) — your own `testImplementation`, see
  [Testing your tool](../testing/testing-your-tool.md).

## Checking it yourself

The list above is generated from a resolved classpath, not hand-maintained. To regenerate it for
your SDK version, resolve any module that depends on the SDK:

```bash
./gradlew :gallery:dependencies --configuration debugCompileClasspath
```

Everything under the `vision.combat:c4ds-sdk` node is host-provided. To see only the SDK's declared
surface (the tables above), read the `api` variants of the SDK's Gradle module metadata:

```bash
find ~/.gradle/caches/modules-2/files-2.1/vision.combat -name 'c4ds-sdk*-*.module'
```

## Quick checklist

- [ ] Nothing from the tables above appears in your `dependencies` block as `implementation`/`api`.
- [ ] Room users declare `ksp(androidx.room.compiler)` and nothing else Room-related.
- [ ] Any library you *do* bundle is genuinely absent from the resolved SDK classpath.
- [ ] You checked the resolved release runtime classpath, not just the declared lines
      ([Review rules](../review-rules.md#gradle-dependency-configuration)).
