package vision.combat.c4.ds.sample.gallery.catalog.ui

import androidx.annotation.Keep
import androidx.annotation.StringRes
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sample.gallery.dialog.DialogToolDescriptor
import vision.combat.c4.ds.sample.gallery.hostlibs.HostLibsToolDescriptor
import vision.combat.c4.ds.sample.gallery.hostservices.HostServicesToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapoverlays.endbar.EndBarToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapoverlays.expandablestatus.ExpandableStatusToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapview.map.MapToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapview.mapinteractor.MapInteractorToolDescriptor
import vision.combat.c4.ds.sample.gallery.model.ModelToolDescriptor
import vision.combat.c4.ds.sample.gallery.network.NetworkToolDescriptor
import vision.combat.c4.ds.sample.gallery.openwith.OpenWithToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapoverlays.overlay.OverlayToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapoverlays.overlayisdefault.OverlayDefaultToolDescriptor
import vision.combat.c4.ds.sample.gallery.panelstate.PanelStateToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapview.renderable.RenderableToolDescriptor
import vision.combat.c4.ds.sample.gallery.resources.collision.CollisionToolDescriptor
import vision.combat.c4.ds.sample.gallery.resources.config.ConfigToolDescriptor
import vision.combat.c4.ds.sample.gallery.resources.material.MaterialToolDescriptor
import vision.combat.c4.ds.sample.gallery.service.ServiceToolDescriptor
import vision.combat.c4.ds.sample.gallery.mapoverlays.status.StatusToolDescriptor
import vision.combat.c4.ds.sample.gallery.storage.StorageToolDescriptor
import vision.combat.c4.ds.sample.gallery.toolmanagement.ToolManagementToolDescriptor
import vision.combat.c4.ds.sample.gallery.uicatalog.UiCatalogToolDescriptor
import vision.combat.c4.ds.sample.gallery.underlay.UnderlayToolDescriptor
import vision.combat.c4.ds.sample.gallery.window.map.MapWindowToolDescriptor
import vision.combat.c4.ds.sample.gallery.window.multiscreen.WindowMultiScreenToolDescriptor
import vision.combat.c4.ds.sample.gallery.window.singlescreen.WindowSingleScreenToolDescriptor
import vision.combat.c4.ds.sdk.tool.requireQualifiedName

/**
 * Registry of all gallery samples.
 *
 * @param section Which section this entry belongs to.
 * @param nameResId String resource for the sample name.
 * @param descResId String resource for the sample description.
 * @param apisResId String resource listing which SDK APIs are demonstrated.
 * @param sourceSubpackage Package path shown on the detail screen (e.g. "window/singlescreen").
 * @param toolClassName Fully-qualified class name of the tool's descriptor. Used both to match this
 *   entry against the active tools (ToolManager.activeTools) and as the argument to
 *   ToolManager.activate / deactivate, which resolve it to a ToolId via resolveToolId.
 * @param isCrossApk True if this entry's tool lives in a separate APK that must be installed.
 * @param crossApkNotInstalledResId Short "not installed" label shown in the sample list row when
 *   [isCrossApk] is true and the APK is absent. Required (non-null) for cross-APK entries.
 * @param crossApkInstallIntroResId Intro sentence on the detail screen's install section. Required
 *   (non-null) for cross-APK entries.
 * @param crossApkInstallCommandsResId Shell command(s) to build/install this entry's APK, shown on
 *   the detail screen. Required (non-null) for cross-APK entries.
 * @param crossApkInstallStatusInstalledResId Status line shown on the detail screen once the APK is
 *   detected as installed. Required (non-null) for cross-APK entries.
 * @param crossApkInstallStatusMissingResId Status line shown on the detail screen while the APK is
 *   not yet installed. Required (non-null) for cross-APK entries.
 */
@Keep
internal enum class CatalogEntry(
    val section: CatalogSection,
    @get:StringRes val nameResId: Int,
    @get:StringRes val descResId: Int,
    @get:StringRes val apisResId: Int,
    val sourceSubpackage: String,
    val toolClassName: String,
    val isCrossApk: Boolean = false,
    @get:StringRes val crossApkNotInstalledResId: Int? = null,
    @get:StringRes val crossApkInstallIntroResId: Int? = null,
    @get:StringRes val crossApkInstallCommandsResId: Int? = null,
    @get:StringRes val crossApkInstallStatusInstalledResId: Int? = null,
    @get:StringRes val crossApkInstallStatusMissingResId: Int? = null,
) {
    // ── MAP VIEW ─────────────────────────────────────────────────────────
    MAP(
        section = CatalogSection.MAP_VIEW,
        nameResId = R.string.map_tool_name,
        descResId = R.string.map_desc,
        apisResId = R.string.map_apis,
        sourceSubpackage = "mapview/map",
        toolClassName = requireQualifiedName<MapToolDescriptor>(),
    ),
    RENDERABLE(
        section = CatalogSection.MAP_VIEW,
        nameResId = R.string.renderable_tool_name,
        descResId = R.string.renderable_desc,
        apisResId = R.string.renderable_apis,
        sourceSubpackage = "mapview/renderable",
        toolClassName = requireQualifiedName<RenderableToolDescriptor>(),
    ),
    MAP_INTERACTOR(
        section = CatalogSection.MAP_VIEW,
        nameResId = R.string.map_interactor_tool_name,
        descResId = R.string.map_interactor_desc,
        apisResId = R.string.map_interactor_apis,
        sourceSubpackage = "mapview/mapinteractor",
        toolClassName = requireQualifiedName<MapInteractorToolDescriptor>(),
    ),

    // ── MAP OVERLAYS ─────────────────────────────────────────────────────
    OVERLAY(
        section = CatalogSection.MAP_OVERLAYS,
        nameResId = R.string.overlay_tool_name,
        descResId = R.string.overlay_desc,
        apisResId = R.string.overlay_apis,
        sourceSubpackage = "mapoverlays/overlay",
        toolClassName = requireQualifiedName<OverlayToolDescriptor>(),
    ),
    OVERLAY_IS_DEFAULT(
        section = CatalogSection.MAP_OVERLAYS,
        nameResId = R.string.overlay_default_tool_name,
        descResId = R.string.overlay_default_desc,
        apisResId = R.string.overlay_default_apis,
        sourceSubpackage = "mapoverlays/overlayisdefault",
        toolClassName = requireQualifiedName<OverlayDefaultToolDescriptor>(),
    ),
    STATUS(
        section = CatalogSection.MAP_OVERLAYS,
        nameResId = R.string.status_tool_name,
        descResId = R.string.status_desc,
        apisResId = R.string.status_apis,
        sourceSubpackage = "mapoverlays/status",
        toolClassName = requireQualifiedName<StatusToolDescriptor>(),
    ),
    EXPANDABLE_STATUS(
        section = CatalogSection.MAP_OVERLAYS,
        nameResId = R.string.expandable_status_tool_name,
        descResId = R.string.expandable_status_desc,
        apisResId = R.string.expandable_status_apis,
        sourceSubpackage = "mapoverlays/expandablestatus",
        toolClassName = requireQualifiedName<ExpandableStatusToolDescriptor>(),
    ),
    ENDBAR(
        section = CatalogSection.MAP_OVERLAYS,
        nameResId = R.string.endbar_tool_name,
        descResId = R.string.endbar_desc,
        apisResId = R.string.endbar_apis,
        sourceSubpackage = "mapoverlays/endbar",
        toolClassName = requireQualifiedName<EndBarToolDescriptor>(),
    ),

    // ── MAP UNDERLAY ──────────────────────────────────────────────────────
    UNDERLAY(
        section = CatalogSection.MAP_UNDERLAY,
        nameResId = R.string.underlay_tool_name,
        descResId = R.string.underlay_desc,
        apisResId = R.string.underlay_apis,
        sourceSubpackage = "underlay",
        toolClassName = requireQualifiedName<UnderlayToolDescriptor>(),
    ),

    // ── PANEL WINDOWS ────────────────────────────────────────────────────
    WINDOW_SINGLE_SCREEN(
        section = CatalogSection.PANEL_WINDOWS,
        nameResId = R.string.window_single_screen_tool_name,
        descResId = R.string.window_single_screen_desc,
        apisResId = R.string.window_single_screen_apis,
        sourceSubpackage = "window/singlescreen",
        toolClassName = requireQualifiedName<WindowSingleScreenToolDescriptor>(),
    ),
    WINDOW_MULTI_SCREEN(
        section = CatalogSection.PANEL_WINDOWS,
        nameResId = R.string.window_multi_screen_tool_name,
        descResId = R.string.window_multi_screen_desc,
        apisResId = R.string.window_multi_screen_apis,
        sourceSubpackage = "window/multiscreen",
        toolClassName = requireQualifiedName<WindowMultiScreenToolDescriptor>(),
    ),
    MAP_WINDOW(
        section = CatalogSection.PANEL_WINDOWS,
        nameResId = R.string.mapwindow_tool_name,
        descResId = R.string.mapwindow_desc,
        apisResId = R.string.mapwindow_apis,
        sourceSubpackage = "window/map",
        toolClassName = requireQualifiedName<MapWindowToolDescriptor>(),
    ),

    // ── PANEL STATE ──────────────────────────────────────────────────────
    PANEL_STATE(
        section = CatalogSection.PANEL_STATE,
        nameResId = R.string.panel_state_tool_name,
        descResId = R.string.panel_state_desc,
        apisResId = R.string.panel_state_apis,
        sourceSubpackage = "panelstate",
        toolClassName = requireQualifiedName<PanelStateToolDescriptor>(),
    ),

    // ── TOOL MANAGEMENT ──────────────────────────────────────────────────
    TOOL_MANAGEMENT(
        section = CatalogSection.TOOL_MANAGEMENT,
        nameResId = R.string.tool_management_tool_name,
        descResId = R.string.tool_management_desc,
        apisResId = R.string.tool_management_apis,
        sourceSubpackage = "toolmanagement",
        toolClassName = requireQualifiedName<ToolManagementToolDescriptor>(),
    ),

    // ── UI COMPONENTS ────────────────────────────────────────────────────
    UI_CATALOG(
        section = CatalogSection.UI_COMPONENTS,
        nameResId = R.string.ui_catalog_tool_name,
        descResId = R.string.ui_catalog_desc,
        apisResId = R.string.ui_catalog_apis,
        sourceSubpackage = "uicatalog",
        toolClassName = requireQualifiedName<UiCatalogToolDescriptor>(),
    ),

    // ── DIALOGS ──────────────────────────────────────────────────────────
    DIALOG(
        section = CatalogSection.DIALOGS,
        nameResId = R.string.dialog_tool_name,
        descResId = R.string.dialog_desc,
        apisResId = R.string.dialog_apis,
        sourceSubpackage = "dialog",
        toolClassName = requireQualifiedName<DialogToolDescriptor>(),
    ),

    // ── MODEL MANAGEMENT ─────────────────────────────────────────────────
    MODEL(
        section = CatalogSection.MODEL_MANAGEMENT,
        nameResId = R.string.model_tool_name,
        descResId = R.string.model_desc,
        apisResId = R.string.model_apis,
        sourceSubpackage = "model",
        toolClassName = requireQualifiedName<ModelToolDescriptor>(),
    ),

    // ── DATA MANAGEMENT ──────────────────────────────────────────────────
    STORAGE(
        section = CatalogSection.DATA_MANAGEMENT,
        nameResId = R.string.storage_tool_name,
        descResId = R.string.storage_desc,
        apisResId = R.string.storage_apis,
        sourceSubpackage = "storage",
        toolClassName = requireQualifiedName<StorageToolDescriptor>(),
    ),
    NETWORK(
        section = CatalogSection.DATA_MANAGEMENT,
        nameResId = R.string.network_tool_name,
        descResId = R.string.network_desc,
        apisResId = R.string.network_apis,
        sourceSubpackage = "network",
        toolClassName = requireQualifiedName<NetworkToolDescriptor>(),
    ),

    // ── LIFECYCLE & SERVICES ──────────────────────────────────────────────
    SERVICE(
        section = CatalogSection.LIFECYCLE_SERVICES,
        nameResId = R.string.service_tool_name,
        descResId = R.string.service_desc,
        apisResId = R.string.service_apis,
        sourceSubpackage = "service",
        toolClassName = requireQualifiedName<ServiceToolDescriptor>(),
    ),

    // ── HOST SERVICES ────────────────────────────────────────────────────
    HOST_SERVICES(
        section = CatalogSection.HOST_SERVICES,
        nameResId = R.string.host_services_tool_name,
        descResId = R.string.host_services_desc,
        apisResId = R.string.host_services_apis,
        sourceSubpackage = "hostservices",
        toolClassName = requireQualifiedName<HostServicesToolDescriptor>(),
    ),
    HOST_LIBS(
        section = CatalogSection.HOST_SERVICES,
        nameResId = R.string.host_libs_tool_name,
        descResId = R.string.host_libs_desc,
        apisResId = R.string.host_libs_apis,
        sourceSubpackage = "hostlibs",
        toolClassName = requireQualifiedName<HostLibsToolDescriptor>(),
    ),
    OPEN_WITH(
        section = CatalogSection.HOST_SERVICES,
        nameResId = R.string.open_with_tool_name,
        descResId = R.string.open_with_desc,
        apisResId = R.string.open_with_apis,
        sourceSubpackage = "openwith",
        toolClassName = requireQualifiedName<OpenWithToolDescriptor>(),
    ),

    // ── RESOURCES & ISOLATION ─────────────────────────────────────────────
    RESOURCES_CONFIG(
        section = CatalogSection.RESOURCES_ISOLATION,
        nameResId = R.string.config_tool_name,
        descResId = R.string.config_desc,
        apisResId = R.string.config_apis,
        sourceSubpackage = "resources/config",
        toolClassName = requireQualifiedName<ConfigToolDescriptor>(),
    ),
    RESOURCES_MATERIAL(
        section = CatalogSection.RESOURCES_ISOLATION,
        nameResId = R.string.material_tool_name,
        descResId = R.string.material_desc,
        apisResId = R.string.material_apis,
        sourceSubpackage = "resources/material",
        toolClassName = requireQualifiedName<MaterialToolDescriptor>(),
    ),
    RESOURCES_COLLISION(
        section = CatalogSection.RESOURCES_ISOLATION,
        nameResId = R.string.collision_tool_name,
        descResId = R.string.collision_desc,
        apisResId = R.string.collision_apis,
        sourceSubpackage = "resources/collision",
        toolClassName = requireQualifiedName<CollisionToolDescriptor>(),
    ),
    NATIVE_CROSS_APK(
        section = CatalogSection.RESOURCES_ISOLATION,
        nameResId = R.string.native_cross_apk_title,
        descResId = R.string.native_cross_apk_desc,
        apisResId = R.string.native_cross_apk_apis,
        sourceSubpackage = "isolation/nativelib",
        toolClassName = "vision.combat.c4.ds.sample.isolation.nativelib.NativeToolDescriptor",
        isCrossApk = true,
        crossApkNotInstalledResId = R.string.native_cross_apk_not_installed,
        crossApkInstallIntroResId = R.string.catalog_cross_apk_install_intro,
        crossApkInstallCommandsResId = R.string.catalog_cross_apk_install_commands,
        crossApkInstallStatusInstalledResId = R.string.catalog_cross_apk_install_status_installed,
        crossApkInstallStatusMissingResId = R.string.catalog_cross_apk_install_status_missing,
    ),

}
