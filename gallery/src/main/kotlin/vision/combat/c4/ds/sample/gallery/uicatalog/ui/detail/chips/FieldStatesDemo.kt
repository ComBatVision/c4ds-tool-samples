package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.ui.component.button.OutlinedButton
import vision.combat.c4.ds.sdk.ui.component.button.TextButton
import vision.combat.c4.ds.sdk.ui.component.checkable.SwitchField
import vision.combat.c4.ds.sdk.ui.component.chip.AllChipsDialog
import vision.combat.c4.ds.sdk.ui.component.chip.AutoCompleteTextFieldBox
import vision.combat.c4.ds.sdk.ui.component.chip.ChipAutocompleteList
import vision.combat.c4.ds.sdk.ui.component.chip.ChipsField
import vision.combat.c4.ds.sdk.ui.component.chip.SimpleChipValue

private const val FlagsOptionCount = 6
private const val OverflowChipCount = 14

/**
 * One live field whose `readOnly`, `enabled`, `isError`, `isLoading` and `rightContent` arguments
 * are driven by the switches below it.
 *
 * The values are [SimpleChipValue]s, which have a name and nothing else. They all share an empty id,
 * so they are matched by name here and suit only a field that never holds more than 10 of them —
 * past that the "+N" dialog needs unique ids.
 */
@Composable
internal fun FieldFlagsDemo() {
    val callSigns = stringArrayResource(R.array.chips_call_signs).toList()
    val available = remember(callSigns) { callSigns.take(FlagsOptionCount).map(::SimpleChipValue) }
    val initialChips = remember(available) { available.take(3) }

    var chips by remember { mutableStateOf(initialChips) }
    var query by remember { mutableStateOf("") }

    var readOnly by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showRightContent by remember { mutableStateOf(true) }

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val options = remember(available, chips, query) {
        available.filter { it !in chips && it.name.contains(query.trim(), ignoreCase = true) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AutoCompleteTextFieldBox(
            expanded = focused && !readOnly,
            modifier = Modifier.fillMaxWidth(),
            textField = {
                ChipsField(
                    query = query,
                    onQueryChanged = { query = it },
                    chips = chips,
                    // The field still reports taps while it is read-only or disabled; it only stops
                    // drawing the close icons. Ignoring the tap is up to the caller.
                    onChipClicked = { chip ->
                        if (enabled && !readOnly) chips = chips - chip
                    },
                    label = stringResource(R.string.chips_recipients_label),
                    readOnly = readOnly,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    // The progress indicator takes the place of rightContent while loading.
                    isLoading = isLoading,
                    isError = isError,
                    rightContent = if (showRightContent) {
                        {
                            IconButton(
                                onClick = { chips = initialChips },
                                modifier = Modifier.size(16.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.chips_action_reset),
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    } else {
                        null
                    },
                )
            },
            menuContent = {
                ChipAutocompleteList(
                    query = query,
                    options = options,
                    onOptionSelected = { option ->
                        query = ""
                        chips = chips + option
                    },
                    // Shown when there is nothing to offer; a default "Nothing found" otherwise.
                    emptyAutocompleteMessage = if (query.isBlank()) {
                        stringResource(R.string.chips_empty_all_selected)
                    } else {
                        stringResource(R.string.chips_empty_no_match, query.trim())
                    },
                )
            },
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SwitchField(
                initialValue = readOnly,
                label = stringResource(R.string.chips_flag_read_only),
                onCheckedChange = { readOnly = it },
            )
            SwitchField(
                initialValue = enabled,
                label = stringResource(R.string.chips_flag_enabled),
                onCheckedChange = { enabled = it },
            )
            SwitchField(
                initialValue = isError,
                label = stringResource(R.string.chips_flag_error),
                onCheckedChange = { isError = it },
            )
            SwitchField(
                initialValue = isLoading,
                label = stringResource(R.string.chips_flag_loading),
                onCheckedChange = { isLoading = it },
            )
            SwitchField(
                initialValue = showRightContent,
                label = stringResource(R.string.chips_flag_right_content),
                onCheckedChange = { showRightContent = it },
            )
        }
    }
}

/**
 * A field with more chips than it shows inline. Only the first 10 are drawn; the rest hide behind a
 * "+N" chip that opens [AllChipsDialog], titled with the field's label. Removals made in the dialog
 * arrive through the same `onChipClicked`.
 *
 * The button opens the same dialog directly, the way a tool would for its own "show all" action.
 */
@Composable
internal fun OverflowDemo() {
    val initialChips = rememberCallSignChips(OverflowChipCount)

    var chips by remember { mutableStateOf(initialChips) }
    var query by remember { mutableStateOf("") }
    var showAllChips by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ChipsField(
            query = query,
            onQueryChanged = { query = it },
            chips = chips,
            onChipClicked = { chip -> chips = chips - chip },
            label = stringResource(R.string.chips_recipients_label),
        )

        SelectionSummary(chips)

        OutlinedButton(
            label = stringResource(R.string.chips_action_show_all),
            onClick = { showAllChips = true },
            enabled = chips.isNotEmpty(),
        )
        TextButton(
            label = stringResource(R.string.chips_action_reset),
            onClick = { chips = initialChips },
        )
    }

    if (showAllChips) {
        AllChipsDialog(
            // null falls back to the default "All items" title.
            title = null,
            chips = chips,
            editable = true,
            onRemoveChip = { chip -> chips = chips - chip },
            onDismiss = { showAllChips = false },
        )
    }
}
