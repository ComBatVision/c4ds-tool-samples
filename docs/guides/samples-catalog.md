# Samples guidebook

**[← README](../../README.md)** · **[Getting started](getting-started.md)** · **[Plugin isolation](plugin-isolation.md)**

The developer guidebook for this repository: all 27 samples with a screenshot, description, SDK APIs,
source path, and verification steps — grouped by the 13 catalog sections in on-screen order. Each
section below is collapsible; expand the ones you are extending.

**Package root:** `vision.combat.c4.ds.sample.*`
**Hub:** `gallery/.../catalog/CatalogTool` — the only launcher-visible tool in `:gallery`.
**Registry source of truth:** [`CatalogSection.kt`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/catalog/ui/CatalogSection.kt) / [`CatalogEntry.kt`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/catalog/ui/CatalogEntry.kt)

**Sections:**
[1 Map View](#section-1-map-view) ·
[2 Map Overlays](#section-2-map-overlays) ·
[3 Map Underlay](#section-3-map-underlay) ·
[4 Panel Windows](#section-4-panel-windows) ·
[5 Panel Management](#section-5-panel-management) ·
[6 UI Components Catalog](#section-6-ui-components-catalog) ·
[7 Tool Dialogs](#section-7-tool-dialogs) ·
[8 Tool Management](#section-8-tool-management) ·
[9 Model Management](#section-9-model-management) ·
[10 Data Management](#section-10-data-management) ·
[11 Lifecycle & Services](#section-11-lifecycle-services) ·
[12 Host Services](#section-12-host-services) ·
[13 Resources & Isolation](#section-13-resources-isolation)

---

## How to navigate

The gallery uses a **3-level** navigation hierarchy:

| Level | Screen | Description |
|---|---|---|
| 1 | **Category list** (root) | 14 category cards, each showing a title, description, and icon. Tap a card to drill into that category. |
| 2 | **Category detail** | Filtered list of samples in the chosen category. Tap a sample row to activate/deactivate it; tap the **ⓘ** icon to see details. |
| 3 | **Sample detail** | SDK APIs, source subpackage, and cross-APK install steps (where applicable). |

| Action | Result |
|---|---|
| Open **Sample Gallery** from host Tools list | Category list with 14 section cards |
| **Tap** a category card | Opens the category subscreen showing samples in that section |
| **Tap** an inactive sample row | Activates that sample (`ToolManager.activate<T>(FLAG_COMPONENT_ON_TOP)`); name highlighted in accent color |
| **Tap** an active sample row (accent color) | Deactivates that sample via `ToolManager.deactivate`; name returns to normal color |
| **Deactivate all** button in the app bar (root) | Deactivates every active gallery tool except the hub itself; shows a confirmation toast |
| **Info icon** (ⓘ) on a sample row | Navigates to the sample detail screen |
| Install `:isolation` APK | Enables **Native / Cross-APK** row in the Resources & Isolation section |

Registry implementation: [`CatalogEntry.kt`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/catalog/ui/CatalogEntry.kt)

---

## Repository layout

```
c4ds-tool-samples/
├── gallery/                     # Main APK — Sample Gallery hub + 26 feature samples
│   └── src/main/kotlin/vision/combat/c4/ds/sample/gallery/
│       ├── catalog/             # Hub: CatalogSection, CatalogEntry, CatalogTool (launcher-visible)
│       ├── mapview/
│       │   ├── map/             # Map — AbstractMapTool, tap handling
│       │   ├── renderable/      # Renderables — WorldWind primitives
│       │   └── mapinteractor/   # Map Interactor — CommonMapInteractor
│       ├── mapoverlays/
│       │   ├── overlay/         # Overlay — ToolComponent.Overlay
│       │   ├── status/          # Status — ToolComponent.Status
│       │   ├── expandablestatus/# Expandable Status
│       │   └── endbar/          # End Bar — EndBar button API
│       ├── underlay/            # Map Underlay — ToolComponent.Underlay
│       ├── window/
│       │   ├── singlescreen/    # Single Screen Window
│       │   ├── multiscreen/     # Multi Screen Window — AppNavHost, subDI
│       │   └── map/             # Secondary Map Window — ToolComponent.MapWindow
│       ├── panelstate/          # Panel Management — PanelManager
│       ├── toolmanagement/      # Tool Management — ToolManager
│       ├── uicatalog/           # UI Components Catalog
│       ├── dialog/              # Tool Dialogs — ToolDialog variants
│       ├── model/               # Model Management — CommonModelInteractor
│       ├── storage/             # Data Management — files, SharedPreferences, Room
│       ├── network/             # Data Management — network requests with the host-provided Ktor client
│       ├── service/             # Lifecycle & Services — AbstractToolService
│       ├── hostservices/        # Host Services — ShareManager, LocalClipboard, InAppNotificationManager
│       ├── hostlibs/            # Host Libraries — Coil, Accompanist, mil-sym (obfuscation-safe)
│       ├── openwith/            # Open With — ShareManager.getShareableUri + custom ACTION_VIEW chooser
│       └── resources/
│           ├── config/          # Config-Qualified Resources
│           ├── material/        # M2 Widgets & Popup Isolation
│           └── collision/       # R.string Collision
├── isolation/                   # Second APK — JNI + asset isolation (cross-APK activation)
│   └── src/main/kotlin/vision/combat/c4/ds/sample/isolation/
│       └── nativelib/           # Native / Cross-APK — NativeToolDescriptor
└── docs/                        # This guidebook and the deep-dive docs
```

Only **Sample Gallery** (`CatalogToolDescriptor`) appears in the host launcher from `:gallery`. All
other gallery tools — including **Open With** — use `categories = emptyList()` and launch from the
hub via `ToolManager`.

---

## The 14 sections

<a id="section-1-map-view"></a>
<details>
<summary><strong>🗺️ Section 1 — Map View</strong> · 3 samples — <em>Map rendering, renderables, and the CommonMapInteractor API.</em></summary>

#### Map

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/39f84bc1-e965-415a-a018-df4ec26dba8e" width="260" alt="Map sample — detail panel with marker placement prompt on the map">
</td>
<td valign="top">

Handles map taps and draws a placemark on the tapped location using `AbstractMapTool` — tap
handling, renderable layers, status bar integration, and an info window.

**SDK APIs:** `AbstractMapTool`, `RenderableLayer`, `SelectDragCallback`, map tap callbacks, `ToolComponent.Status`, `ToolComponent.Window`, `shouldShowCoordinates`, `shouldShowAzimuth`.

**Source:** [`gallery/.../mapview/map/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapview/map) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapview.map.MapToolDescriptor`

**Verify:** Tap terrain → placemark appears → status bar shows coordinates/azimuth → open info window for interaction overview.

</td>
</tr>
</table>

#### Renderables

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/e6e48f91-51cd-4f7a-a3c2-16d876ab2998" width="260" alt="Renderables sample — shape color and add point/line/polygon controls with shapes on the map">
</td>
<td valign="top">

Draws WorldWind renderables — point, polyline, polygon, circle, label — on the map with
color/size customization.

**SDK APIs:** `RenderableLayer`, `addRenderable`, `removeAllRenderables`, WorldWind `Placemark`/`Path`/`Polygon`/`Ellipse`/`Label`, `requestRedraw`, `ToolComponent.Window`, `requiredComponent`, `WindowScaffold`.

**Source:** [`gallery/.../mapview/renderable/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapview/renderable) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapview.renderable.RenderableToolDescriptor`

**Verify:** Window opens → add each renderable type → adjust color/size → renderables appear on map → clearing removes all.

</td>
</tr>
</table>

#### Map Interactor

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/a2a23197-e9cc-458e-a8d7-8e4eeb590bfb" width="260" alt="Map Interactor sample — showcase hub with Camera & LookAt, Focus camera, Display mode, Cursor pin, and Corrections entries">
</td>
<td valign="top">

Live `CommonMapInteractor` readout and controls for camera, display mode, reticle, cursor, focus,
and magnetic corrections.

**SDK APIs:** `CommonMapInteractor`, `mapNavigatorEvent`, `camera`, `lookAt`, `selectedPosition`, `isLookAtAboveHorizon`, `mapDisplayMode`, `updateMapDisplayMode`, `arDistanceLimit`, `setArDistanceLimit`, `isReticleVisible`, `setReticleVisible`, `isCursorPinned`, `pinCursor`, `unpinCursor`, `isMapVisible`, `setMapVisible`, `focusOnLocation`, `focusOnSector`, `getDeclination`, `getConvergence`, `getAngleCorrection`. Also `CommonModelInteractor.userModel` for focus-on-user.

**Source:** [`gallery/.../mapview/mapinteractor/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapview/mapinteractor) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapview.mapinteractor.MapInteractorToolDescriptor`

**Verify:** Launch → window shows live camera/LookAt readout → switch display mode → toggle reticle/map visibility → pin/unpin cursor → **Focus on cursor** moves map to cursor position → **Focus on user** moves map to user location (no-op when no GPS fix) → declination/convergence values update at LookAt.

</td>
</tr>
</table>

</details>

<a id="section-2-map-overlays"></a>
<details>
<summary><strong>🧭 Section 2 — Map Overlays</strong> · 5 samples — <em>Overlays, status bars, expandable status, EndBar buttons, and default-overlay replace/restore.</em></summary>

#### Overlay

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/6c0a63f3-87d0-43eb-ac1c-2ad07d53f6e8" width="260" alt="Overlay sample active — overlay composable showing cursor position and user model">
</td>
<td valign="top">

`ToolComponent.Overlay` composable reading cursor position and user model, with an end-bar close
button.

**SDK APIs:** `ToolComponent.Overlay`, `CommonMapInteractor.selectedPosition`, `CommonModelInteractor.userModel`, `CommonLocaleSettingsInteractor.coordinateSystemFormat`, `AbstractTool.endBar`, `EndBarActionButton`.

**Source:** [`gallery/.../mapoverlays/overlay/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapoverlays/overlay) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapoverlays.overlay.OverlayToolDescriptor`

**Verify:** Overlay visible on map → cursor coordinates update when panning → user model name appears when set.

</td>
</tr>
</table>

#### Overlay isDefault

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/9c4eab1a-50f7-46fb-8b36-d4847bfa5e1b" width="260" alt="Overlay isDefault demo — hub window driving Demo A/B, showing the active tool's default overlay badge on the map">
</td>
<td valign="top">

A hub window (`overlay_default_tool_name` in-app) drives two hidden demo tools, each declaring its
own `ToolComponent.Overlay(isDefault = true)`. Activating Demo B auto-shows B's overlay, which
displaces Demo A's default overlay in the exclusive overlay region; deactivating Demo B
automatically restores Demo A's overlay. The sample uses only `ToolManager.activate`/`deactivate`
on the two demo descriptors — no `showComponent`/`hideComponent` calls. Teaching point: the
`isDefault` contract — default components are auto-shown on tool start and "restored after another
component that caused this one to hide is now hidden."

**SDK APIs:** `ToolComponent.Overlay`, `component(isDefault = true)`, `requiredComponent`, `ToolManager.activate`, `ToolManager.deactivate`, `ToolManager.isActive`, `ToolManager.activeTools`.

**Source:** [`gallery/.../mapoverlays/overlayisdefault/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapoverlays/overlayisdefault) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapoverlays.overlayisdefault.OverlayDefaultToolDescriptor`

**Verify:** Activate Demo A → Demo A's badge appears → Activate Demo B → Demo A's badge is replaced by Demo B's badge automatically → Deactivate Demo B → Demo A's badge reappears automatically.

</td>
</tr>
</table>

#### Status

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/d84b661d-8ae7-4f5d-ba7c-cac9f177f744" width="260" alt="Status sample active — host-rendered coordinates and azimuth status bar">
</td>
<td valign="top">

Status bar with host coordinate/azimuth chrome flags; host chrome renders coordinates and azimuth.

**SDK APIs:** `ToolComponent.Status`, `statusComponent`, `Status.shouldShowCoordinates`, `Status.shouldShowAzimuth`.

**Source:** [`gallery/.../mapoverlays/status/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapoverlays/status) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapoverlays.status.StatusToolDescriptor`

**Verify:** Status strip visible at bottom → host coordinates and azimuth update with map interaction.

</td>
</tr>
</table>

#### Expandable Status

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/54ccf187-9df7-4275-bbf1-baa377834810" width="260" alt="Expandable Status sample active and expanded — Position Below/Above toggle">
</td>
<td valign="top">

Collapsible status panel above or below the strip. Expand and collapse via the host chevron above
the status bar, or the end-bar toggle button.

**SDK APIs:** `ToolComponent.ExpandableStatus`, `expandableStatusComponent`, `ExpandableStatus.isExpanded`, `ExpandableStatus.shouldShowAbove`, `AbstractTool.endBar`, `EndBarToggleButton`.

**Source:** [`gallery/.../mapoverlays/expandablestatus/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapoverlays/expandablestatus) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapoverlays.expandablestatus.ExpandableStatusToolDescriptor`

**Verify:** Toggle expand/collapse → panel moves above/below per **Show above** setting.

</td>
</tr>
</table>

#### End Bar

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/9b6722fc-931f-448b-8e2e-8eb7954dd7f8" width="260" alt="End Bar sample active — EndBar menu open with Option A, slider, and toggle state ON">
</td>
<td valign="top">

Full EndBar API — action, toggle, and menu buttons on the map's EndBar (each with a distinct icon),
including checkable menu items and a slider.

**SDK APIs:** `AbstractTool.endBar`, `EndBarActionButton`, `EndBarToggleButton`, `EndBarMenuButton`, `EndBarMenuScope.Checkable`, `EndBarMenuScope.Slider`.

**Source:** [`gallery/.../mapoverlays/endbar/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/mapoverlays/endbar) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.mapoverlays.endbar.EndBarToolDescriptor`

**Verify:** Three distinct icons appear on the end bar → action fires toast → toggle state reflected in window → menu items and slider work.

</td>
</tr>
</table>

</details>

<a id="section-3-map-underlay"></a>
<details>
<summary><strong>🌄 Section 3 — Map Underlay</strong> · 1 sample — <em>Composables rendered behind the map layer in AR mode.</em></summary>

#### Map Underlay

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/3f7dc08a-d30c-4975-92ba-163062926f63" width="260" alt="Map Underlay sample active — underlay visible behind the map in AR mode with the gallery list open">
</td>
<td valign="top">

`ToolComponent.Underlay` — a full-screen composable layer rendered behind the map. The host enables
AR map mode while any underlay is active.

**SDK APIs:** `ToolComponent.Underlay`, `requiredComponent`, `AbstractTool.endBar`, `EndBarActionButton`, `ToolManager.deactivate`.

**Source:** [`gallery/.../underlay/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/underlay) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.underlay.UnderlayToolDescriptor`

**Verify:** Semi-transparent underlay visible behind map content; close via EndBar action.

</td>
</tr>
</table>

</details>

<a id="section-4-panel-windows"></a>
<details>
<summary><strong>🪟 Section 4 — Panel Windows</strong> · 3 samples — <em>Single-screen, multi-screen, and secondary-map panel windows.</em></summary>

#### Single Screen Window

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/6eaf5e88-ee73-44eb-b4bb-7b9f362c4181" width="260" alt="Single Screen Window sample — counter at 0 with Increment and Reset buttons">
</td>
<td valign="top">

Minimal single-screen tool window with a ViewModel-backed counter, using MVI state and a one-shot
toast event.

**SDK APIs:** `ToolComponent.Window`, `requiredComponent`, `WindowScaffold`, `BackNavTopAppBar`, `diViewModel()`, `showToast`.

**Source:** [`gallery/.../window/singlescreen/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/window/singlescreen) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.window.singlescreen.WindowSingleScreenToolDescriptor`

**Verify:** Window opens → **Increment** increases counter → **Reset** resets counter and shows toast.

</td>
</tr>
</table>

#### Multi Screen Window

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/6d2fa0a4-b890-4a53-8791-07808d7d4599" width="260" alt="Multi Screen Window sample — Home screen with Settings action in the app bar">
</td>
<td valign="top">

Multi-screen window using `AppNavHost`, a tool-scoped DI module, and persisted `SharedPreferences`
settings shared across screens.

**SDK APIs:** `AppNavHost`, `Route`, navigation transitions, `BackNavTopAppBar`, `subDI { import(module) }`, tool-scoped `SharedPreferences`.

**Source:** [`gallery/.../window/multiscreen/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/window/multiscreen) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.window.multiscreen.WindowMultiScreenToolDescriptor`

**Verify:** Home screen → navigate to Settings → toggle → navigate back → description visibility matches toggle.

</td>
</tr>
</table>

#### Secondary Map Window

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/cb029d5f-e2dc-47ad-b07d-74b86ab07ef3" width="260" alt="Secondary Map Window sample — embedded second map inside a tool window">
</td>
<td valign="top">

Embeds a secondary map inside a tool window via `ToolComponent.MapWindow`, adding supplemental
end-bar and nav-bar actions alongside the host's default zoom controls.

**SDK APIs:** `ToolComponent.MapWindow`, embedded `MapView`, `MapController`, `MapController.InteractionMode`, `MapWindow.mapEndBarButtons`, `MapWindow.navBarContent`, `MapWindow.focusCameraOn`.

**Source:** [`gallery/.../window/map/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/window/map) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.window.map.MapWindowToolDescriptor`

**Verify:** Window opens with embedded map → zoom in/out work → mode selector switches view → Focus camera button moves map.

</td>
</tr>
</table>

</details>

<a id="section-5-panel-management"></a>
<details>
<summary><strong>📐 Section 5 — Panel Management</strong> · 1 sample — <em>Open, close, and observe panel state via PanelManager.</em></summary>

#### Panel Management

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/b529339c-d54c-4d94-a5fe-e4930ff9f43b" width="260" alt="Panel Management sample — full-screen panel showing state Opened Full with Open Half, Open Full, and Close Panel buttons">
</td>
<td valign="top">

Open (Half/Full) and close the panel via `PanelManager`, observing live `PanelState` through a
`StateFlow`.

**SDK APIs:** `PanelManager.openPanel(PanelState.Opened.Half)`, `PanelManager.openPanel(PanelState.Opened.Full())`, `PanelManager.closePanel()`, `PanelManager.panelState: StateFlow<PanelState>`, `PanelState.Closed`.

**Source:** [`gallery/.../panelstate/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/panelstate) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.panelstate.PanelStateToolDescriptor`

**Verify:** Window opens → **Open Half** opens panel to half → **Open Full** expands → **Close** closes → current state label updates live.

</td>
</tr>
</table>

</details>

<a id="section-6-ui-components-catalog"></a>
<details>
<summary><strong>🧩 Section 6 — UI Components Catalog</strong> · 1 sample — <em>Promoted public SDK components: form fields, buttons, inputs, and more.</em></summary>

#### UI Components Catalog

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/440f5534-45ec-4390-9b9e-585d728ba2e1" width="260" alt="UI Catalog component list — InlineMessage, HeaderField, ExpandableField, FormFieldBox, NestedForm rows">
</td>
<td valign="top">

Navigable catalog of promoted public SDK components: six form fields plus Buttons, TopAppBar,
Inputs, Selection, Feedback, and Lists groups, each shown in several states. The **Hostility
Selector** demo renders bare MIL-STD-2525 affiliation frames via the promoted `HostilityItem` for
each affiliation (Unknown, Friend, Hostile, Neutral, Suspect) — not a specific unit symbol, and not
plain colored swatches.

**SDK APIs:** `InlineMessage`, `HeaderField`, `ExpandableField`, `FormFieldBox`, `NestedForm`, `HostilitySelector`, `HostilityItem`, buttons, inputs, selection, feedback, revealable lists, `AppNavHost`, `WindowScaffold`.

**Source:** [`gallery/.../uicatalog/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/uicatalog) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.uicatalog.UiCatalogToolDescriptor`

**Verify:** Launch → component list opens → tap a component → detail screen shows each documented state → back navigation returns to list. In the Hostility Selector demo, each affiliation swatch shows its MIL-STD-2525 symbol.

</td>
</tr>
</table>

Sub-screens:

<table>
<tr>
<td align="center"><img src="https://github.com/user-attachments/assets/e2d67768-1d81-4d10-a484-85b3a8c2530d" width="160" alt="InlineMessage demo"><br><sub>InlineMessage</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/c16cf296-0fb5-4f6b-b8fb-cd393dbd94a6" width="160" alt="HeaderField demo"><br><sub>HeaderField</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/5273feea-f5ec-4d58-91c2-b615796cde65" width="160" alt="ExpandableField demo"><br><sub>ExpandableField</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/c80c7d1c-3566-4c9b-859c-6f44ce5a41f7" width="160" alt="FormFieldBox demo"><br><sub>FormFieldBox</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/e40c8978-e5ec-420f-a4f1-fa663da6d181" width="160" alt="NestedForm demo"><br><sub>NestedForm</sub></td>
</tr>
<tr>
<td align="center"><img src="https://github.com/user-attachments/assets/b1eaa262-80de-4dd7-8b3a-31c0a8c4b719" width="160" alt="Buttons demo — Primary, Outlined, Text, Destructive"><br><sub>Buttons</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/76fcfd67-916e-4a73-8f7b-0a3d698bc1d1" width="160" alt="TopAppBar demo — title-only, back nav, subtitle states"><br><sub>TopAppBar</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/8cf5f4ff-8ed8-4c3f-84c2-dc1045ca7b27" width="160" alt="Inputs demo — text input, integer stepper, dropdowns"><br><sub>Inputs</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/b1831c41-89ab-459c-9aaa-661f38306a11" width="160" alt="Feedback demo — AppDialog and Info/Warning/Error banners"><br><sub>Feedback</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/70664d5c-7791-4795-8298-cee6d2baac7a" width="160" alt="Revealable Lists demo — swipe-to-delete revealed"><br><sub>Lists</sub></td>
</tr>
</table>

</details>

<a id="section-7-tool-dialogs"></a>
<details>
<summary><strong>💬 Section 7 — Tool Dialogs</strong> · 1 sample — <em>ToolDialog variants: Confirmation, Destructive, Info, and Custom.</em></summary>

#### Tool Dialogs

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/2958cead-29ef-473c-8048-b2be3cada782" width="260" alt="Tool Dialogs sample — Destructive dialog 'Delete item' with Cancel and Delete over the variants screen">
</td>
<td valign="top">

All four `ToolDialog` variants shown via `AbstractTool.showDialog` / `dismissDialog`.

**SDK APIs:** `ToolDialog.Confirmation`, `.Destructive`, `.Info`, `.Custom`, `AbstractTool.showDialog()`, `dismissDialog()`.

**Source:** [`gallery/.../dialog/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/dialog) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.dialog.DialogToolDescriptor`

**Verify:** Four buttons each open the correct dialog type; confirm and dismiss work for every variant.

</td>
</tr>
</table>

</details>

<a id="section-8-tool-management"></a>
<details>
<summary><strong>🎛️ Section 8 — Tool Management</strong> · 1 sample — <em>Activate, deactivate, and inspect tools via ToolManager.</em></summary>

#### Tool Management

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/3e6a7c94-6871-41d0-9bf9-c2c8f453601c" width="260" alt="Tool Management sample — Map Tool Control with Map tool active: No and Activate/Deactivate buttons">
</td>
<td valign="top">

Demonstrates `ToolManager`: activate/deactivate/`isActive` against the Map sample, `showComponent`
to bring its window forward, and observing `activeTools` live.

**SDK APIs:** `ToolManager.activate`, `ToolManager.deactivate`, `ToolManager.isActive`, `ToolManager.activeTools: StateFlow`, `ToolManager.showComponent`, `ToolManager.FLAG_COMPONENT_ON_TOP`.

**Source:** [`gallery/.../toolmanagement/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/toolmanagement) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.toolmanagement.ToolManagementToolDescriptor`

**Verify:** Activate Map tool → active list updates while the Tool Management window stays on top (its buttons remain usable) → **Show Map Window** brings the Map window forward → deactivate Map tool → list updates again.

</td>
</tr>
</table>

> [!NOTE]
> `ToolManager.activate()` always shows the activated tool's required components, so activating the
> Map tool would normally push the Map window over this sample's own window. The sample therefore
> activates with `FLAG_NONE` and immediately re-shows its own window via
> `ToolManager.showComponent(window, FLAG_COMPONENT_ON_TOP)`, keeping its buttons usable while the
> Map tool runs. Copy this pattern for any control-panel tool that activates other tools.

</details>

<a id="section-9-model-management"></a>
<details>
<summary><strong>🪖 Section 9 — Model Management</strong> · 1 sample — <em>CommonModelInteractor create/consume/commit, symbol keys, and model selection events.</em></summary>

#### Model Management

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/6b1f1071-7714-4b67-9702-064103ef5203" width="260" alt="Model Management showcase hub — Models list, Create/consume & commit, Symbol keys, Selection & events entries">
</td>
<td valign="top">

Demonstrates `CommonModelInteractor` for battlespace models: list, create/consume/commit, symbol
keys, and selection & events — plus read-only awareness.

**SDK APIs:** `CommonModelInteractor`, `getAllModels`, `createModel`, `consumeModel`, `commitModel`, `rollbackChanges`, `deleteModel`, `selectModel`, `unselectModel`, `followModel`, `startModelInteraction`, `selectedModel`, `userModel`, `isReadOnly`, `ModelAttrs`/`symbolKey`, `rememberSymbolPainter`, `diViewModel()`.

**Source:** [`gallery/.../model/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/model) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.model.ModelToolDescriptor`

**Verify:** Model list populated → tap row to select → **Unselect** clears selection → read-only banner shown when `isReadOnly` is true.

</td>
</tr>
</table>

Sub-screens:

<table>
<tr>
<td align="center"><img src="https://github.com/user-attachments/assets/f32cb236-f867-449f-83b8-a6a2ce43c3fb" width="190" alt="Models list — 2 Hostile and 1 Friend model"><br><sub>Models list</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/c1692674-9635-47dc-b13a-269a24668a13" width="190" alt="Create, consume and commit — status consumed/staged"><br><sub>Create, consume &amp; commit</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/341a946d-ced9-4b0e-8ab1-7ab8b80a1d15" width="190" alt="Symbol keys — Rifleman, Air track, Civil disturbance, Fire event"><br><sub>Symbol keys</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/d6ac17e7-4c5d-4ccc-9a39-94c4a89e494c" width="190" alt="Selection and events — Selected: Sample model 1 with Follow/Unselect"><br><sub>Selection &amp; events</sub></td>
</tr>
</table>

</details>

<a id="section-10-data-management"></a>
<details>
<summary><strong>💾 Section 10 — Data Management</strong> · 2 samples — <em>Isolated file I/O, plugin-scoped SharedPreferences, an isolated Room database, and network requests with the host-provided Ktor client.</em></summary>

#### Data Management

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/1c08f80e-2286-4b0e-8c92-e46a5618eb7c" width="260" alt="Data Management showcase hub — File I/O, SharedPreferences, Room Database entries">
</td>
<td valign="top">

Isolated file I/O, plugin-scoped `SharedPreferences`, and an isolated Room database — three storage
showcases in one hub. File I/O displays session directory paths and writes/reads a file off the
main thread.

**SDK APIs:** `CommonSessionStorageInteractor` (`getRootDirectoryPath()`, `getUserDirectoryPath()`), `SharedPreferences`, Room, `Dispatchers.IO`, `viewModelScope.launch`, `File.writeText`, `File.readText`.

**Source:** [`gallery/.../storage/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/storage) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.storage.StorageToolDescriptor`

**Verify:** Window shows root and user directory paths → **Write File** writes `gallery_sample.txt` → **Read File** reads it back and displays contents → **Read** before **Write** shows "file not found" hint rather than crashing.

</td>
</tr>
</table>

Sub-screens:

<table>
<tr>
<td align="center"><img src="https://github.com/user-attachments/assets/49258059-1f48-4590-a9c3-90e538c2e6f4" width="190" alt="File I/O — session directory paths with Write/Read File"><br><sub>File I/O</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/acfc70f2-b45a-4113-bbe1-5b0a3ac15bd8" width="190" alt="SharedPreferences — stored string and counter with Save String"><br><sub>SharedPreferences</sub></td>
<td align="center"><img src="https://github.com/user-attachments/assets/d89793c7-3e6a-4c7e-9fb8-5d1020ba318f" width="190" alt="Room Database — Add a Note and Clear All Notes"><br><sub>Room Database</sub></td>
</tr>
</table>

#### Network Requests

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/0b2786ad-74d6-488c-b243-d8e42845abbc" width="260" alt="Network Requests sample active — the tool window over the map showing the selected position and the Current Weather card fetched with the host-provided Ktor client">
</td>
<td valign="top">

A real HTTPS request with the Ktor client the host already provides. The tool binds its **own**
untagged `HttpClient` in its Kodein module — the SDK deliberately keeps that slot free (its shared
client is bound under `SdkRemoteTags.HTTP_CLIENT`) — and fetches current weather for the map's
selected position from the keyless Open-Meteo API. The response DTO is mapped to a domain model
behind a `WeatherRepository` interface; every Ktor and serialization class comes from the host via
`compileOnly(c4ds-sdk)`, so nothing network-related is bundled into the APK.

**SDK APIs:** `HttpClient(Android)`, `ContentNegotiation` + kotlinx-serialization `json`, `CommonMapInteractor.selectedPosition`, `CommonLocaleSettingsInteractor.coordinateSystemFormat`, `diViewModel`.

**Source:** [`gallery/.../network/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/network) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.network.NetworkToolDescriptor`

**Verify:** Select a position on the map → open **Network Requests** → the window shows that position → **Fetch Weather** shows a progress row, then the Current Weather card (condition, temperature, humidity, wind, observation time) → with connectivity off, **Fetch Weather** shows a "Request failed" toast instead of crashing.

</td>
</tr>
</table>

</details>

<a id="section-11-lifecycle-services"></a>
<details>
<summary><strong>🔄 Section 11 — Lifecycle &amp; Services</strong> · 1 sample — <em>A session AbstractToolService doing background work, an unread badge, and a live lifecycle log.</em></summary>

#### Lifecycle & Services

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/72846bca-a734-4d20-b5bb-a86ff5a5fe24" width="260" alt="Lifecycle & Services sample — session service active, Inbox with unread count and Mark all read">
</td>
<td valign="top">

A session-scoped `AbstractToolService` does background work all session — even while the window is
closed — and posts an unread badge on the tool list item. The window also logs the tool lifecycle
callbacks as they fire.

**SDK APIs:** `ToolDescriptor.createService`, `AbstractToolService`, `AbstractToolService.toolSubDI`, `ToolNotificationManager.counter`, `AbstractTool.onComponentShown`/`onComponentHidden`/`onDestroyRequested`.

**Source:** [`gallery/.../service/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/service) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.service.ServiceToolDescriptor`

**Verify:** Start session → service ticks in background → tool list badge increments → open tool window → event counter matches badge.

</td>
</tr>
</table>

</details>

<a id="section-12-host-services"></a>
<details>
<summary><strong>🔗 Section 12 — Host Services</strong> · 3 samples — <em>Host-provided, plugin-accessible services: sharing, clipboard, in-app notifications, opening a file in another app, and the libraries the host puts on your classpath — no bundling required.</em></summary>

Both samples appear as rows in the **Host Services** catalog section:

<img src="https://github.com/user-attachments/assets/486968c3-4446-4cee-9f22-2e2cc5a34033" width="260" alt="Sample Gallery — Host Services catalog section listing the Host Services and Open With sample rows">

#### Host Services

<table>
<tr>
<td width="280" valign="top">

<img src="https://github.com/user-attachments/assets/76d37a2c-5899-4355-b80b-92e8ccbf2fb1" width="260" alt="Host Services sample active — Share section with Share link, Share coordinates, Share file, and Share image buttons over the map">

</td>
<td valign="top">

Three host-provided services demonstrated with **zero bundling** — pure `compileOnly(c4ds-sdk)`.
This tool's UI code runs in the HOST process (the normal case for plugin UI), so all three
services are simply on the classpath: **Share** (link, coordinates, file, image) via
`LocalShareManager`, provided by the host's `MainScreen` and re-exported from `c4ds-sdk-core:ui`
through `c4ds-sdk`'s `api(...)` dependency; **Clipboard** via Compose's own `LocalClipboard`; and
**In-app notification** via `InAppNotificationManager`, resolved through Kodein DI
(`rememberInstance`).

The notification also shows *where* plugin resources may be read. `InAppNotificationModel.content`
is authored by the tool but composed by the **host**, next to the map — so the sample renders it
with `painterResource(R.drawable.ic_notification)` and `stringResource(...)` called **inside** the
content lambda rather than hoisted into the tool window. That is safe because `AbstractTool` binds a
tool-scoped `InAppNotificationManager` that wraps posted content in the tool's environment (tool
`Context`, DI scope, `ViewModelStore`): plugin ids resolve against this APK's resource table, and
re-resolve when the host's app language or night mode changes. Read against the host's context, the
same ids would be a wrong resource or a `Resources.NotFoundException`.

Its **Share file** button shares a file via the OS share sheet (`ShareManager.shareFile`) — for
the alternative pattern of minting a Uri and building your own intent (`ShareManager.getShareableUri`
+ a custom `ACTION_VIEW` chooser), see the **Open With** sample below.

**SDK APIs:** `ShareManager`/`LocalShareManager` (`shareLink`, `shareCoordinates`, `shareFile`, `shareImage`), Compose `LocalClipboard`/`ClipEntry`, `InAppNotificationManager` (`postTransientNotification`, `InAppNotificationModel`, `InAppNotificationData`), `IconWithText`, plugin `painterResource`/`stringResource` inside notification content, Kodein `rememberInstance`.

**Source:** [`gallery/.../hostservices/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/hostservices) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.hostservices.HostServicesToolDescriptor`

**Verify:** Tap **Share link** / **Share coordinates** / **Share file** / **Share image** → chooser opens for each. Tap **Copy sample text** → clipboard confirmation appears. Tap **Post notification** → a transient in-app notification appears over the map showing the plugin's own bell drawable next to the plugin's own string (no crash, no host resource), and a posted confirmation is shown.

</td>
</tr>
</table>

#### Host Libraries

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/13817b63-bed4-4156-b4c7-f75e4ce840e3" width="260" alt="Host Libraries sample active — Coil, Accompanist Permissions and mil-sym sections over the map">
</td>
<td valign="top">

Uses the three host-provided libraries that no other sample touches — **Coil** (`AsyncImage` loading
this plugin's own drawable), **Accompanist Permissions** (`rememberPermissionState` for CAMERA), and
**mil-sym** (`SymbolUtilities.getBasicSymbolID2525C`). All three arrive through
`compileOnly(c4ds-sdk)`; the module declares no dependency of its own.

It exists as a **regression test you can see**. A plugin never contains host-provided classes — they
are `compileOnly`, so the plugin APK carries only *references* to them. If R8 renames one of those
classes on the host side, the reference resolves to nothing and the plugin crashes — in release
only, because debug builds are not minified. Every other gallery sample uses Compose, Kodein,
WorldWind, Ktor or Room, all of which sit under broad keep rules that were never at risk; these four
did not, and were silently obfuscated until the SDK's consumer rules were fixed.

See [Host-provided libraries](../reference/host-provided-libraries.md) for the full catalog and
versions. Note what this sample deliberately does **not** cover: OkHttp also resolves against the
host, but only so a pre-existing external plugin keeps working — **Ktor is the single network client
for plugins**, and no sample in this repository uses anything else.

**SDK APIs:** `coil3.compose.AsyncImage`, Accompanist `rememberPermissionState`/`PermissionStatus`, `armyc2.c5isr.renderer.utilities.SymbolUtilities`.

**Source:** [`gallery/.../hostlibs/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/hostlibs) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.hostlibs.HostLibsToolDescriptor`

**Verify:** Open the sample → the Coil section shows the plugin's own layered icon; the permissions
section reports CAMERA status with a request button; the symbology section prints a result string. The real test is doing this against a **release** host build — on a minified host
with the old keep rules, the window crashed instead of rendering.

</td>
</tr>
</table>

#### Open With

<table>
<tr>
<td width="280" valign="top">

<img src="https://github.com/user-attachments/assets/093d31d8-c197-4f16-be2e-33966e337f09" width="260" alt="Open With sample active — explainer text, an 'Open sample file with…' button, and the plugin-owned-FileProvider rule caption">

</td>
<td valign="top">

Demonstrates the correct pattern for a plugin that needs to hand a file to another app: it does
**not** declare its own `FileProvider`. A plugin-owned provider can never work for this, even if
its library is bundled — this tool's *code* runs in the HOST process, and a process can only grant
a `content://` Uri whose provider it itself owns; the host doesn't own a plugin's provider
(different UID), so the grant is silently dropped and the target app gets a `SecurityException`.
Instead, the tool writes a sample file under `toolContext.cacheDir/export/`, mints a **host-owned**
Uri via `ShareManager.getShareableUri` (obtained through `LocalShareManager`), and opens it with a
custom `ACTION_VIEW` chooser intent — or shows an "outside the host's shareable roots" message if
`getShareableUri` returns `null`. Compare with the sibling **Host Services** sample above, which
shares the same kind of file via the OS share sheet (`ShareManager.shareFile`) instead of a custom
intent. Like every other gallery sample it uses `categories = emptyList()` and launches from the
Sample Gallery hub, and it declares no manifest components of its own, so it bundles nothing extra —
pure `compileOnly(c4ds-sdk)`.

**SDK APIs:** `ShareManager.getShareableUri`, `LocalShareManager`, `Intent.ACTION_VIEW` +
`FLAG_GRANT_READ_URI_PERMISSION`, `ToolComponent.Window`, `requiredComponent`, `WindowScaffold`,
`BackNavTopAppBar`.

**Source:** [`gallery/.../openwith/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/openwith) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.openwith.OpenWithToolDescriptor`

**Verify:** Open Sample Gallery → Host Services → **Open With** → tap **Open sample file with…** → a chooser opens for the freshly written sample file (or, if the host is older than SDK 0.5.1 and lacks `getShareableUri`, the "outside the host's shareable roots" message appears instead).

</td>
</tr>
</table>

</details>

<a id="section-13-resources-isolation"></a>
<details>
<summary><strong>🔒 Section 13 — Resources &amp; Isolation</strong> · 4 samples — <em>Config-qualified resources, Material widgets, R.string collision, assets, native libraries (NDK), and cross-APK isolation.</em></summary>

Deep dive: **[Plugin isolation](plugin-isolation.md)** — smoke tests, isolation cases (a–e, g, h), and
cross-APK activation.

#### Config-Qualified Resources

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/1a98985f-0963-4139-8523-e25fe79277c0" width="260" alt="Config-Qualified Resources sample — day mode with locale and plugin-font demo">
</td>
<td valign="top">

Config-qualified resources — locale (`values-uk`), night mode (`values-night`), a plugin-compiled
font, and a raw resource file. Covers isolation cases [(c)](plugin-isolation.md#case-c-config-reactivity)
and [(e)](plugin-isolation.md#case-e-font--raw-resource).

**SDK APIs:** Configuration-qualified `values` / `values-night` / `values-uk`, `stringResource`, `FontFamily(Font(R.font.*))`, `openRawResource(R.raw.*)`.

**Source:** [`gallery/.../resources/config/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/resources/config) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.resources.config.ConfigToolDescriptor`

**Verify:** Toggle dark mode → mode string/icon updates → change locale to Ukrainian → all strings localize. Window shows text rendered with plugin `R.font.sample_font` and content read from `R.raw.sample_note`.

</td>
</tr>
</table>

#### M2 Widgets & Popup Isolation

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/da2b01d0-581e-4526-a39f-0940a10dbe51" width="260" alt="M2 Widgets & Popup Isolation sample — Show AlertDialog and Open dropdown menu demos">
</td>
<td valign="top">

Plugin-compiled Material 2 widgets and popup context isolation across a Compose window boundary: a
raw `DropdownMenu` shows how `LocalContext` resets in a sub-composition, and
`ProvideWindowContext`/`ToolAlertDialog` show how to re-provide it. Isolation case
[(a)](plugin-isolation.md#case-a-m2-composition-fallback).

**SDK APIs:** Plugin-local M2 (`Scaffold`, `SnackbarHost`, `Snackbar`, `AlertDialog`, `DropdownMenu`, `Slider`), `ProvideWindowContext`, `ToolAlertDialog`, and the SDK's automatic composition-context fallback (internal to the SDK, transparent to plugin authors).

**Source:** [`gallery/.../resources/material/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/resources/material) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.resources.material.MaterialToolDescriptor`

**Verify:** Window opens without crash → all four widget demos interactive.

</td>
</tr>
</table>

#### R.string Collision

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/e1184b5e-82a2-404a-99bc-c0987a74ebcd" width="260" alt="R.string Collision sample — host-vs-plugin resolution table showing plugin wins">
</td>
<td valign="top">

Plugin `R.string` values take priority over identically-named host strings — plugin-first
resolution when names collide. Isolation case [(b)](plugin-isolation.md#case-b-rstring-collision).

**SDK APIs:** Plugin `R.string.settings` vs host `R.string.settings` — plugin value wins; resolution is backed by the SDK's automatic composition-context fallback (internal — transparent to plugin authors).

**Source:** [`gallery/.../resources/collision/`](../../gallery/src/main/kotlin/vision/combat/c4/ds/sample/gallery/resources/collision) · **Descriptor:** `vision.combat.c4.ds.sample.gallery.resources.collision.CollisionToolDescriptor`

**Verify:** Window displays the plugin-specific settings string (defined in `gallery/src/main/res/values/strings.xml`), not the host default.

</td>
</tr>
</table>

#### Native / Cross-APK (`:isolation`)

<table>
<tr>
<td width="280" valign="top">
<img src="https://github.com/user-attachments/assets/ec2281f2-67b4-42a5-8bf1-b5403893cfbb" width="260" alt="Native / Cross-APK sample — JNI result isolation-jni/1.0 and asset read from the :isolation APK">
</td>
<td valign="top">

Cross-APK tool isolation: per-APK ClassLoader separation, a native `.so` loaded from the plugin's
`nativeLibraryDir`, and a plugin-scoped `AssetManager`. Lives in the separate `:isolation` APK and
is activated from the hub across the APK boundary. Isolation case
[(h)](plugin-isolation.md#case-h-cross-apk-native-so--assets).

**SDK APIs:** `ToolManager.resolveToolId(fqcn)`, `ToolContext.assets` (plugin `AssetManager`), `System.loadLibrary` from plugin `nativeLibraryDir`, `ToolComponent.Window`.

**Source:** [`isolation/.../nativelib/`](../../isolation/src/main/kotlin/vision/combat/c4/ds/sample/isolation/nativelib) · **Descriptor:** `vision.combat.c4.ds.sample.isolation.nativelib.NativeToolDescriptor` · **Native:** [`isolation/src/main/cpp/`](../../isolation/src/main/cpp) (CMake → `libisolation_jni.so`)

**Verify:**

1. Install both `:gallery` and `:isolation` APKs (see **Details** on the hub card for install commands).
2. Open Sample Gallery → Resources & Isolation → **Native / Cross-APK** → tap to launch.
3. Window shows asset content prefix and JNI result `isolation-jni/1.0`.

</td>
</tr>
</table>

</details>
---

## i18n check

All user-visible strings exist in `values/` and `values-uk/`. After switching system locale to Ukrainian, open each sample from the hub and confirm no English leaks (except intentional code-level debug labels).

---

## Manual upgrade test

Not tied to a single tool — validates host behavior across APK updates:

1. Install `:gallery` with `versionCode = 1`. Open **Multi Screen Window**, set a preference.
2. Bump `versionCode` in [`gallery/build.gradle.kts`](../../gallery/build.gradle.kts), rebuild, reinstall.
3. Confirm SharedPreferences state survived and hub still lists all samples.

See [Plugin isolation — case (d)](plugin-isolation.md#case-d-pinned-state-survives-versioncode-bump).

---

## Appendix — Tech stack

| Layer | Choice |
|---|---|
| Language | Kotlin `2.4.0` |
| UI | Jetpack Compose (K2 Compose compiler bundled with Kotlin `2.4.0`) |
| Build | Android Gradle Plugin `9.2.1`, Gradle `9.5.1` |
| SDK | `c4ds-sdk` / `c4ds-sdk-runtime` `0.5.1` |
| DI | Kodein (`subDI`, `diViewModel()`) |
| Persistence | Room `2.8.4`, `SharedPreferences`, plugin-scoped file storage |
| Annotation processing | KSP `2.3.9` |
| Native | NDK + CMake (`:isolation` only) |
| Desugaring | `desugar_jdk_libs` `2.1.5` |
| JVM target | 17 |
| `minSdk` / `targetSdk` / `compileSdk` | 26 / 37 / 37 |

---

**Next:** [Getting started](getting-started.md) — setup, screen layout, and the tool integration
guide · [Plugin isolation](plugin-isolation.md) — isolation cases and smoke tests ·
[← back to README](../../README.md)
