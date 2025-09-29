package vision.combat.c4.ds.example.tool.window.navigation.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import vision.combat.c4.ds.sdk.tool.ToolId
import vision.combat.c4.ds.example.tool.window.navigation.data.respository.NavigationToolRepository

internal class InfoViewModel(
    private val repository: NavigationToolRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    fun handleAction(action: Action) {
        when (action) {
            is Action.ActivateTool -> {
                emitEvent(Event.ToolActivationRequested(action.toolId, repository.getOpenOnTop()))
            }
        }
    }

    private fun emitEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    sealed interface Action {
        data class ActivateTool(val toolId: ToolId) : Action
    }

    sealed interface Event {
        data class ToolActivationRequested(val toolId: ToolId, val onTop: Boolean) : Event
    }
}
