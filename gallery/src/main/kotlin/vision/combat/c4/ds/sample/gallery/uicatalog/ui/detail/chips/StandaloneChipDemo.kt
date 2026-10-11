package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.chips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail.DemoSection
import vision.combat.c4.ds.sdk.ui.component.chip.Chip
import vision.combat.c4.ds.sdk.ui.component.chip.ChipValue

/**
 * [Chip] on its own, outside a field — the same chip [vision.combat.c4.ds.sdk.ui.component.chip.ChipsField]
 * draws, for tags and status badges in a list row or a header.
 */
@Composable
internal fun StandaloneChipDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DemoSection(stringResource(R.string.chips_section_chip_basic)) {
            ChipRow {
                Chip(text = stringResource(R.string.chips_chip_default))
                Chip(text = stringResource(R.string.chips_chip_disabled), enabled = false)
                // The outline ChipsField puts on the chip the next Backspace removes.
                Chip(text = stringResource(R.string.chips_chip_highlighted), highlighted = true)
            }
        }
        DemoSection(stringResource(R.string.chips_section_chip_types)) {
            ChipRow {
                Chip(text = stringResource(R.string.chips_chip_type_active), type = ChipValue.Type.Active)
                Chip(text = stringResource(R.string.chips_chip_type_warning), type = ChipValue.Type.Warning)
                Chip(text = stringResource(R.string.chips_chip_type_error), type = ChipValue.Type.Error)
                Chip(text = stringResource(R.string.chips_chip_type_disabled), type = ChipValue.Type.Disabled)
                Chip(text = stringResource(R.string.chips_chip_type_none), type = ChipValue.Type.None)
            }
        }
        DemoSection(stringResource(R.string.chips_section_chip_icons)) {
            ChipRow {
                Chip(
                    text = stringResource(R.string.chips_chip_icon_trailing),
                    iconPainter = rememberVectorPainter(Icons.Default.Close),
                )
                Chip(
                    text = stringResource(R.string.chips_chip_icon_leading),
                    leadingIconPainter = rememberVectorPainter(Icons.Default.Person),
                )
                Chip(
                    text = stringResource(R.string.chips_chip_icon_both),
                    type = ChipValue.Type.Active,
                    iconPainter = rememberVectorPainter(Icons.Default.Lock),
                    leadingIconPainter = rememberVectorPainter(Icons.Default.Person),
                )
            }
        }
        DemoSection(stringResource(R.string.chips_section_chip_click)) {
            var clicks by remember { mutableIntStateOf(0) }

            ChipRow {
                Chip(
                    text = stringResource(R.string.chips_chip_clicks, clicks),
                    onClick = { clicks++ },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}
