package vision.combat.c4.ds.sample.gallery.websocket.domain.model

/** Domain model of a reported earthquake — what the UI renders, with no trace of the transport. */
internal data class Earthquake(
    val id: String,
    val magnitude: Double,
    val magnitudeType: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val depthKm: Double,
    /** Origin time, UTC epoch milliseconds. */
    val timeEpochMillis: Long,
)
