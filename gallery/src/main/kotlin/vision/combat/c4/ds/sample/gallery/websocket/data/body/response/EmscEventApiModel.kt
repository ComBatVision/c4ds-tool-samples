package vision.combat.c4.ds.sample.gallery.websocket.data.body.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An EMSC earthquake as a GeoJSON feature — the same shape in the FDSN query response and in
 * WebSocket messages. Only the fields the sample renders; `ignoreUnknownKeys` drops the rest.
 */
@Serializable
internal data class EmscEventApiModel(
    @SerialName("id") val id: String? = null,
    @SerialName("properties") val properties: Properties? = null,
) {
    @Serializable
    internal data class Properties(
        @SerialName("time") val time: String? = null,
        @SerialName("flynn_region") val region: String? = null,
        @SerialName("lat") val latitude: Double? = null,
        @SerialName("lon") val longitude: Double? = null,
        @SerialName("depth") val depthKm: Double? = null,
        @SerialName("mag") val magnitude: Double? = null,
        @SerialName("magtype") val magnitudeType: String? = null,
    )
}

/** Response body of the EMSC FDSN `event/1/query?format=json` endpoint. */
@Serializable
internal data class EmscEventCollectionApiModel(
    @SerialName("features") val features: List<EmscEventApiModel> = emptyList(),
)

/**
 * One text frame from the EMSC WebSocket. [action] is `create` for a new event and `update` when
 * an agency revises one (magnitude, location); both carry the full event in [data].
 */
@Serializable
internal data class EmscSocketMessageApiModel(
    @SerialName("action") val action: String? = null,
    @SerialName("data") val data: EmscEventApiModel? = null,
)
