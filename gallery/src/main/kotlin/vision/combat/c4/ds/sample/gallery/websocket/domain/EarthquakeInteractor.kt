package vision.combat.c4.ds.sample.gallery.websocket.domain

import kotlinx.coroutines.flow.Flow
import vision.combat.c4.ds.sample.gallery.websocket.domain.model.Earthquake
import vision.combat.c4.ds.sample.gallery.websocket.domain.repository.EarthquakeRepository

/** Domain entry point for the live earthquake feed; the transport stays behind [EarthquakeRepository]. */
internal class EarthquakeInteractor(
    private val repository: EarthquakeRepository,
) {
    fun observeRecentEarthquakes(): Flow<List<Earthquake>> = repository.observeRecentEarthquakes()
}
