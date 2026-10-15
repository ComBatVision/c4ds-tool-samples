package __PACKAGE__.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import __PACKAGE__.domain.repository.__TOOL__Repository

internal class __TOOL__Interactor(private val repository: __TOOL__Repository) {
    fun observeCount(scope: CoroutineScope): StateFlow<Int> = repository.observeCount(scope)

    fun increment(current: Int) = repository.setCount(current + 1)

    fun reset() = repository.setCount(0)
}
