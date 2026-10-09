package vision.combat.c4.ds.sample.gallery.websocket.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import vision.combat.c4.ds.sample.gallery.websocket.domain.model.Earthquake
import vision.combat.c4.ds.sample.gallery.websocket.domain.repository.EarthquakeRepository
import java.io.IOException

/**
 * Keeps a list of recent earthquakes current: the HTTP backlog first, so the list is not empty
 * while waiting for the next event, then every event the WebSocket pushes, merged by id (EMSC
 * revises events after the first report). A dropped socket is reopened with exponential backoff;
 * the socket, its frames and reconnects never leave this class.
 */
internal class EarthquakeRepositoryImpl(
    private val emscApiService: EmscApiService,
) : EarthquakeRepository {

    override fun observeRecentEarthquakes(): Flow<List<Earthquake>> = flow {
        var earthquakes = emscApiService.getRecentEvents(MAX_EARTHQUAKES)
            .mapNotNull { it.toDomainOrNull() }
            .sortedByDescending(Earthquake::timeEpochMillis)
        emit(earthquakes)

        liveEarthquakes().collect { earthquake ->
            earthquakes = (earthquakes.filterNot { it.id == earthquake.id } + earthquake)
                .sortedByDescending(Earthquake::timeEpochMillis)
                .take(MAX_EARTHQUAKES)
            emit(earthquakes)
        }
    }.flowOn(Dispatchers.IO)

    // Reconnects on IOException with exponential backoff. The failure count resets whenever an
    // event arrives, so a long-lived feed survives any number of drops spread over time, while a
    // server that stays unreachable fails after MAX_RETRIES attempts.
    private fun liveEarthquakes(): Flow<Earthquake> {
        var failures = 0
        return emscApiService.liveEvents()
            .onEach { failures = 0 }
            .retryWhen { cause, _ ->
                if (cause !is IOException || failures >= MAX_RETRIES) return@retryWhen false
                delay(INITIAL_BACKOFF_MILLIS shl failures)
                failures++
                true
            }
            .mapNotNull { it.toDomainOrNull() }
    }

    private companion object {
        private const val MAX_EARTHQUAKES = 30
        private const val MAX_RETRIES = 5
        private const val INITIAL_BACKOFF_MILLIS = 1_000L
    }
}
