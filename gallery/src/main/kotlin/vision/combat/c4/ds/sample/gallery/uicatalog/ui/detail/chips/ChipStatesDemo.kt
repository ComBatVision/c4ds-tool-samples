package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.ui.component.button.TextButton
import vision.combat.c4.ds.sdk.ui.component.chip.AutoCompleteTextFieldBox
import vision.combat.c4.ds.sdk.ui.component.chip.ChipAutocompleteList
import vision.combat.c4.ds.sdk.ui.component.chip.ChipValue
import vision.combat.c4.ds.sdk.ui.component.chip.ChipsField
import vision.combat.c4.ds.sdk.ui.component.dialog.AppDialog

/**
 * A [ChipValue] that uses every optional member of the interface.
 *
 * Texts that need a resource are resolved in the `get…` functions: the field passes them the
 * composition's `LocalContext`, which in a plugin is the plugin's own context.
 */
internal data class StationChip(
    override val id: String,
    override val name: String?,
    override val type: ChipValue.Type = ChipValue.Type.None,
    override val enabled: Boolean = true,
    override val removable: Boolean = true,
    override val enabledInOptions: Boolean = true,
    override val icon: ImageVector? = null,
    override val leadingIcon: ImageVector? = null,
    override val dateTime: String? = null,
    @get:StringRes val descriptionResId: Int? = null,
    @get:StringRes val infoResId: Int? = null,
) : ChipValue {

    override fun getDescription(context: Context): String? = descriptionResId?.let(context::getString)

    override fun getInfo(context: Context): String? = infoResId?.let(context::getString)

    override fun getFallbackName(context: Context): String = context.getString(R.string.chips_station_unnamed)
}

/**
 * The demo stations, named after the first call signs. Ids are fixed, so the selection survives a
 * locale change.
 */
private fun stationsOf(names: List<String>, times: List<String>): List<StationChip> = listOf(
    // Not removable: the close icon gives way to `icon`, and a tap only explains why.
    StationChip(
        id = "locked",
        name = names[0],
        type = ChipValue.Type.Active,
        removable = false,
        icon = Icons.Default.Lock,
        descriptionResId = R.string.chips_station_online,
    ),
    // Disabled: drawn without a close icon and cannot be removed either.
    StationChip(
        id = "disabled",
        name = names[1],
        type = ChipValue.Type.Disabled,
        enabled = false,
        descriptionResId = R.string.chips_station_offline,
    ),
    StationChip(
        id = "active",
        name = names[2],
        type = ChipValue.Type.Active,
        leadingIcon = Icons.Default.Person,
        dateTime = times[0],
        descriptionResId = R.string.chips_station_online,
        infoResId = R.string.chips_station_role_observer,
    ),
    StationChip(
        id = "warning",
        name = names[3],
        type = ChipValue.Type.Warning,
        leadingIcon = Icons.Default.Person,
        dateTime = times[1],
        descriptionResId = R.string.chips_station_weak_signal,
        infoResId = R.string.chips_station_role_medic,
    ),
    StationChip(
        id = "error",
        name = names[4],
        type = ChipValue.Type.Error,
        dateTime = times[2],
        descriptionResId = R.string.chips_station_unreachable,
    ),
    StationChip(
        id = "offline",
        name = names[5],
        type = ChipValue.Type.Disabled,
        descriptionResId = R.string.chips_station_offline,
    ),
    // Listed, but a tap on the option row is ignored.
    StationChip(
        id = "busy",
        name = names[6],
        enabledInOptions = false,
        descriptionResId = R.string.chips_station_busy,
    ),
    // No name: the chip and the option row show getFallbackName().
    StationChip(id = "unnamed", name = null),
)

private val InitiallySelectedIds = setOf("locked", "disabled", "active")

/**
 * Shows what the optional members of [ChipValue] change: the status dot, the icons, the option row
 * texts, and which chips and options react to a tap.
 *
 * [ChipsField] reports every chip tap through `onChipClicked`, removable or not, so the caller tells
 * a removal from a plain tap — here a chip that cannot be removed opens a dialog that says why.
 */
@Composable
internal fun ChipStatesDemo() {
    val context = LocalContext.current
    val names = stringArrayResource(R.array.chips_call_signs).toList()
    val times = stringArrayResource(R.array.chips_station_times).toList()
    val stations = remember(names, times) { stationsOf(names, times) }

    var chips by remember(stations) { mutableStateOf(stations.filter { it.id in InitiallySelectedIds }) }
    var query by remember { mutableStateOf("") }
    var explainedChip by remember { mutableStateOf<StationChip?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val options = remember(stations, chips, query) { stations.filterOptions(chips, query) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AutoCompleteTextFieldBox(
            expanded = focused,
            modifier = Modifier.fillMaxWidth(),
            textField = {
                ChipsField(
                    query = query,
                    onQueryChanged = { query = it },
                    chips = chips,
                    onChipClicked = { chip ->
                        if (chip.removable && chip.enabled) {
                            chips = chips - chip
                        } else {
                            explainedChip = chip
                        }
                    },
                    label = stringResource(R.string.chips_stations_label),
                    interactionSource = interactionSource,
                )
            },
            menuContent = {
                ChipAutocompleteList(
                    query = query,
                    options = options,
                    // Not called for an option with enabledInOptions = false.
                    onOptionSelected = { option ->
                        query = ""
                        chips = chips + option
                    },
                )
            },
        )

        SelectionSummary(chips)
    }

    explainedChip?.let { chip ->
        val onDismiss = { explainedChip = null }

        AppDialog(
            onDismiss = onDismiss,
            title = chip.name ?: chip.getFallbackName(context),
            body = stringResource(
                if (chip.removable) R.string.chips_station_disabled_body else R.string.chips_station_locked_body
            ),
            confirmButton = {
                TextButton(label = stringResource(R.string.chips_dialog_ok), onClick = onDismiss)
            },
        )
    }
}
