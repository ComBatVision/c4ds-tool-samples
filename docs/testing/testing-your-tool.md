# Testing your tool

**[← AGENTS.md](../../AGENTS.md)**

What to unit-test in a c4ds tool, and the JUnit 5 + MockK + Turbine pattern for ViewModels
and interactors. The layering described in [Architecture for plugins](../architecture/architecture-for-plugins.md)
is what makes this possible on the plain JVM, without an emulator or instrumentation test.

> **Sibling docs:** [Architecture for plugins](../architecture/architecture-for-plugins.md) ·
> [UI layer conventions](../architecture/ui-layer-conventions.md) · [Data & domain](../architecture/data-and-domain.md)

---

## What to test (and what not to)

| Layer                                              | Test?                                                                   | How                                                                                               |
|----------------------------------------------------|-------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| **ViewModel**                                      | Yes — this is where your tool's behavior lives.                         | JUnit 5, MockK, Turbine. Mock interactors, drive `Action`s, assert `UiState`/`Event` emissions.   |
| **Interactor**                                     | Yes, once it does more than pass through.                               | JUnit 5, MockK, Turbine. Mock repositories/SDK interactors, assert outputs and side-effect calls. |
| **Mapper / pure utility**                          | Yes.                                                                    | JUnit 5, no mocks needed — pure functions.                                                        |
| **Repository**                                     | Yes when there's real branching (caching, fallback, combining sources). | JUnit 5, MockK. Fake the underlying `SharedPreferences`/DAO.                                      |
| Room DAO bodies                                    | No (Room generates them).                                               | Test the repository that uses them instead.                                                       |
| Composables                                        | No automated unit tests by default.                                     | Verify visually by launching the tool in the host; `@Preview` is useful during development.       |
| SDK types themselves (`CommonMapInteractor`, etc.) | No — external to your tool.                                             | Test your own interactor/repository code that calls them, using a mock/fake.                      |

If a class is hard to unit-test, it's almost always because it instantiates its
collaborators internally instead of receiving them through the constructor. Constructor-
inject everything (repositories, interactors, SDK domain interactors) — see
[Architecture for plugins](../architecture/architecture-for-plugins.md) and
[Data & domain](../architecture/data-and-domain.md).

---

## Toolchain

- **Runner:** JUnit 5 (`useJUnitPlatform()`). Configure your module's test task for JUnit
  Platform — no JUnit 4.
- **Mocking:** [MockK](https://mockk.io). Use `relaxed = true` for collaborators with a large
  call surface where only a couple of calls matter to the test.
- **Flows:** [Turbine](https://github.com/cashapp/turbine) for asserting event-`Flow`
  emissions in sequence. For `StateFlow`, reading `.value` directly after driving an
  `Action` is often simpler than using Turbine.
- **Coroutines:** `kotlinx-coroutines-test` (`runTest`, `UnconfinedTestDispatcher`,
  `StandardTestDispatcher`).

This samples repository's own build ships no unit tests, so none of these are in its version
catalog; add the ones you need to your own tool project's version catalog. They are ordinary, publicly
published libraries — resolve their current versions the same way you resolve any other
dependency for your project (Maven Central / your version catalog tooling), rather than
copying a version number from here.

Typical test-only dependencies for a JVM/unit-test source set:

```kotlin
testImplementation(kotlin("test"))
testImplementation(libs.junit.jupiter)          // org.junit.jupiter:junit-jupiter
testImplementation(libs.mockk)                  // io.mockk:mockk
testImplementation(libs.turbine)                // app.cash.turbine:turbine
testImplementation(libs.kotlinx.coroutines.test) // org.jetbrains.kotlinx:kotlinx-coroutines-test
```

---

## ViewModel test pattern

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyToolViewModelTest {

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockInteractor(): MyToolInteractor = mockk(relaxed = true) {
        every { observeShowDescription(any()) } returns MutableStateFlow(true)
    }

    @Test
    fun `Reset sets count to zero and emits CounterReset`() = runTest {
        val viewModel = MyToolViewModel()

        viewModel.handleAction(MyToolViewModel.Action.Increment)
        viewModel.handleAction(MyToolViewModel.Action.Reset)

        assertEquals(0, viewModel.uiState.value.count)
    }

    @Test
    fun `Reset emits a CounterReset event`() = runTest {
        val viewModel = MyToolViewModel()

        viewModel.event.test {
            viewModel.handleAction(MyToolViewModel.Action.Reset)
            assertEquals(MyToolViewModel.Event.CounterReset, awaitItem())
        }
    }
}
```

Rules of thumb:

- **Use `UnconfinedTestDispatcher()` as Main** when the test reads `StateFlow.value`
  synchronously right after dispatching an `Action`. Use `StandardTestDispatcher()` +
  `advanceUntilIdle()` when you need precise control over coroutine ordering (e.g. an
  interactor launches work into its own scope and you need to wait for it deterministically).
- **Collect the event `Flow` with Turbine's `.test { }`** rather than `.first()` when you
  want to assert more than one emission, or assert that *nothing else* is emitted
  (`expectNoEvents()` / `cancelAndIgnoreRemainingEvents()`).
- Mock every constructor dependency (interactors, not repositories, if the ViewModel only
  ever talks to an interactor) — this is exactly what the layering in
  [Architecture for plugins](../architecture/architecture-for-plugins.md) buys you: a ViewModel test never
  needs `SharedPreferences`, Room, or a running host.

---

## Interactor test pattern

Same shape, one layer down — mock the repository (or SDK domain interactor), assert the
interactor's outputs and side-effect calls:

```kotlin
class MyToolInteractorTest {

    @Test
    fun `setShowDescription forwards to the repository`() {
        val repository = mockk<MyToolRepository>(relaxed = true)
        val interactor = MyToolInteractor(repository)

        interactor.setShowDescription(true)

        verify { repository.setShowDescription(true) }
    }
}
```

---

## Running tests

```bash
./gradlew :your-module:testDebugUnitTest
```

Prefer this module-scoped task over a root-level `test` task once your project grows beyond
one module — it keeps iteration fast and matches which module you actually changed.
