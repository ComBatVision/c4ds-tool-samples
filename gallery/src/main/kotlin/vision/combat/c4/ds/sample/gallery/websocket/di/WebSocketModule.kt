package vision.combat.c4.ds.sample.gallery.websocket.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.instance
import org.kodein.di.provider
import vision.combat.c4.ds.sample.gallery.websocket.data.EarthquakeRepositoryImpl
import vision.combat.c4.ds.sample.gallery.websocket.data.EmscApiService
import vision.combat.c4.ds.sample.gallery.websocket.domain.EarthquakeInteractor
import vision.combat.c4.ds.sample.gallery.websocket.domain.repository.EarthquakeRepository

internal val webSocketModule = DI.Module("webSocketModule") {
    bindSingleton { Json { ignoreUnknownKeys = true } }

    // The tool's own Ktor client, on the OkHttp engine: the Android engine cannot open
    // WebSockets. One client serves both the HTTP backlog request and the WebSocket. Bound
    // untagged — the SDK keeps that slot free for plugins (its shared client is under
    // SdkRemoteTags.HTTP_CLIENT). Every Ktor/OkHttp class is host-provided.
    bindSingleton {
        val json: Json = instance()
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }
            install(WebSockets) {
                // Keeps the idle feed alive through NATs and detects a dead peer.
                pingIntervalMillis = PING_INTERVAL_MILLIS
            }
        }
    }

    // HttpClient creation is heavy, so the service takes a provider and resolves the client
    // lazily on first use — the same shape as the Network Requests sample.
    bindSingleton { EmscApiService(provider(), instance()) }

    bindSingleton<EarthquakeRepository> { EarthquakeRepositoryImpl(instance()) }

    bindSingletonOf(::EarthquakeInteractor)
}

private const val PING_INTERVAL_MILLIS = 15_000L
