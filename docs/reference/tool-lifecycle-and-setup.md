# Tool lifecycle & setup

**[← AGENTS.md](../../AGENTS.md)**

Gradle setup, the `ToolDescriptor` / `AbstractTool` contract, screen regions
(`ToolComponent`), `AbstractToolService`, and the Kodein DI lifecycle that ties them
together.

> **Sibling docs:** [Architecture for plugins](../architecture/architecture-for-plugins.md) ·
> [UI layer conventions](../architecture/ui-layer-conventions.md) · [Data & domain](../architecture/data-and-domain.md) ·
> [Resource & isolation](resource-and-isolation.md)

---

## Gradle setup

A plugin module is a plain Android **application** module (`com.android.application`), not a
library, and it declares **no `Activity`**. Apply the `vision.combat.c4.ds` conventions plugin
alongside it — it is published with the SDK, at the SDK's version:

```kotlin
// your-module/build.gradle.kts
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

That is the whole build script. The conventions plugin supplies the SDK dependencies,
`compileSdk`/`minSdk`/`targetSdk`, Java 17 and the host's Kotlin/Compose versions, Compose support,
the release build type with minification, a generated `-repackageclasses`, the host-provided library
exclusions, and a `verify<Variant>C4dsBoundary` check. See
**[Getting started — Gradle setup](../guides/getting-started.md#gradle-setup)** for the full list and
the reasoning behind each item.

Two SDK artifacts, two different Gradle configurations:

- **`compileOnly(libs.combat.ds.sdk)`** — SDK types and Compose APIs your code compiles
  against; not packaged into your APK because the host already has them.
- **`runtimeOnly(libs.combat.ds.sdk.runtime)`** — the small runtime shim the host loads when
  your tool activates.

Do not add the SDK as `implementation` — that would bundle a duplicate copy into your APK
and can cause class-identity mismatches at runtime.

**Kotlin version must match the host you're targeting.** A mismatched Kotlin/Compose
compiler version between your plugin and the host is a common source of subtle runtime
failures in plugin UI — confirm the exact Kotlin version your target host build expects
before you pin your own.

Get SDK Maven credentials and the current SDK coordinate/version from whoever distributes
your target host's SDK; this doc intentionally doesn't hardcode a specific version since it
changes independently of this guidance.

---

## Registering your tool with the host

Two files, in addition to your Kotlin code:

### 1. `combat_tools.xml` — descriptor registry

```xml
<?xml version="1.0" encoding="utf-8"?>
<combat-tools>
    <tool-descriptor name="com.example.mytool.MyToolDescriptor" />
</combat-tools>
```

List the fully-qualified class name of every `ToolDescriptor` your APK provides — one APK
can declare many tools.

### 2. `AndroidManifest.xml` — metadata pointer

```xml

<application ...><meta-data android:name="vision.combat.c4.ds.sdk.DECLARED_TOOLS"
android:resource="@xml/combat_tools" /></application>
```

The host reads this metadata to discover your `combat_tools.xml` and, through it, every
descriptor you declare.

---

## `ToolDescriptor` — identity and factory

`ToolDescriptor` is what the host uses to list your tool and construct it on demand. One
descriptor per tool:

```kotlin
class MyToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    override val nameResId: Int = R.string.my_tool_name
    override val iconResId: Int = R.drawable.ic_my_tool

    // emptyList() = hidden from the host's Tools list; only reachable via ToolManager.activate<T>()
    // or from another tool that resolves your descriptor. Include a launcher category to make the
    // tool directly visible in the host's Tools list.
    override val categories: List<String> = emptyList()

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool =
        MyTool(toolContext, this, di, params)
}
```

- `nameResId` / `iconResId` — shown wherever the host lists tools.
- `categories` — controls where/whether the tool is user-visible. A single APK with many
  tools typically makes **one** descriptor launcher-visible (a "hub") and leaves the rest
  `emptyList()`, activating them programmatically via `ToolManager.activate<T>()` from the
  hub. Copy this pattern for any multi-tool APK.
- `createTool` — factory the host calls when your tool is activated. Receives the DI graph
  (`di`) your tool should extend, and any activation `params`.
- `serviceDescriptionResId` + `createService` — optional; only needed if your tool has a
  session-scoped background service. See [`AbstractToolService`](#abstracttoolservice--session-scoped-background-work)
  below.

**Keep `AbstractTool` and `ToolDescriptor` in separate files** — e.g. `MyTool.kt` and
`MyToolDescriptor.kt` — rather than combining them into one file. This is the convention
every sample in this repo follows and keeps each type easy to locate by name.

---

## `AbstractTool` — the tool instance

`AbstractTool` is the live instance the host holds while your tool is active. It declares
which screen regions it needs and reacts to lifecycle callbacks.

```kotlin
internal class MyTool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractTool(toolContext, toolDescriptor, parentDI, params) {

    override val window: ToolComponent.Window by requiredComponent {
        MyToolWindow()
    }

    override fun onComponentShown(component: ToolComponent) {
        super.onComponentShown(component)
        // e.g. start observing something only while a component is visible
    }

    override fun onComponentHidden(component: ToolComponent) {
        super.onComponentHidden(component)
    }

    override fun onUpdate(toolParams: ToolParams?) {
        super.onUpdate(toolParams)
        // called when the host re-activates this tool with new params
    }

    override fun onDestroyRequested() {
        super.onDestroyRequested()
        // release anything not owned by a ViewModel/service
    }
}
```

### Screen regions (`ToolComponent`)

Declare only what your tool needs, each via `requiredComponent { }` (host will always show
it while the tool is active) — there's no `optionalComponent` variant; a tool either needs a
region or it doesn't:

| Component                            | Region                        | Typical use                                       |
|--------------------------------------|-------------------------------|---------------------------------------------------|
| **`ToolComponent.Window`**           | Side/bottom panel             | Full screens, with or without internal navigation |
| **`ToolComponent.MapWindow`**        | Variant of `Window`           | Window with an embedded secondary map             |
| **`ToolComponent.Overlay`**          | Over the main map             | Heads-up content, cursor readouts                 |
| **`ToolComponent.Status`**           | Bottom info strip             | Non-critical info                                 |
| **`ToolComponent.ExpandableStatus`** | Bottom strip, collapsible     | Status that can expand into more detail           |
| **`ToolComponent.Underlay`**         | Full layer under the main map | AR / background layers                            |

A tool can declare more than one component (e.g. a `Window` plus an `Overlay`). Each
`requiredComponent { }` block builds the Compose entry point for that region lazily, the
first time the host shows it.

`AbstractTool.endBar { }` is a separate DSL (not a `ToolComponent`) for adding action/toggle/
menu buttons to the map's end bar; see the [End Bar sample](../guides/samples-catalog.md#section-2-map-overlays)
for a worked example.

---

## `AbstractToolService` — session-scoped background work

If your tool needs to do work that outlives its window (background polling, a badge
counter, anything that should keep running while the tool is inactive but the session is
alive), give your descriptor a service:

```kotlin
class MyToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    // ...
    override val serviceDescriptionResId = R.string.my_tool_service_description

    override fun createService(toolContext: ToolContext, di: DI): AbstractToolService =
        MyToolService(toolContext, this, di)
}

internal class MyToolService(
    toolContext: ToolContext,
    descriptor: ToolDescriptor,
    parentDI: DI,
) : AbstractToolService(toolContext, descriptor, parentDI) {

    override val di: DI = subDI(super.di) {
        bindSingletonOf(::MyToolNotificationManager)
    }

    init {
        coroutineScope.launch {
            // background work using the service's own coroutine scope
        }
    }

    override fun onDestroy() {
        // cleanup
    }
}
```

The host binds your service instance into your tool's DI graph automatically, so
`AbstractTool` can reach it directly:

```kotlin
internal class MyTool(...) : AbstractTool(...) {
    private val service: MyToolService by instance()
    // ...
}
```

This gives you a single source of truth that survives the window opening/closing — the
window reads live state from the service (e.g. via a `StateFlow`) instead of duplicating it.

---

## Kodein DI lifecycle

- `AbstractTool` and `AbstractToolService` each expose a `di: DI` property seeded from a
  parent graph (`parentDI`, passed in by the host through `createTool`/`createService`).
- Override `di` with `subDI(super.di) { import(yourModule) }` to add tool-scoped bindings
  without touching what the parent already provides.
- Bind repositories/interactors as singletons scoped to that sub-graph
  (`bindSingleton { ... }` / `bindSingletonOf(::YourClass)`), so every ViewModel resolved
  via `diViewModel()` inside this tool shares the same instances.
- Resolve tool-scoped storage (like your isolated `SharedPreferences`) **inside your DI
  module's binding**, e.g. `bindSingleton { MyToolRepository(instance(arg =
  requireQualifiedName<YourToolDescriptor>())) }` — this is the standard way to key storage
  to your specific tool. It isn't something you call from arbitrary tool code; it's the
  `instance(arg = ...)` lookup that supplies the `SharedPreferences` (or other keyed)
  constructor argument when the module wires up the binding — see the worked example in
  [Architecture for plugins — adding domain + data layers](../architecture/architecture-for-plugins.md#adding-domain--data-layers)
  and [Resource & isolation](resource-and-isolation.md).

---

## Build and run from Android Studio

Your module has no `Activity`, so a normal "Run" configuration won't work as-is:

1. Select **Nothing** under **Launch options** in the run configuration.
2. Enable **Always install with package manager** so the host reliably sees rebuilt APKs.

Command line:

```bash
./gradlew :your-module:assembleRelease
adb install -r your-module/build/outputs/apk/release/your-module-release.apk
```

After install, open the host app; it refreshes its Tools list. If a change doesn't show up,
force-stop and restart the host app.
