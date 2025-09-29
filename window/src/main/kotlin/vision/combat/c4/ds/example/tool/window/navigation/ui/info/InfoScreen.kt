package vision.combat.c4.ds.example.tool.window.navigation.ui.info

import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import org.kodein.di.compose.rememberInstance
import vision.combat.c4.ds.sdk.tool.ToolManager
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.component.button.Button
import vision.combat.c4.ds.sdk.ui.component.button.IconButton
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel
import vision.combat.c4.ds.example.tool.window.navigation.ui.info.InfoViewModel.Action
import vision.combat.c4.ds.example.tool.window.simple.SimpleToolDescriptor

@Composable
internal fun InfoScreen(
    navigateToSettings: () -> Unit,
) {
    val viewModel = diViewModel<InfoViewModel>()

    ScreenContent(
        onAction = viewModel::handleAction,
        navigateToSettings = navigateToSettings,
    )

    EventHandler(
        eventFlow = viewModel.event,
    )
}

@Composable
private fun ScreenContent(
    onAction: (Action) -> Unit,
    navigateToSettings: () -> Unit,
) {
    WindowScaffold(
        topAppBar = { TopAppBar(navigateToSettings) },
        content = { Content(onAction) },
    )
}

@Composable
private fun TopAppBar(
    navigateToSettings: () -> Unit,
) {
    BackNavTopAppBar("Info") {
        IconButton(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            onClick = navigateToSettings,
        )
    }
}

@Composable
private fun Content(
    onAction: (Action) -> Unit,
) {
    Text("Tool info screen content")
    Button(
        label = "Open Simple Tool",
        onClick = { onAction(Action.ActivateTool(SimpleToolDescriptor.ID)) },
    )
}

@Composable
private fun EventHandler(
    eventFlow: Flow<InfoViewModel.Event>,
) {
    val toolManager by rememberInstance<ToolManager>()

    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is InfoViewModel.Event.ToolActivationRequested -> {
                    val flags = if (event.onTop) ToolManager.FLAG_COMPONENT_ON_TOP else ToolManager.FLAG_NONE
                    toolManager.activate(event.toolId, flags)
                }
            }
        }
    }
}
