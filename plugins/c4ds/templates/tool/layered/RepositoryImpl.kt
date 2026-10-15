package __PACKAGE__.data

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import __PACKAGE__.domain.repository.__TOOL__Repository
import vision.combat.c4.ds.sdk.data.util.observeAsStateFlow

internal class __TOOL__RepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : __TOOL__Repository {

    override fun observeCount(scope: CoroutineScope): StateFlow<Int> =
        sharedPreferences.observeAsStateFlow(KEY_COUNT, 0, scope)

    override fun setCount(count: Int) = sharedPreferences.edit { putInt(KEY_COUNT, count) }

    private companion object {
        const val KEY_COUNT = "count"
    }
}
