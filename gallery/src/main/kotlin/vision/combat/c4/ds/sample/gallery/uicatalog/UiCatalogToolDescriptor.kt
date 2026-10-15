package vision.combat.c4.ds.sample.gallery.uicatalog

import org.kodein.di.DI
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams

/**
 * Catalog-launched sample showcasing promoted public SDK UI components: six form-field components
 * and six grouped showcases (Buttons, Inputs, Chips field, Selection, Feedback, Lists).
 *
 * Navigation: a list of components -> a per-component detail screen that renders the component in
 * several states (see [vision.combat.c4.ds.sample.gallery.uicatalog.ui.UiCatalogWindow]).
 *
 * SDK APIs demonstrated:
 *   ui.component.field.InlineMessage, HeaderField, ExpandableField, FormFieldBox, NestedForm,
 *   ui.component.hostility.HostilitySelector,
 *   ui.component.button.*, bar.TopAppBar, bar.AppBarActionButton, bar.AppFab,
 *   ui.component.text.OutlinedTextInputField, IntegerStepper, dropdown.*, measurement.*,
 *   coordinates.CoordinatesInputWithSystem,
 *   ui.component.chip.ChipsField, ChipAutocompleteList, AutoCompleteTextFieldBox, ChipValue, Chip,
 *   AllChipsDialog (from SDK 0.6.3),
 *   SegmentedButtonRow, SliderWithLabel, checkable.*, RadioGroup, ColorSelector,
 *   AppDialog, Banner, Carousel, Tooltip,
 *   reveal.RevealableLazyColumn, list.ListItem, reveal.EditMenuButton, reveal.DeleteMenuButton,
 *   ui.navigation.AppNavHost, WindowScaffold, bar.BackNavTopAppBar, bar.TopAppBar
 */
class UiCatalogToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    override val nameResId: Int = R.string.ui_catalog_tool_name
    override val iconResId: Int = R.drawable.ic_ui_catalog
    override val categories: List<String> = emptyList()

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool {
        return UiCatalogTool(toolContext, this, di, params)
    }
}
