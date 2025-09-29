package vision.combat.c4.ds.example.tool.window.navigation.di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import vision.combat.c4.ds.example.tool.window.navigation.NavigationToolDescriptor
import vision.combat.c4.ds.example.tool.window.navigation.data.respository.NavigationToolRepository

internal val navigationToolModule = DI.Module("navigationToolModule") {
    bindSingleton {
        NavigationToolRepository(instance(arg = NavigationToolDescriptor.ID))
    }
}
