package vision.combat.c4.ds.example.tool.window.navigation.data.respository

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import vision.combat.c4.ds.sdk.data.util.observeAsStateFlow

internal class NavigationToolRepository(
    private val sharedPreferences: SharedPreferences,
) {
    fun getOpenOnTop(): Boolean {
        return sharedPreferences.getBoolean(SHOULD_OPEN_ON_TOP_KEY, false)
    }

    fun setOpenOnTop(shouldOpenOnTop: Boolean) {
        sharedPreferences.edit { putBoolean(SHOULD_OPEN_ON_TOP_KEY, shouldOpenOnTop) }
    }

    fun observeOpenOnTop(scope: CoroutineScope): StateFlow<Boolean> {
        return sharedPreferences.observeAsStateFlow<Boolean>(SHOULD_OPEN_ON_TOP_KEY, false, scope)
    }

    private companion object {
        private const val SHOULD_OPEN_ON_TOP_KEY = "should_open_on_top"
    }
}
