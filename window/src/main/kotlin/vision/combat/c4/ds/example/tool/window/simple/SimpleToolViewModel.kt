package vision.combat.c4.ds.example.tool.window.simple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import earth.worldwind.geom.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vision.combat.c4.ds.sdk.domain.interactor.CommonModelInteractor
import vision.combat.c4.ds.sdk.domain.interactor.selectedModelUpdatedEvent
import vision.combat.c4.ds.sdk.domain.interactor.userModelUpdatedEvent
import vision.combat.c4.ds.sdk.domain.util.toLocation
import vision.combat.c4.model.BattlespaceConceptModel

internal class SimpleToolViewModel(
    private val modelInteractor: CommonModelInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(produceState())
    val uiState: StateFlow<UiState> = _uiState

    private val _event = MutableSharedFlow<Event>()
    val event: SharedFlow<Event> = _event.asSharedFlow()

    init {
        with(modelInteractor) {
            merge(userModel, userModelUpdatedEvent)
                .onEach { updateState { produceState(userModel = it) } }
                .launchIn(viewModelScope)

            merge(selectedModel, selectedModelUpdatedEvent)
                .onEach { updateState { produceState(selectedModel = it) } }
                .launchIn(viewModelScope)
        }
    }

    private fun produceState(
        userModel: BattlespaceConceptModel? = modelInteractor.userModel.value,
        selectedModel: BattlespaceConceptModel? = modelInteractor.selectedModel.value,
    ): UiState {
        return UiState(
            userModel = userModel?.toModelInfo(),
            selectedModel = selectedModel?.toModelInfo(),
        )
    }

    private fun BattlespaceConceptModel.toModelInfo(): UiState.ModelInfo {
        return UiState.ModelInfo(name, location.center.toLocation())
    }

    fun handleAction(action: Action) {
        when (action) {
            is Action.ClearSelection -> {
                modelInteractor.stopModelInteraction()
                emitEvent(Event.ModelUnselected)
            }
        }
    }

    private inline fun updateState(block: UiState.() -> UiState) {
        _uiState.update { block(it) }
    }

    private fun emitEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    data class UiState(
        val userModel: ModelInfo? = null,
        val selectedModel: ModelInfo? = null,
    ) {
        data class ModelInfo(
            val name: String?,
            val location: Location,
        )
    }

    sealed class Action {
        data object ClearSelection : Action()
    }

    sealed class Event {
        data object ModelUnselected : Event()
    }
}
