package vision.combat.c4.ds.example.tool.window.navigation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sdk.ui.component.WindowContentDefaults.VerticalPadding
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel
import vision.combat.c4.ds.example.tool.window.navigation.ui.settings.SettingsViewModel.Action
import vision.combat.c4.ds.example.tool.window.navigation.ui.settings.SettingsViewModel.UiState

@Composable
internal fun SettingsScreen(viewModel: SettingsViewModel = diViewModel<SettingsViewModel>()) {
    val uiState by viewModel.uiState.collectAsState()

    ScreenContent(uiState, viewModel::handleAction)
}

@Composable
private fun ScreenContent(
    uiState: UiState,
    onAction: (Action) -> Unit,
) {
    WindowScaffold(
        topAppBar = { TopAppBar() },
        contentPaddingValues = PaddingValues(0.dp, VerticalPadding),
        content = { Content(uiState, onAction) },
    )
}

@Composable
private fun TopAppBar() {
    BackNavTopAppBar("Settings")
}

@Composable
private fun Content(
    uiState: UiState,
    onAction: (Action) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onAction(Action.SetOpenOnTop(!uiState.openOnTop)) }
            .heightIn(min = 44.dp)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Open tool on top",
                style = MaterialTheme.typography.subtitle1,
            )
            Text(
                text = "If selected, the activated tool will be opened on top of the current one",
                style = MaterialTheme.typography.caption,
                color = LocalContentColor.current.copy(ContentAlpha.disabled)
            )
        }

        Switch(
            checked = uiState.openOnTop,
            onCheckedChange = { onAction(Action.SetOpenOnTop(it)) },
        )
    }
}
