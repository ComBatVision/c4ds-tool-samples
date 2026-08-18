package vision.combat.c4.ds.sample.gallery.window.map

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import earth.worldwind.geom.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.kodein.di.DI
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.map.MapController
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolComponent
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.sdk.tool.mapWindow
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.component.bar.endbar.EndBarActionButton
import vision.combat.c4.ds.sdk.ui.component.menu.OverflowMenu
import vision.combat.c4.ds.sdk.ui.component.menu.OverflowMenuItem
import vision.combat.c4.ds.sdk.ui.component.menu.OverflowMenuScope

/**
 * Minimal [AbstractTool] subclass that wires a [ToolComponent.MapWindow] with a
 * focus-camera end-bar button and a custom interaction-mode app bar.
 */
internal class MapWindowTool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractTool(toolContext, toolDescriptor, parentDI, params) {

    override val window: ToolComponent.MapWindow by mapWindow(
        isRequired = { true },
        showMapOnActivation = true,
        mapEndBarButtons = { FocusCameraButton() },
        navBarContent = { MapWindowAppBar() },
        initialize = {
            navigationController.interactionMode = MapController.InteractionMode.LOOK_AT
        },
    )

    @Composable
    private fun FocusCameraButton() {
        EndBarActionButton(
            icon = rememberVectorPainter(Icons.Default.MyLocation),
            contentDescription = stringResource(R.string.mapwindow_focus_camera),
            onClick = {
                // Kyiv — same stable demo location as the MapInteractor sample
                window.focusCameraOn(Location.fromDegrees(50.45, 30.52), lockCamera = false)
            },
        )
    }

    /**
     * Standard tool app bar (back navigation + title) with the interaction-mode
     * selection moved into an overflow "…" menu, mirroring how other tools present
     * their window chrome via [ToolComponent.MapWindow.navBarContent].
     */
    @Composable
    private fun MapWindowAppBar() {
        var selectedMode by remember { mutableStateOf(MapController.InteractionMode.LOOK_AT) }
        BackNavTopAppBar(
            title = stringResource(R.string.mapwindow_tool_name),
            actions = {
                OverflowMenu {
                    InteractionModeItem(
                        label = stringResource(R.string.mapwindow_mode_lookat),
                        selected = selectedMode == MapController.InteractionMode.LOOK_AT,
                        onClick = {
                            selectedMode = MapController.InteractionMode.LOOK_AT
                            window.mapView?.navigationController?.interactionMode = MapController.InteractionMode.LOOK_AT
                        },
                    )
                    InteractionModeItem(
                        label = stringResource(R.string.mapwindow_mode_fpv),
                        selected = selectedMode == MapController.InteractionMode.FPV,
                        onClick = {
                            selectedMode = MapController.InteractionMode.FPV
                            window.mapView?.navigationController?.interactionMode = MapController.InteractionMode.FPV
                        },
                    )
                }
            },
        )
    }

    @Composable
    private fun OverflowMenuScope.InteractionModeItem(
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
    ) {
        OverflowMenuItem(
            icon = {
                if (selected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                } else {
                    Spacer(Modifier.size(24.dp))
                }
            },
            title = label,
            onClick = onClick,
        )
    }
}
