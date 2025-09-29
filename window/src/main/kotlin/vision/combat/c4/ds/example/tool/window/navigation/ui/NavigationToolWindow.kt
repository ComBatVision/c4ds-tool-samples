package vision.combat.c4.ds.example.tool.window.navigation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vision.combat.c4.ds.sdk.ui.navigation.AppNavHost
import vision.combat.c4.ds.example.tool.window.navigation.NavigationToolRoute
import vision.combat.c4.ds.example.tool.window.navigation.ui.info.InfoScreen
import vision.combat.c4.ds.example.tool.window.navigation.ui.settings.SettingsScreen

@Composable
internal fun NavigationToolWindow() {
    val navController = rememberNavController()
    val startDestination = NavigationToolRoute.Info.route

    AppNavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavigationToolRoute.Info.route) {
            InfoScreen(
                navigateToSettings = {
                    navController.navigate(NavigationToolRoute.Settings.route)
                },
            )
        }

        composable(NavigationToolRoute.Settings.route) {
            SettingsScreen()
        }
    }
}
