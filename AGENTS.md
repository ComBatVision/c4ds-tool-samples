# AGENTS.md — c4ds external tools

Thin index for AI coding agents (and humans) building an **external ComBat 4 Dismounted
Soldier (C4DS) tool** against the published `c4ds-sdk`. Detail lives in topic-scoped docs
under [`docs/`](docs/) — read only the ones relevant to your task.

This file describes **how to build your own tool**, generically, using only public SDK
surface. It has no knowledge of — and makes no claims about — the host application's
internal implementation.

> **Editing minimally is the default.** When a rule needs to change, update the doc that
> owns it so there is exactly one place to maintain.

---

## Where to look

| If you're…                                                                     | Read                                                                                                                                                                                      |
|--------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Starting a new tool, or unsure how the pieces fit together (start here)        | [`docs/architecture/architecture-for-plugins.md`](docs/architecture/architecture-for-plugins.md) — layering, module shape, `ToolDescriptor`/`AbstractTool` at a glance                                              |
| Setting up Gradle, the SDK dependency, and registering your tool with the host | [`docs/reference/tool-lifecycle-and-setup.md`](docs/reference/tool-lifecycle-and-setup.md) — `ToolDescriptor`, `AbstractTool`, `ToolContext`, screen regions, `AbstractToolService`, Kodein `subDI` lifecycle |
| Adding or changing a Compose screen, window, or ViewModel                      | [`docs/architecture/ui-layer-conventions.md`](docs/architecture/ui-layer-conventions.md) — MVI (`UiState`/`Action`/`Event`), `diViewModel()`, `EventHandler`                                                        |
| Adding or changing a repository, interactor, or mapper                         | [`docs/architecture/data-and-domain.md`](docs/architecture/data-and-domain.md) — repository/interactor patterns, isolated storage                                                                                   |
| Writing or updating unit tests                                                 | [`docs/testing/testing-your-tool.md`](docs/testing/testing-your-tool.md) — JUnit 5, MockK, Turbine; what to test and what not to                                                                          |
| Bundling resources, assets, or native (`.so`) libraries in your plugin APK     | [`docs/reference/resource-and-isolation.md`](docs/reference/resource-and-isolation.md) — plugin-scoped resources, assets, native libs                                                                         |
| Wondering whether a library is already on the classpath, or at which version   | [`docs/reference/host-provided-libraries.md`](docs/reference/host-provided-libraries.md) — what `compileOnly(c4ds-sdk)` re-exports, with versions, and what it doesn't                                        |
| Reviewing a change before it merges                                            | [`docs/review-rules.md`](docs/review-rules.md) — architecture/MVI/DI, threading/lifecycle/Compose, resources/locale, testing, dependency hygiene checklist                                |

Using Claude Code? The [`c4ds` plugin](https://github.com/ComBatVision/c4ds-claude-plugin)
scaffolds tools (`/c4ds:new-tool`), checks the environment (`/c4ds:doctor`) and updates the SDK
and these docs (`/c4ds:update-sdk`).

---

## Non-negotiables

The full rules live in the docs above. The handful that apply across every change:

- **A tool is a plain Android `Application` module with no `Activity`.** The host discovers
  it via manifest metadata and a `combat_tools.xml` descriptor registry. See
  [`docs/reference/tool-lifecycle-and-setup.md`](docs/reference/tool-lifecycle-and-setup.md).
- **Layering:** UI → Domain → Data, same direction always. Composables/ViewModels never
  touch `SharedPreferences`, Room, or files directly — go through a repository. See
  [`docs/architecture/architecture-for-plugins.md`](docs/architecture/architecture-for-plugins.md).
- **UI uses MVI:** a ViewModel exposes one `StateFlow<UiState>` and one event `Flow`
  (`Channel`-backed), and accepts a sealed `Action`. See
  [`docs/architecture/ui-layer-conventions.md`](docs/architecture/ui-layer-conventions.md).
- **DI:** Kodein (`DI`, `subDI`, `diViewModel()`). Bind a tool-scoped module by overriding
  `AbstractTool.di` / `AbstractToolService.di` with `subDI(super.di) { import(yourModule) }`.
- **Isolation is not optional.** Your `SharedPreferences`, files, and directories are
  plugin-scoped by construction (obtained through SDK APIs, keyed by your descriptor's
  qualified name) — never reach for ambient `Context.getSharedPreferences` with a hardcoded
  name and assume it's yours alone. See
  [`docs/reference/resource-and-isolation.md`](docs/reference/resource-and-isolation.md).
- **Do not bundle the host-provided runtime.** Kotlin stdlib, Compose, and most AndroidX
  artifacts are supplied by the host; your module depends on the SDK with `compileOnly` +
  `runtimeOnly`, never `implementation`, for `c4ds-sdk`.
- **Testing:** JUnit 5 + MockK + Turbine for ViewModels, interactors, and mappers.
  Composables are not unit-tested. See
  [`docs/testing/testing-your-tool.md`](docs/testing/testing-your-tool.md).
- **Style:** match the file you're editing; don't refactor opportunistically.

---

## Build verification

From your tool repo's root (adjust module names to your own project):

```bash
./gradlew :your-module:assembleRelease
adb install -r your-module/build/outputs/apk/release/your-module-release.apk
```

Neither your module nor the host declares an `Activity` for it — install the APK, then
launch the host app and activate your tool from its Tools list (or from wherever your
`ToolDescriptor.categories` places it). For unit tests, prefer module-scoped Gradle tasks
(e.g. `:your-module:testDebugUnitTest`) over a root `test` task once your project grows
past one module.
