package vision.combat.c4.ds.sample.gallery.websocket.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import earth.worldwind.geom.Location
import earth.worldwind.geom.Position
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vision.combat.c4.ds.sample.gallery.websocket.domain.EarthquakeInteractor
import vision.combat.c4.ds.sample.gallery.websocket.domain.model.Earthquake
import vision.combat.c4.ds.sdk.domain.interactor.CommonMapInteractor
import vision.combat.c4.ds.sdk.domain.interactor.settings.CommonLocaleSettingsInteractor
import vision.combat.c4.ds.sdk.ui.util.toString
import vision.combat.c4.unit.CoordinateSystemFormat

internal class WebSocketViewModel(
    private val earthquakeInteractor: EarthquakeInteractor,
    private val localeSettingsInteractor: CommonLocaleSettingsInteractor,
    private val mapInteractor: CommonMapInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event: Flow<Event> = _event.receiveAsFlow()

    // Domain models by id, so a tap on a UI item can be resolved back to its earthquake.
    private var earthquakesById: Map<String, Earthquake> = emptyMap()

    private var feedJob: Job? = null

    // The feed is collected in viewModelScope: it stays open while the ViewModel lives and closes
    // when it is cleared.
    init {
        observeFeed()
    }

    fun handleAction(action: Action) {
        when (action) {
            Action.Retry -> observeFeed()
            is Action.ShowOnMap -> earthquakesById[action.id]?.let(::showOnMap)
        }
    }

    private fun observeFeed() {
        if (feedJob?.isActive == true) return
        _uiState.update { it.copy(feed = UiState.Feed.LOADING) }
        feedJob = viewModelScope.launch {
            try {
                earthquakeInteractor.observeRecentEarthquakes()
                    .onEach { earthquakes -> earthquakesById = earthquakes.associateBy(Earthquake::id) }
                    .combine(localeSettingsInteractor.coordinateSystemFormat) { earthquakes, format ->
                        earthquakes.toItems(format)
                    }
                    .collect { items ->
                        _uiState.update { it.copy(feed = UiState.Feed.LIVE, earthquakes = items) }
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(feed = UiState.Feed.UNAVAILABLE) }
                _event.send(Event.FeedFailed(e.message))
            }
        }
    }

    private fun showOnMap(earthquake: Earthquake) {
        mapInteractor.focusOnLocation(Location.fromDegrees(earthquake.latitude, earthquake.longitude))
    }

    private fun List<Earthquake>.toItems(format: CoordinateSystemFormat): List<UiState.EarthquakeItem> =
        map { it.toItem(format) }

    private fun Earthquake.toItem(format: CoordinateSystemFormat) = UiState.EarthquakeItem(
        id = id,
        magnitude = magnitude,
        magnitudeLabel = "%.1f".format(magnitude),
        magnitudeType = magnitudeType,
        region = region,
        timeEpochMillis = timeEpochMillis,
        depthKm = depthKm.toInt(),
        coordinates = Position.fromDegrees(latitude, longitude, 0.0).toString(format),
    )

    data class UiState(
        val feed: Feed = Feed.LOADING,
        val earthquakes: List<EarthquakeItem> = emptyList(),
    ) {
        /** State of the feed as the screen shows it: loading, live, or unavailable with Retry. */
        enum class Feed { LOADING, LIVE, UNAVAILABLE }

        data class EarthquakeItem(
            val id: String,
            val magnitude: Double,
            val magnitudeLabel: String,
            val magnitudeType: String,
            val region: String,
            val timeEpochMillis: Long,
            val depthKm: Int,
            val coordinates: String?,
        )
    }

    sealed interface Action {
        data object Retry : Action
        data class ShowOnMap(val id: String) : Action
    }

    sealed interface Event {
        data class FeedFailed(val message: String?) : Event
    }
}
