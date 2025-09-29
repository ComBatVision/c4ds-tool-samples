package vision.combat.c4.ds.example.tool.window.navigation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import vision.combat.c4.ds.sdk.common.util.coroutines.mapAndStateIn
import vision.combat.c4.ds.example.tool.window.navigation.data.respository.NavigationToolRepository

internal class SettingsViewModel(
    private val repository: NavigationToolRepository,
) : ViewModel() {

    val uiState: StateFlow<UiState> = repository
        .observeOpenOnTop(viewModelScope)
        .mapAndStateIn(viewModelScope) { UiState(it) }

    fun handleAction(action: Action) {
        when (action) {
            is Action.SetOpenOnTop -> {
                repository.setOpenOnTop(action.openOnTop)
            }
        }
    }

    data class UiState(
        val openOnTop: Boolean = false,
    )

    sealed interface Action {
        @JvmInline
        value class SetOpenOnTop(val openOnTop: Boolean) : Action
    }
}
