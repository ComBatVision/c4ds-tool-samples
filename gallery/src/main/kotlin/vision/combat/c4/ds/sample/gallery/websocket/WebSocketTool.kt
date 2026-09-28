package vision.combat.c4.ds.sample.gallery.websocket

import earth.worldwind.geom.AltitudeMode
import earth.worldwind.geom.Offset
import earth.worldwind.geom.Position
import earth.worldwind.shape.Placemark
import earth.worldwind.shape.PlacemarkAttributes
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.subDI
import vision.combat.c4.ds.sample.gallery.websocket.di.webSocketModule
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketViewModel.UiState.EarthquakeItem
import vision.combat.c4.ds.sample.gallery.websocket.ui.WebSocketWindow
import vision.combat.c4.ds.sample.gallery.websocket.ui.map.RippleImages
import vision.combat.c4.ds.sdk.domain.interactor.CommonMapInteractor
import vision.combat.c4.ds.sdk.tool.AbstractMapTool
import vision.combat.c4.ds.sdk.tool.ToolComponent
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams
import vision.combat.c4.ds.sdk.tool.requiredComponent

/**
 * [AbstractMapTool] subclass for the live earthquake feed: imports [webSocketModule] (which binds
 * the tool's own OkHttp-engine `HttpClient`), wires [WebSocketWindow] as the single window
 * component, and draws the earthquakes on the map as ripple placemarks in its renderable layer.
 *
 * The tool is the map view of [WebSocketViewModel]: it gets the same ViewModel instance the window
 * uses (both live in the tool's `ViewModelStore`) and draws the earthquakes from its UI state, so
 * the feed is collected once and the ViewModel never references the tool. Observation runs in the
 * tool's `scope`, which is cancelled with the tool; the renderable layer goes away with it.
 */
internal class WebSocketTool(
    toolContext: ToolContext,
    toolDescriptor: ToolDescriptor,
    parentDI: DI,
    params: ToolParams?,
) : AbstractMapTool(toolContext, toolDescriptor, parentDI, params) {

    override val di: DI = subDI(super.di) { import(webSocketModule) }

    override val window: ToolComponent.Window by requiredComponent {
        WebSocketWindow()
    }

    // Same key and store as diViewModel() in the window, so this is the window's ViewModel.
    private val viewModel: WebSocketViewModel by lazy {
        ViewModelProvider(this)[WebSocketViewModel::class.java]
    }
    private val mapInteractor: CommonMapInteractor by instance()

    private val rippleImages = RippleImages()
    private val markers = mutableMapOf<String, Marker>()

    init {
        viewModel.uiState
            .map { it.earthquakes }
            .distinctUntilChanged()
            .onEach(::showEarthquakes)
            .launchIn(scope)
    }

    /**
     * Syncs the map to [earthquakes] by id: markers for earthquakes that left the list are
     * removed, and only new or revised earthquakes are redrawn.
     */
    private fun showEarthquakes(earthquakes: List<EarthquakeItem>) {
        val ids = earthquakes.mapTo(HashSet(), EarthquakeItem::id)
        markers.keys.filterNot(ids::contains).forEach { id ->
            markers.remove(id)?.let { removeRenderable(it.placemark) }
        }

        earthquakes.forEach { earthquake ->
            val current = markers[earthquake.id]
            if (current?.earthquake == earthquake) return@forEach
            current?.let { removeRenderable(it.placemark) }
            val placemark = earthquake.toPlacemark()
            markers[earthquake.id] = Marker(earthquake, placemark)
            addRenderable(placemark)
        }
        mapInteractor.requestRedraw()
    }

    private fun EarthquakeItem.toPlacemark(): Placemark =
        Placemark(
            Position.fromDegrees(latitude, longitude, 0.0),
            PlacemarkAttributes.createWithImage(rippleImages.forMagnitude(magnitude)).apply {
                imageOffset = Offset.center()
            },
            "M$magnitudeLabel",
        ).apply {
            altitudeMode = AltitudeMode.CLAMP_TO_GROUND
            // Display only: taps on the map keep their default host behavior.
            isPickEnabled = false
        }

    private class Marker(val earthquake: EarthquakeItem, val placemark: Placemark)
}
