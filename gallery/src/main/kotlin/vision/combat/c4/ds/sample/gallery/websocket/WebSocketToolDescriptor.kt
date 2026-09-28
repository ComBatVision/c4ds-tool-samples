package vision.combat.c4.ds.sample.gallery.websocket

import org.kodein.di.DI
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams

/**
 * A live earthquake feed over a WebSocket, with the Ktor client the host already provides.
 *
 * WebSockets need a socket-capable engine. The SDK's shared client and the Network Requests
 * sample use `HttpClient(Android)`, which is `HttpURLConnection`-based and cannot upgrade a
 * connection — `client.webSocket(...)` on it fails at runtime. This tool binds its own
 * untagged [io.ktor.client.HttpClient] on the **OkHttp** engine instead; the engine, OkHttp and
 * the `WebSockets` plugin all come with the SDK (0.6.1+), so nothing is bundled into the APK.
 *
 * Layering is the point of the sample. The data layer loads recent events from the EMSC FDSN
 * endpoint over HTTP, then keeps the list current from the EMSC WebSocket, merging revisions by
 * id and reconnecting with backoff. The domain exposes only `Flow<List<Earthquake>>`; frames,
 * sockets and reconnects never leave the data layer, and loading/unavailable are UI state. The
 * UI renders earthquakes in a list and as ripple placemarks on the map (the tool is an
 * [vision.combat.c4.ds.sdk.tool.AbstractMapTool]), and moves the host map to the one the user
 * taps.
 *
 * SDK APIs demonstrated:
 *   - HttpClient(OkHttp) + WebSockets + ContentNegotiation (host-provided Ktor)
 *   - AbstractMapTool.addRenderable / removeRenderable with WorldWind Placemark
 *   - CommonMapInteractor.focusOnLocation
 *   - CommonLocaleSettingsInteractor.coordinateSystemFormat
 *
 * SDK files:
 *   c4ds-sdk-core/data/build.gradle.kts (Ktor engines re-exported to plugins)
 *   c4ds-sdk/proguard-rules.pro (keeps io.ktor.** and okhttp3.** names in the host)
 */
class WebSocketToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    override val nameResId: Int = R.string.websocket_tool_name
    override val iconResId: Int = R.drawable.ic_websocket
    override val categories: List<String> = emptyList()

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool {
        return WebSocketTool(toolContext, this, di, params)
    }
}
