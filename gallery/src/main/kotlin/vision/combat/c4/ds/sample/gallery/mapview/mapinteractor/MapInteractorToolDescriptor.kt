package vision.combat.c4.ds.sample.gallery.mapview.mapinteractor

import org.kodein.di.DI
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.tool.AbstractTool
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolDescriptor
import vision.combat.c4.ds.sdk.tool.ToolParams

/**
 * Demonstrates CommonMapInteractor: live camera readout, display mode, reticle, cursor pin,
 * focusOnLocation/Sector, and magnetic corrections.
 *
 * SDK APIs: CommonMapInteractor, mapNavigatorEvent, camera, lookAt, selectedPosition,
 *           isLookAtAboveHorizon, mapDisplayMode, requestCameraViewMode, arDistanceLimit,
 *           setArDistanceLimit, isReticleVisible, setReticleVisible, isCursorPinned,
 *           pinCursor, unpinCursor, isMapVisible, setMapVisible, focusOnLocation, focusOnSector, getDeclination,
 *           getConvergence, getAngleCorrection.
 *
 * SDK file: c4ds-sdk-core/domain/src/commonMain/.../interactor/CommonMapInteractor.kt
 */
class MapInteractorToolDescriptor(toolContext: ToolContext) : ToolDescriptor(toolContext) {
    override val nameResId: Int = R.string.map_interactor_tool_name
    override val iconResId: Int = R.drawable.ic_map_interactor
    override val categories: List<String> = emptyList()

    override fun createTool(toolContext: ToolContext, di: DI, params: ToolParams?): AbstractTool {
        return MapInteractorTool(toolContext, this, di, params)
    }
}
