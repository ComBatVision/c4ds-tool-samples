package vision.combat.c4.ds.sample.gallery.websocket.data

import vision.combat.c4.ds.sample.gallery.websocket.data.body.response.EmscEventApiModel
import vision.combat.c4.ds.sample.gallery.websocket.domain.model.Earthquake
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Maps an EMSC event DTO to the domain model at the data boundary. Returns null for an event
 * missing anything the domain model requires, so a malformed frame is skipped, not fatal.
 */
internal fun EmscEventApiModel.toDomainOrNull(): Earthquake? {
    val props = properties ?: return null
    return Earthquake(
        id = id ?: return null,
        magnitude = props.magnitude ?: return null,
        magnitudeType = props.magnitudeType.orEmpty(),
        region = props.region?.toTitleCase() ?: return null,
        latitude = props.latitude ?: return null,
        longitude = props.longitude ?: return null,
        depthKm = props.depthKm ?: 0.0,
        timeEpochMillis = props.time?.toEpochMillisOrNull() ?: return null,
    )
}

// kotlin.time.Instant, not java.time: a java.time reference is desugared into j$.time classes
// in the plugin APK, which collide with the host's own desugared copy.
@OptIn(ExperimentalTime::class)
private fun String.toEpochMillisOrNull(): Long? =
    Instant.parseOrNull(this)?.toEpochMilliseconds()

/**
 * EMSC sends Flinn-Engdahl region names in upper case ("NEAR THE COAST OF WESTERN TURKEY").
 * Title-cases them, keeping short joining words lower case after the first word.
 */
private fun String.toTitleCase(): String =
    lowercase().split(' ').mapIndexed { index, word ->
        if (index > 0 && word in LOWERCASE_WORDS) word else word.replaceFirstChar(Char::titlecase)
    }.joinToString(" ")

private val LOWERCASE_WORDS = setOf("of", "the", "and")
