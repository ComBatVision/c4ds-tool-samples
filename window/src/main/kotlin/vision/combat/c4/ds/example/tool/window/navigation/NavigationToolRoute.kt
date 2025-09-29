package vision.combat.c4.ds.example.tool.window.navigation

import vision.combat.c4.ds.sdk.ui.navigation.Route

internal sealed class NavigationToolRoute(override val route: String) : Route {
    data object Info : NavigationToolRoute("info")
    data object Settings : NavigationToolRoute("settings")
}
