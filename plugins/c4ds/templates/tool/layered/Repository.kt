package __PACKAGE__.domain.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal interface __TOOL__Repository {
    fun observeCount(scope: CoroutineScope): StateFlow<Int>
    fun setCount(count: Int)
}
