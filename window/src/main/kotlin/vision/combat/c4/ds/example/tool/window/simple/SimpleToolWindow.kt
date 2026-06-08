@file:Suppress("UnusedReceiverParameter")

package vision.combat.c4.ds.example.tool.window.simple

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.component.button.TextButton
import vision.combat.c4.ds.sdk.ui.component.coordinates.CoordinatesInputWithSystem
import vision.combat.c4.ds.sdk.ui.component.measurement.AltitudeInput
import vision.combat.c4.ds.sdk.ui.component.measurement.AngleInput
import vision.combat.c4.ds.sdk.ui.component.measurement.DistanceInput
import vision.combat.c4.ds.sdk.ui.component.measurement.SpeedInput
import vision.combat.c4.ds.sdk.ui.util.showToast
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel
import vision.combat.c4.ds.tool.sample.window.R
import vision.combat.c4.ds.example.tool.window.simple.SimpleToolViewModel.Action
import vision.combat.c4.ds.example.tool.window.simple.SimpleToolViewModel.Event
import vision.combat.c4.ds.example.tool.window.simple.SimpleToolViewModel.UiState

@Composable
internal fun SimpleToolWindow(viewModel: SimpleToolViewModel = diViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WindowContent(
        uiState = uiState,
        onAction = { viewModel.handleAction(it) },
    )

    EventHandler(
        eventFlow = viewModel.event,
    )
}

@Composable
private fun WindowContent(uiState: UiState, onAction: (Action) -> Unit) {
    WindowScaffold(
        topAppBar = { TopAppBar() },
        content = { Content(uiState, onAction) },
    )
}

@Composable
private fun TopAppBar() {
    BackNavTopAppBar(
        title = stringResource(R.string.simple_tool_name),
    )
}

@Composable
private fun ColumnScope.Content(uiState: UiState, onAction: (Action) -> Unit) {
    ModelInfo(
        info = uiState.userModel,
        sectionLabel = stringResource(R.string.user_model),
    )
    Divider(
        modifier = Modifier.padding(vertical = 16.dp)
    )
    ModelInfo(
        info = uiState.selectedModel,
        sectionLabel = stringResource(R.string.selected_model),
    )
    UnselectButton(
        enabled = uiState.selectedModel != null,
        onUnselect = { onAction(Action.ClearSelection) },
    )
    Divider(
        modifier = Modifier.padding(vertical = 16.dp)
    )
    MeasurementInputsDemo()
}

@Composable
private fun ColumnScope.ModelInfo(info: UiState.ModelInfo?, sectionLabel: String) {
    Text(
        text = sectionLabel,
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    if (info != null) {
        OutlinedTextField(
            value = info.name ?: stringResource(R.string.value_not_specified),
            enabled = false,
            onValueChange = {},
            label = { Text(stringResource(R.string.model_name)) },
            modifier = Modifier.fillMaxWidth(),
        )
        CoordinatesInputWithSystem(
            location = info.location,
            onLocationChanged = {},
            enabled = false,
        )
    } else {
        Text(stringResource(R.string.model_not_selected))
    }
}

@Composable
private fun UnselectButton(enabled: Boolean, onUnselect: () -> Unit) {
    TextButton(
        label = stringResource(R.string.unselect_model),
        onClick = onUnselect,
        enabled = enabled,
    )
}

@Composable
private fun ColumnScope.MeasurementInputsDemo() {
    Text(
        text = "Measurement Inputs",
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    var distance by remember { mutableStateOf<Double?>(1500.0) }
    var speed by remember { mutableStateOf<Float?>(15f) }
    var altitude by remember { mutableStateOf<Double?>(120.0) }
    var azimuth by remember { mutableStateOf<Double?>(90.0) }

    DistanceInput(
        distanceMetres = distance,
        onValueChange = { distance = it },
        label = "Distance",
        modifier = Modifier.fillMaxWidth(),
    )
    SpeedInput(
        speedMps = speed,
        onValueChange = { speed = it },
        label = "Speed",
        modifier = Modifier.fillMaxWidth(),
    )
    AltitudeInput(
        altitudeMetres = altitude,
        onValueChange = { altitude = it },
        label = "Altitude",
        modifier = Modifier.fillMaxWidth(),
    )
    AngleInput(
        angleDegrees = azimuth,
        onValueChange = { azimuth = it },
        label = "Azimuth",
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun EventHandler(eventFlow: Flow<Event>) {
    val context = LocalContext.current

    LaunchedEffect(eventFlow, context) {
        eventFlow.collect { event ->
            when (event) {
                is Event.ModelUnselected -> context.showToast(R.string.model_unselected)
            }
        }
    }
}
