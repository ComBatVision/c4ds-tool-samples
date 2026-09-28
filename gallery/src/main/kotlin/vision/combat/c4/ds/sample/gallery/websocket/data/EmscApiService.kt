package vision.combat.c4.ds.sample.gallery.websocket.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import vision.combat.c4.ds.sample.gallery.websocket.data.body.response.EmscEventApiModel
import vision.combat.c4.ds.sample.gallery.websocket.data.body.response.EmscEventCollectionApiModel
import vision.combat.c4.ds.sample.gallery.websocket.data.body.response.EmscSocketMessageApiModel
import java.io.IOException

/**
 * Thin Ktor transport around the keyless EMSC (European-Mediterranean Seismological Centre)
 * services: an HTTP query for recent events and a WebSocket that pushes each new or revised
 * event. Both run on the same OkHttp-engine client. Error handling is the caller's job.
 */
internal class EmscApiService(
    private val clientProvider: () -> HttpClient,
    private val json: Json,
) {
    private inline val client get() = clientProvider()

    suspend fun getRecentEvents(limit: Int): List<EmscEventApiModel> =
        client.get(QUERY_URL) {
            url {
                parameters.append("format", "json")
                parameters.append("limit", limit.toString())
            }
        }.body<EmscEventCollectionApiModel>().features

    /**
     * Cold flow of pushed events. `client.webSocket` suspends for the whole session, so it runs
     * inside a `channelFlow`: collecting opens the socket, cancelling closes it. The feed is
     * meant to be endless, so both a dropped connection and a server-side close end the flow
     * with an [IOException] — the caller decides whether to reconnect. A frame that does not
     * parse is skipped rather than ending the feed.
     */
    fun liveEvents(): Flow<EmscEventApiModel> = channelFlow {
        client.webSocket(urlString = SOCKET_URL) {
            for (frame in incoming) {
                if (frame !is Frame.Text) continue
                val message = try {
                    json.decodeFromString<EmscSocketMessageApiModel>(frame.readText())
                } catch (_: SerializationException) {
                    continue
                }
                message.data?.let { this@channelFlow.send(it) }
            }
        }
        throw IOException("EMSC closed the WebSocket")
    }

    private companion object {
        private const val QUERY_URL = "https://www.seismicportal.eu/fdsnws/event/1/query"
        private const val SOCKET_URL = "wss://www.seismicportal.eu/standing_order/websocket"
    }
}
