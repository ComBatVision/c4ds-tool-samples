package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips.BasicSelectionDemo
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips.ChipStatesDemo
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips.CreateNewItemDemo
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips.FieldFlagsDemo
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips.OverflowDemo
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips.StandaloneChipDemo

/**
 * The stateless chips field of the SDK (from SDK 0.6.3), one showcase per section.
 *
 * The field keeps no state of its own: every showcase owns the typed query and the selected chips,
 * filters the options itself, and decides what a tap on a chip means.
 */
@Composable
internal fun ChipsFieldDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        ChipsShowcase(R.string.chips_basic_name, R.string.chips_basic_desc) { BasicSelectionDemo() }
        ChipsShowcase(R.string.chips_create_name, R.string.chips_create_desc) { CreateNewItemDemo() }
        ChipsShowcase(R.string.chips_states_name, R.string.chips_states_desc) { ChipStatesDemo() }
        ChipsShowcase(R.string.chips_field_states_name, R.string.chips_field_states_desc) { FieldFlagsDemo() }
        ChipsShowcase(R.string.chips_overflow_name, R.string.chips_overflow_desc) { OverflowDemo() }
        ChipsShowcase(R.string.chips_standalone_name, R.string.chips_standalone_desc) { StandaloneChipDemo() }
    }
}

@Composable
private fun ChipsShowcase(nameResId: Int, descResId: Int, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(nameResId),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface,
        )
        Text(
            text = stringResource(descResId),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface,
        )
        content()
    }
}
