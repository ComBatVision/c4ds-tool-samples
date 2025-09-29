package vision.combat.c4.ds.example.tool.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vision.combat.c4.ds.example.tool.overlay.OverlayToolViewModel.UiState
import vision.combat.c4.ds.sdk.ui.theme.mediumOverlay
import vision.combat.c4.ds.sdk.ui.theme.primaryOverlay
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel
import vision.combat.c4.ds.tool.sample.overlay.R

@Composable
internal fun Overlay(viewModel: OverlayToolViewModel = diViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OverlayContent(uiState)
}

@Composable
private fun OverlayContent(uiState: UiState) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .background(MaterialTheme.colors.primaryOverlay, shape = MaterialTheme.shapes.mediumOverlay)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tool),
                contentDescription = "Example icon",
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = stringResource(R.string.overlay_tool_example_title),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.overlay_tool_example_description),
                textAlign = TextAlign.Center,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.overlay_tool_user_location))
                Text(uiState.userLocation ?: stringResource(R.string.overlay_tool_unknown_location))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.overlay_tool_selected_location))
                Text(uiState.selectedLocation ?: stringResource(R.string.overlay_tool_unknown_location))
            }
        }
    }
}
