package vision.combat.c4.ds.sample.gallery.mapview.mapinteractor.ui.detail

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.domain.interactor.CommonMapInteractor
import vision.combat.c4.ds.sdk.domain.model.MapDisplayMode
import vision.combat.c4.ds.sdk.ui.component.IntegerStepper
import vision.combat.c4.ds.sdk.ui.component.SegmentedButtonItem
import vision.combat.c4.ds.sdk.ui.component.SegmentedButtonRow
import vision.combat.c4.ds.sdk.ui.component.checkable.SwitchField
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel

/**
 * Requests a camera view for the map — [MapDisplayMode] AR or VR — via
 * [CommonMapInteractor.requestCameraViewMode]. In AR mode an [IntegerStepper] tunes the
 * AR distance limit; [SwitchField]s toggle the reticle and overall map visibility.
 *
 * A tool asks for a camera view, it does not set the display mode: the host owns the map surface
 * and applies the request while the tool shows a [ToolComponent.Underlay] over the map (see the
 * Underlay showcase). With no underlay the map stays [MapDisplayMode.Normal] and the request is
 * simply remembered. Passing `null` withdraws it.
 *
 * The showcase ViewModel resets these settings to a neutral state in [ViewModel.onCleared].
 */
@Composable
internal fun ColumnScope.DisplayModeShowcase(viewModel: DisplayModeViewModel = diViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Text(
        text = stringResource(R.string.map_sc_display_explainer),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onSurface,
    )

    Divider(modifier = Modifier.padding(vertical = 8.dp))

    SectionLabel(stringResource(R.string.map_sc_display_mode_section))
    val displayModes = listOf(
        SegmentedButtonItem(MapDisplayMode.Normal, stringResource(R.string.map_sc_mode_normal)),
        SegmentedButtonItem(MapDisplayMode.AR, stringResource(R.string.map_sc_mode_ar)),
        SegmentedButtonItem(MapDisplayMode.VR, stringResource(R.string.map_sc_mode_vr)),
    )
    SegmentedButtonRow(
        items = displayModes,
        selected = state.displayMode,
        onSelected = viewModel::setDisplayMode,
        modifier = Modifier.fillMaxWidth(),
    )
    if (state.displayMode == MapDisplayMode.AR) {
        Spacer(modifier = Modifier.height(4.dp))
        IntegerStepper(
            value = state.arDistanceLimit.toInt(),
            label = stringResource(R.string.map_sc_ar_distance),
            valueRange = 500..20000,
            smallStep = 500,
            largeStep = 2000,
            onValueChange = { viewModel.setArDistanceLimit(it.toDouble()) },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Divider(modifier = Modifier.padding(vertical = 8.dp))

    SectionLabel(stringResource(R.string.map_sc_visibility_section))
    SwitchField(
        initialValue = state.isReticleVisible,
        label = stringResource(R.string.map_sc_reticle),
        onCheckedChange = viewModel::setReticleVisible,
    )
    SwitchField(
        initialValue = state.isMapVisible,
        label = stringResource(R.string.map_sc_map_visible),
        onCheckedChange = viewModel::setMapVisible,
    )
}

internal class DisplayModeViewModel(
    private val mapInteractor: CommonMapInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        mapInteractor.mapDisplayMode
            .onEach { mode -> _uiState.update { it.copy(displayMode = mode) } }
            .launchIn(viewModelScope)

        mapInteractor.arDistanceLimit
            .onEach { dist -> _uiState.update { it.copy(arDistanceLimit = dist) } }
            .launchIn(viewModelScope)

        mapInteractor.isReticleVisible
            .onEach { v -> _uiState.update { it.copy(isReticleVisible = v) } }
            .launchIn(viewModelScope)

        mapInteractor.isMapVisible
            .onEach { v -> _uiState.update { it.copy(isMapVisible = v) } }
            .launchIn(viewModelScope)
    }

    fun setDisplayMode(mode: MapDisplayMode) {
        // Normal is not a camera view — asking for it means withdrawing the request.
        mapInteractor.requestCameraViewMode(mode.takeIf { it != MapDisplayMode.Normal })
    }

    fun setArDistanceLimit(distanceM: Double) {
        mapInteractor.setArDistanceLimit(distanceM)
    }

    fun setReticleVisible(visible: Boolean) {
        mapInteractor.setReticleVisible(visible)
    }

    fun setMapVisible(visible: Boolean) {
        mapInteractor.setMapVisible(visible)
    }

    override fun onCleared() {
        // Leave the map in a neutral state when this showcase is closed.
        mapInteractor.requestCameraViewMode(null)
        mapInteractor.setMapVisible(true)
        mapInteractor.setReticleVisible(false)
        super.onCleared()
    }

    data class UiState(
        val displayMode: MapDisplayMode = MapDisplayMode.Normal,
        val arDistanceLimit: Double = 3000.0,
        val isReticleVisible: Boolean = false,
        val isMapVisible: Boolean = true,
    )
}
