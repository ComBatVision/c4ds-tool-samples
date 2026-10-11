package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.ui.component.chip.AutoCompleteTextFieldBox
import vision.combat.c4.ds.sdk.ui.component.chip.ChipAutocompleteList
import vision.combat.c4.ds.sdk.ui.component.chip.ChipsField

private const val AvailableCount = 8

/**
 * The canonical composition: [ChipsField] as the anchor of an [AutoCompleteTextFieldBox], with a
 * [ChipAutocompleteList] as its menu.
 *
 * The query and the selection are plain local state. The menu is open while the field is focused,
 * which the shared [MutableInteractionSource] reports.
 */
@Composable
internal fun BasicSelectionDemo() {
    val available = rememberCallSignChips(AvailableCount)

    var chips by remember { mutableStateOf(available.take(2)) }
    var query by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val options = remember(available, chips, query) { available.filterOptions(chips, query) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AutoCompleteTextFieldBox(
            expanded = focused,
            modifier = Modifier.fillMaxWidth(),
            textField = {
                ChipsField(
                    query = query,
                    onQueryChanged = { query = it },
                    chips = chips,
                    // Reported for a tap and for the second Backspace on an empty query. The field
                    // removes nothing itself; every chip here is removable, so a click is a removal.
                    onChipClicked = { chip -> chips = chips - chip },
                    label = stringResource(R.string.chips_recipients_label),
                    placeholder = stringResource(R.string.chips_recipients_placeholder),
                    interactionSource = interactionSource,
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
                )
            },
        )

        SelectionSummary(chips)
    }
}
