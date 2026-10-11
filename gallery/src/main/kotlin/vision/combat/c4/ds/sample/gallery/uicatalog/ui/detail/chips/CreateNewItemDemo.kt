package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.ui.component.chip.AutoCompleteTextFieldBox
import vision.combat.c4.ds.sdk.ui.component.chip.ChipAutocompleteList
import vision.combat.c4.ds.sdk.ui.component.chip.ChipsField

/**
 * Lets the user turn the typed text into a new item.
 *
 * [ChipAutocompleteList] only draws the "create new item" row and reports the tap; whether the row
 * is offered ([ChipAutocompleteList]'s `allowNewOptionCreation`) and what creating means are the
 * caller's decisions. Here a tag can be created when the text is not blank and no known tag has that
 * name; a created tag joins the known tags, so it is offered again after it is removed.
 */
@Composable
internal fun CreateNewItemDemo() {
    val presetNames = stringArrayResource(R.array.chips_tags).toList()

    var knownTags by remember(presetNames) {
        mutableStateOf(presetNames.mapIndexed { index, name -> SampleChip(id = "preset:$index", name = name) })
    }
    var chips by remember { mutableStateOf(emptyList<SampleChip>()) }
    var query by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val newTagName = query.trim()
    val canCreate = newTagName.isNotEmpty() && knownTags.none { it.name.equals(newTagName, ignoreCase = true) }
    val options = remember(knownTags, chips, query) { knownTags.filterOptions(chips, query) }

    val createTag = {
        if (canCreate) {
            val tag = SampleChip(id = "custom:$newTagName", name = newTagName)

            knownTags = knownTags + tag
            chips = chips + tag
            query = ""
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AutoCompleteTextFieldBox(
            expanded = focused,
            modifier = Modifier.fillMaxWidth(),
            textField = {
                ChipsField(
                    query = query,
                    onQueryChanged = { query = it },
                    chips = chips,
                    onChipClicked = { chip -> chips = chips - chip },
                    label = stringResource(R.string.chips_tags_label),
                    placeholder = stringResource(R.string.chips_tags_placeholder),
                    interactionSource = interactionSource,
                    // The keyboard action creates the tag too, so the list is not the only way.
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { createTag() }),
                )
            },
            menuContent = {
                ChipAutocompleteList(
                    // The query is the title of the "create new item" row.
                    query = newTagName,
                    options = options,
                    onOptionSelected = { option ->
                        query = ""
                        chips = chips + option
                    },
                    onNewOptionSelected = createTag,
                    allowNewOptionCreation = canCreate,
                )
            },
        )

        SelectionSummary(chips)
    }
}
