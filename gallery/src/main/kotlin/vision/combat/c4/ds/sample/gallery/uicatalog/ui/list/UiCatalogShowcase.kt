package vision.combat.c4.ds.sample.gallery.uicatalog.ui.list

import androidx.annotation.Keep
import androidx.annotation.StringRes
import vision.combat.c4.ds.sample.gallery.R

/**
 * Static registry of the promoted public-SDK components showcased by the UI Catalog sample.
 *
 * @param nameResId String resource for the component name (shown as list title + detail title).
 * @param descResId String resource for the component description.
 */
@Keep
internal enum class UiCatalogShowcase(
    @get:StringRes val nameResId: Int,
    @get:StringRes val descResId: Int,
) {
    INLINE_MESSAGE(
        nameResId = R.string.ui_catalog_inline_message_name,
        descResId = R.string.ui_catalog_inline_message_desc,
    ),
    HEADER_FIELD(
        nameResId = R.string.ui_catalog_header_field_name,
        descResId = R.string.ui_catalog_header_field_desc,
    ),
    EXPANDABLE_FIELD(
        nameResId = R.string.ui_catalog_expandable_field_name,
        descResId = R.string.ui_catalog_expandable_field_desc,
    ),
    FORM_FIELD_BOX(
        nameResId = R.string.ui_catalog_form_field_box_name,
        descResId = R.string.ui_catalog_form_field_box_desc,
    ),
    NESTED_FORM(
        nameResId = R.string.ui_catalog_nested_form_name,
        descResId = R.string.ui_catalog_nested_form_desc,
    ),
    HOSTILITY_SELECTOR(
        nameResId = R.string.ui_catalog_hostility_selector_name,
        descResId = R.string.ui_catalog_hostility_selector_desc,
    ),
    BUTTONS(
        nameResId = R.string.ui_catalog_buttons_name,
        descResId = R.string.ui_catalog_buttons_desc,
    ),
    TOP_APP_BAR(
        nameResId = R.string.ui_catalog_top_app_bar_name,
        descResId = R.string.ui_catalog_top_app_bar_desc,
    ),
    INPUTS(
        nameResId = R.string.ui_catalog_inputs_name,
        descResId = R.string.ui_catalog_inputs_desc,
    ),
    CHIPS_FIELD(
        nameResId = R.string.ui_catalog_chips_field_name,
        descResId = R.string.ui_catalog_chips_field_desc,
    ),
    SELECTION(
        nameResId = R.string.ui_catalog_selection_name,
        descResId = R.string.ui_catalog_selection_desc,
    ),
    FEEDBACK(
        nameResId = R.string.ui_catalog_feedback_name,
        descResId = R.string.ui_catalog_feedback_desc,
    ),
    LISTS(
        nameResId = R.string.ui_catalog_lists_name,
        descResId = R.string.ui_catalog_lists_desc,
    )
}
