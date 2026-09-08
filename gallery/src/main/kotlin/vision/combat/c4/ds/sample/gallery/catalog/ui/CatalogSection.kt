package vision.combat.c4.ds.sample.gallery.catalog.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import vision.combat.c4.ds.sample.gallery.R

/**
 * Sections grouping samples in the catalog category list.
 *
 * Ordering here controls display order in [CatalogListScreen].
 *
 * @param titleResId Display title string resource.
 * @param descResId Short description of what samples are in this section.
 * @param iconResId Drawable resource for the section card icon.
 */
enum class CatalogSection(
    @get:StringRes val titleResId: Int,
    @get:StringRes val descResId: Int,
    @get:DrawableRes val iconResId: Int,
) {
    MAP_VIEW(
        titleResId = R.string.section_map_view_title,
        descResId = R.string.section_map_view_desc,
        iconResId = R.drawable.ic_map,
    ),
    MAP_OVERLAYS(
        titleResId = R.string.section_map_overlays_title,
        descResId = R.string.section_map_overlays_desc,
        iconResId = R.drawable.ic_overlay,
    ),
    MAP_UNDERLAY(
        titleResId = R.string.section_map_underlay_title,
        descResId = R.string.section_map_underlay_desc,
        iconResId = R.drawable.ic_underlay,
    ),
    PANEL_WINDOWS(
        titleResId = R.string.section_panel_windows_title,
        descResId = R.string.section_panel_windows_desc,
        iconResId = R.drawable.ic_window,
    ),
    PANEL_STATE(
        titleResId = R.string.section_panel_state_title,
        descResId = R.string.section_panel_state_desc,
        iconResId = R.drawable.ic_panel_state,
    ),
    UI_COMPONENTS(
        titleResId = R.string.section_ui_components_title,
        descResId = R.string.section_ui_components_desc,
        iconResId = R.drawable.ic_components,
    ),
    DIALOGS(
        titleResId = R.string.section_dialogs_title,
        descResId = R.string.section_dialogs_desc,
        iconResId = R.drawable.ic_dialog,
    ),
    TOOL_MANAGEMENT(
        titleResId = R.string.section_tool_management_title,
        descResId = R.string.section_tool_management_desc,
        iconResId = R.drawable.ic_tool_management,
    ),
    MODEL_MANAGEMENT(
        titleResId = R.string.section_model_management_title,
        descResId = R.string.section_model_management_desc,
        iconResId = R.drawable.ic_model,
    ),
    DATA_MANAGEMENT(
        titleResId = R.string.section_data_management_title,
        descResId = R.string.section_data_management_desc,
        iconResId = R.drawable.ic_storage,
    ),
    LIFECYCLE_SERVICES(
        titleResId = R.string.section_lifecycle_services_title,
        descResId = R.string.section_lifecycle_services_desc,
        iconResId = R.drawable.ic_service,
    ),
    HOST_SERVICES(
        titleResId = R.string.section_host_services_title,
        descResId = R.string.section_host_services_desc,
        iconResId = R.drawable.ic_share,
    ),
    RESOURCES_ISOLATION(
        titleResId = R.string.section_resources_isolation_title,
        descResId = R.string.section_resources_isolation_desc,
        iconResId = R.drawable.ic_isolation,
    ),
}
