package __PACKAGE__.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import __PACKAGE__.domain.__TOOL__Interactor

internal class __TOOL__ViewModel(
    private val interactor: __TOOL__Interactor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event: Flow<Event> = _event.receiveAsFlow()

    init {
        observeCount()
    }

    fun handleAction(action: Action) {
        when (action) {
            is Action.Increment -> interactor.increment(_uiState.value.count)
            is Action.Reset -> {
                interactor.reset()
                emitEvent(Event.CounterReset)
            }
        }
    }

    private fun observeCount() {
        interactor.observeCount(viewModelScope)
            .onEach { count -> _uiState.update { it.copy(count = count) } }
            .launchIn(viewModelScope)
    }

    private fun emitEvent(event: Event) {
        viewModelScope.launch { _event.send(event) }
    }

    data class UiState(val count: Int = 0)

    sealed interface Action {
        data object Increment : Action
        data object Reset : Action
    }

    sealed interface Event {
        data object CounterReset : Event
    }
}
