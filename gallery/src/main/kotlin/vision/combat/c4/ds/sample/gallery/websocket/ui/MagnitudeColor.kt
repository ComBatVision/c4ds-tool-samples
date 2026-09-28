package vision.combat.c4.ds.sample.gallery.websocket.ui

import androidx.compose.ui.graphics.Color

/** Rough severity scale shared by the list badge and the map marker: minor, light, moderate, strong. */
internal fun magnitudeColor(magnitude: Double): Color = when {
    magnitude < 3.0 -> Color(0xFF607D8B)
    magnitude < 4.5 -> Color(0xFFF9A825)
    magnitude < 6.0 -> Color(0xFFEF6C00)
    else -> Color(0xFFC62828)
}
