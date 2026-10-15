package __PACKAGE__.di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.bindSingletonOf
import org.kodein.di.instance
import __PACKAGE__.__TOOL__ToolDescriptor
import __PACKAGE__.data.__TOOL__RepositoryImpl
import __PACKAGE__.domain.__TOOL__Interactor
import __PACKAGE__.domain.repository.__TOOL__Repository
import vision.combat.c4.ds.sdk.tool.requireQualifiedName

internal val __TOOL_CAMEL__Module = DI.Module("__TOOL_CAMEL__Module") {
    // Plugin-isolated SharedPreferences, keyed by the descriptor's qualified name.
    bindSingleton<__TOOL__Repository> {
        __TOOL__RepositoryImpl(instance(arg = requireQualifiedName<__TOOL__ToolDescriptor>()))
    }

    bindSingletonOf(::__TOOL__Interactor)
}
