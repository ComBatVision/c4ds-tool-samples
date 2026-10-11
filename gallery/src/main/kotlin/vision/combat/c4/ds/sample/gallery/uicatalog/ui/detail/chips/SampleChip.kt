package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.ui.component.chip.ChipValue

/**
 * The smallest useful [ChipValue]: a stable [id] and a [name].
 *
 * Give every value a unique id — the field's "+N" dialog keys its rows by it.
 */
internal data class SampleChip(
    override val id: String,
    override val name: String,
) : ChipValue

/** The first [count] call signs as chips; the call sign doubles as the id. */
@Composable
internal fun rememberCallSignChips(count: Int): List<SampleChip> {
    val callSigns = stringArrayResource(R.array.chips_call_signs).toList()

    return remember(callSigns, count) {
        callSigns.take(count).map { SampleChip(id = it, name = it) }
    }
}

/**
 * Options to offer for [query]: not selected yet and with a name containing the query. The SDK list
 * shows what it is given, so filtering and ordering belong to the caller.
 */
internal fun <Value : ChipValue> List<Value>.filterOptions(selected: List<Value>, query: String): List<Value> =
    filter { option ->
        selected.none { it.id == option.id } && option.name.orEmpty().contains(query.trim(), ignoreCase = true)
    }

/** Line under a field that proves the selection lives in the caller, not in the field. */
@Composable
internal fun SelectionSummary(chips: List<ChipValue>) {
    val context = LocalContext.current
    val names = chips.joinToString { it.name ?: it.getFallbackName(context) }

    Text(
        text = if (chips.isEmpty()) {
            stringResource(R.string.chips_selection_empty)
        } else {
            stringResource(R.string.chips_selection_summary, chips.size, names)
        },
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onSurface,
    )
}
