package vision.combat.c4.ds.sample.gallery.websocket.ui.map

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import earth.worldwind.render.image.ImageSource
import vision.combat.c4.ds.sample.gallery.websocket.ui.magnitudeColor
import android.graphics.Color as AndroidColor

/**
 * Marker images for earthquake placemarks: a seismic "ripple" in the severity color — the same
 * colors as the list badges — with a white-outlined dot and two concentric rings fading
 * outwards. Sized by magnitude in dp so it reads the same on any screen density, and cached per
 * color and size, as there are only a few.
 */
internal class RippleImages {
    private val images = mutableMapOf<Pair<Int, Int>, ImageSource>()
    private val density = Resources.getSystem().displayMetrics.density

    fun forMagnitude(magnitude: Double): ImageSource {
        val color = magnitudeColor(magnitude).toArgb()
        val sizePx = (sizeDp(magnitude) * density).toInt()
        return images.getOrPut(color to sizePx) { ImageSource.fromBitmap(draw(color, sizePx)) }
    }

    private fun draw(color: Int, sizePx: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val center = sizePx / 2f
        val stroke = RING_STROKE_DP * density
        val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = stroke
            this.color = color
        }
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = AndroidColor.WHITE }
        Canvas(bitmap).apply {
            RINGS.forEach { (radiusFraction, alpha) ->
                ring.alpha = alpha
                drawCircle(center, center, center * radiusFraction - stroke / 2, ring)
            }
            val dotRadius = center * DOT_FRACTION
            drawCircle(center, center, dotRadius, outline)
            drawCircle(center, center, dotRadius - stroke, fill)
        }
        return bitmap
    }

    private companion object {
        private const val RING_STROKE_DP = 2f
        private const val DOT_FRACTION = 0.3f

        /** Ring radius as a fraction of the marker radius, and its alpha (0-255): inner ring stronger. */
        private val RINGS = listOf(0.65f to 235, 1f to 150)

        // Bigger earthquakes get bigger ripples: M2 is 32 dp, M6 is 56 dp.
        fun sizeDp(magnitude: Double): Float = (20 + magnitude * 6).toFloat().coerceIn(28f, 64f)
    }
}
