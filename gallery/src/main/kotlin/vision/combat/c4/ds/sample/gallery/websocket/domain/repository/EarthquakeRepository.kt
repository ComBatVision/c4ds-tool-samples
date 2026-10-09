package vision.combat.c4.ds.sample.gallery.websocket.domain.repository

import kotlinx.coroutines.flow.Flow
import vision.combat.c4.ds.sample.gallery.websocket.domain.model.Earthquake

/**
 * Domain-facing contract for the live earthquake feed. How the list is kept current — an HTTP
 * request for the backlog, a WebSocket for what happens next, reconnects — is a data-layer
 * detail; nothing above it knows a socket is involved.
 */
internal interface EarthquakeRepository {
    /**
     * Cold flow of the most recent earthquakes, newest first. Emits the recent backlog first, then
     * a new list each time an earthquake is reported or revised. Collecting keeps the feed open;
     * cancelling closes it. Throws when the feed cannot be (re)established.
     */
    fun observeRecentEarthquakes(): Flow<List<Earthquake>>
}
