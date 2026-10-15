package __PACKAGE__.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import __NAMESPACE__.R
import __PACKAGE__.ui.__TOOL__ViewModel.Action
import __PACKAGE__.ui.__TOOL__ViewModel.Event
import __PACKAGE__.ui.__TOOL__ViewModel.UiState
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.component.button.Button
import vision.combat.c4.ds.sdk.ui.component.button.OutlinedButton
import vision.combat.c4.ds.sdk.ui.util.showToast
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel

@Composable
internal fun __TOOL__Window(viewModel: __TOOL__ViewModel = diViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WindowContent(uiState = uiState, onAction = viewModel::handleAction)
    EventHandler(eventFlow = viewModel.event)
}

@Composable
private fun WindowContent(uiState: UiState, onAction: (Action) -> Unit) {
    WindowScaffold(
        topAppBar = { BackNavTopAppBar(title = stringResource(R.string.__TOOL_SNAKE___tool_name)) },
        content = { Content(uiState, onAction) },
    )
}

@Composable
private fun ColumnScope.Content(uiState: UiState, onAction: (Action) -> Unit) {
    Text(
        text = stringResource(R.string.__TOOL_SNAKE___count, uiState.count),
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(bottom = 16.dp),
    )
    Row {
        Button(
            label = stringResource(R.string.__TOOL_SNAKE___increment),
            onClick = { onAction(Action.Increment) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            label = stringResource(R.string.__TOOL_SNAKE___reset),
            onClick = { onAction(Action.Reset) },
        )
    }
}

@Composable
private fun EventHandler(eventFlow: Flow<Event>) {
    val context = LocalContext.current
    val resetMessage = stringResource(R.string.__TOOL_SNAKE___reset_toast)
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is Event.CounterReset -> context.showToast(resetMessage)
            }
        }
    }
}
