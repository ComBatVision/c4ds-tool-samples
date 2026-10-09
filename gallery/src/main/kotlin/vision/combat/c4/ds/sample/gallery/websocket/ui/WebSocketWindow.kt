package vision.combat.c4.ds.sample.gallery.websocket.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel.Action
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel.Event
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel.UiState
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel.UiState.EarthquakeItem
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel.UiState.Feed
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.component.button.Button
import vision.combat.c4.ds.sdk.ui.component.list.ListItem
import vision.combat.c4.ds.sdk.ui.component.list.ListItemDefaults
import vision.combat.c4.ds.sdk.ui.util.showToast
import vision.combat.c4.ds.sdk.ui.viewmodel.diViewModel

@Composable
internal fun WebSocketWindow(viewModel: WebSocketViewModel = diViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WindowContent(uiState = uiState, onAction = viewModel::handleAction)
    EventHandler(eventFlow = viewModel.event)
}

@Composable
private fun WindowContent(uiState: UiState, onAction: (Action) -> Unit) {
    WindowScaffold(
        topAppBar = { BackNavTopAppBar(title = stringResource(R.string.websocket_tool_name)) },
        // Edge-to-edge list: no scaffold padding or spacing, so ListItems and their dividers
        // span the window. Text blocks add their own padding.
        contentPaddingValues = PaddingValues(0.dp),
        contentVerticalArrangement = Arrangement.Top,
        content = { Content(uiState, onAction) },
    )
}

@Composable
private fun ColumnScope.Content(uiState: UiState, onAction: (Action) -> Unit) {
    Column(modifier = Modifier.padding(TextPadding)) {
        Text(
            text = stringResource(R.string.websocket_explainer),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        FeedStatus(feed = uiState.feed, onRetry = { onAction(Action.Retry) })
    }

    // Relative times ("5 minutes ago") are recomputed every minute, not only when data arrives.
    val now by produceState(System.currentTimeMillis()) {
        while (true) {
            delay(DateUtils.MINUTE_IN_MILLIS)
            value = System.currentTimeMillis()
        }
    }
    // WindowScaffold content already scrolls and the list is capped, so a plain Column is
    // enough — a LazyColumn here would nest two vertical scrolls.
    if (uiState.earthquakes.isNotEmpty()) Divider()
    uiState.earthquakes.forEach { item ->
        EarthquakeListItem(
            item = item,
            now = now,
            onClick = { onAction(Action.ShowOnMap(item.id)) },
        )
    }

    if (uiState.earthquakes.isNotEmpty()) {
        Text(
            text = stringResource(R.string.websocket_attribution),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(TextPadding),
        )
    }
}

@Composable
private fun FeedStatus(feed: Feed, onRetry: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (feed) {
            Feed.LOADING -> {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.websocket_status_loading), style = MaterialTheme.typography.body2)
            }
            Feed.LIVE -> {
                Box(modifier = Modifier.size(10.dp).background(LiveColor, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.websocket_status_live), style = MaterialTheme.typography.body2)
            }
            Feed.UNAVAILABLE -> Column {
                Text(
                    text = stringResource(R.string.websocket_status_unavailable),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Button(label = stringResource(R.string.websocket_retry), onClick = onRetry)
            }
        }
    }
}

@Composable
private fun EarthquakeListItem(
    item: EarthquakeItem,
    now: Long,
    onClick: () -> Unit,
) {
    ListItem(
        headline = {
            Text(
                text = item.region,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface,
            )
        },
        supportingText = {
            Column {
                Text(
                    text = stringResource(R.string.websocket_depth_scale, item.depthKm, item.magnitudeType),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface,
                )
                item.coordinates?.let { coordinates ->
                    Text(
                        text = coordinates,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    )
                }
            }
        },
        leadingIcon = { MagnitudeBadge(item) },
        contentTrailingTop = {
            Text(
                text = DateUtils.getRelativeTimeSpanString(
                    item.timeEpochMillis,
                    now,
                    DateUtils.MINUTE_IN_MILLIS,
                ).toString(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            )
        },
        // The whole row is the action (show on map), so no forward chevron.
        onItemClick = onClick,
        canGoForward = false,
    )
}

@Composable
private fun MagnitudeBadge(item: EarthquakeItem) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(ListItemDefaults.LeadingIconSize)
            .background(magnitudeColor(item.magnitude), CircleShape),
    ) {
        Text(
            text = item.magnitudeLabel,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

private val LiveColor = Color(0xFF43A047)

// Matches the SDK ListItem's horizontal padding so text lines up with the rows.
private val TextPadding = PaddingValues(horizontal = ListItemDefaults.HorizontalPadding, vertical = 16.dp)

@Composable
private fun EventHandler(eventFlow: Flow<Event>) {
    val context = LocalContext.current
    val feedFailed = stringResource(R.string.websocket_error_feed)
    val unknownError = stringResource(R.string.websocket_error_unknown)
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is Event.FeedFailed ->
                    context.showToast("$feedFailed ${event.message ?: unknownError}")
            }
        }
    }
}
